package com.divyapath.app.audio;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Converts plain devotional text into rich SSML markup for Edge TTS.
 *
 * Produces natural, warm-sounding devotional speech by applying:
 *   - Voice wrapping (Edge TTS requires voice name in SSML)
 *   - Prosody control (slower rate, slightly lower pitch for gravitas)
 *   - Paragraph/sentence structure for natural breathing
 *   - Smart breaks at verse boundaries, section headers, stanza ends
 *   - Emphasis on deity names for reverent delivery
 *   - Content-type aware profiles (aarti, chalisa, mantra, stotra)
 */
public class SsmlDevotionalBuilder {

    // Content type profiles
    public static final String TYPE_AARTI = "aarti";
    public static final String TYPE_CHALISA = "chalisa";
    public static final String TYPE_MANTRA = "mantra";
    public static final String TYPE_STOTRA = "stotra";
    public static final String TYPE_DEFAULT = "default";

    // Break durations
    private static final int BREAK_SECTION_HEADER_MS = 1200;
    private static final int BREAK_VERSE_END_MS = 800;
    private static final int BREAK_STANZA_MS = 600;
    private static final int BREAK_NORMAL_MS = 500;
    private static final int BREAK_REFRAIN_MS = 300;

    // Deity names to emphasize
    private static final Set<String> DEITY_NAMES = new HashSet<>(Arrays.asList(
            "गणेश", "शिव", "विष्णु", "राम", "कृष्ण", "हनुमान",
            "दुर्गा", "लक्ष्मी", "सरस्वती", "पार्वती", "काली",
            "महादेव", "भोलेनाथ", "शंकर", "नारायण", "हरि",
            "गोविन्द", "गोपाल", "मुरारी", "ब्रह्मा", "गायत्री",
            "सीता", "राधा", "जानकी", "भगवान", "ईश्वर",
            "महाकाल", "नीलकण्ठ", "जगन्नाथ", "बजरंगबली",
            "गजानन", "विनायक", "सत्यनारायण", "श्रीराम"
    ));

    // Section header patterns
    private static final Set<String> SECTION_HEADERS = new HashSet<>(Arrays.asList(
            "दोहा", "चौपाई", "सोरठा", "छन्द", "अर्धाली"
    ));

    private String contentType = TYPE_DEFAULT;
    private String voiceName = EdgeTtsClient.VOICE_FEMALE;
    private String rateValue = "-15%";   // Edge uses relative percentages like "-15%"
    private String pitchValue = "-5%";   // Edge uses relative percentages like "-5%"

    public SsmlDevotionalBuilder() {}

    /**
     * Set the content type for profile-specific prosody.
     */
    public SsmlDevotionalBuilder setContentType(@Nullable String contentType) {
        this.contentType = contentType != null ? contentType : TYPE_DEFAULT;
        applyContentTypeProfile();
        return this;
    }

    /**
     * Set the Edge TTS voice name.
     */
    public SsmlDevotionalBuilder setVoiceName(@NonNull String voiceName) {
        this.voiceName = voiceName;
        return this;
    }

    /**
     * Override the speaking rate (e.g. "-15%", "-25%", "+10%").
     */
    public SsmlDevotionalBuilder setRate(String rateValue) {
        this.rateValue = rateValue;
        return this;
    }

    /**
     * Override the pitch adjustment (e.g. "-5%", "-10%", "+0%").
     */
    public SsmlDevotionalBuilder setPitch(String pitchValue) {
        this.pitchValue = pitchValue;
        return this;
    }

    /**
     * Build SSML from a full devotional text (multiple lines).
     */
    @NonNull
    public String buildFullText(@NonNull String text) {
        String[] lines = text.split("\n");
        return buildFromLines(lines);
    }

    /**
     * Build SSML from an array of lines.
     */
    @NonNull
    public String buildFromLines(@NonNull String[] lines) {
        StringBuilder body = new StringBuilder();

        boolean inParagraph = false;
        String lastRefrain = null;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) {
                if (inParagraph) {
                    body.append("    </p>\n");
                    inParagraph = false;
                }
                body.append(String.format("    <break time=\"%dms\"/>\n", BREAK_STANZA_MS));
                continue;
            }

            // Skip pure punctuation/number lines
            if (line.matches("^[॥।\\|\\s\\d०-९]+$")) {
                body.append(String.format("    <break time=\"%dms\"/>\n", BREAK_VERSE_END_MS));
                continue;
            }

            if (!inParagraph) {
                body.append("    <p>\n");
                inParagraph = true;
            }

            if (isSectionHeader(line)) {
                body.append(String.format("      <s>%s</s>\n", escapeXml(line)));
                body.append(String.format("      <break time=\"%dms\"/>\n", BREAK_SECTION_HEADER_MS));
                body.append("    </p>\n");
                inParagraph = false;
            } else if (isVerseEndMarker(line)) {
                body.append(String.format("      <break time=\"%dms\"/>\n", BREAK_VERSE_END_MS));
                body.append("    </p>\n");
                inParagraph = false;
            } else {
                String processedLine = applyEmphasis(line);
                body.append(String.format("      <s>%s</s>\n", processedLine));

                int breakMs = getBreakAfterLine(line, lines, i, lastRefrain);
                if (breakMs > 0) {
                    body.append(String.format("      <break time=\"%dms\"/>\n", breakMs));
                }

                if (i < 3) {
                    lastRefrain = line;
                }
            }
        }

        if (inParagraph) {
            body.append("    </p>\n");
        }

        return wrapWithVoice(body.toString());
    }

    /**
     * Build SSML for a single line (used for line-by-line playback).
     */
    @NonNull
    public String buildSingleLine(@NonNull String line) {
        String trimmed = line.trim();
        if (trimmed.isEmpty()) {
            return wrapWithVoice("    <break time=\"500ms\"/>\n");
        }

        String processedLine = applyEmphasis(trimmed);
        return wrapWithVoice("    <s>" + processedLine + "</s>\n");
    }

    // --- Internal ---

    /**
     * Wraps body content with Edge TTS SSML envelope: speak > voice > prosody.
     */
    private String wrapWithVoice(String bodyContent) {
        return "<speak version=\"1.0\" xmlns=\"http://www.w3.org/2001/10/synthesis\" xml:lang=\"hi-IN\">\n"
                + "  <voice name=\"" + voiceName + "\">\n"
                + "  <prosody rate=\"" + rateValue + "\" pitch=\"" + pitchValue + "\" volume=\"loud\">\n"
                + bodyContent
                + "  </prosody>\n"
                + "  </voice>\n"
                + "</speak>";
    }

    private void applyContentTypeProfile() {
        switch (contentType) {
            case TYPE_AARTI:
                rateValue = "-10%";    // Slightly faster, energetic
                pitchValue = "-3%";
                break;
            case TYPE_CHALISA:
                rateValue = "-15%";    // Measured, steady
                pitchValue = "-5%";
                break;
            case TYPE_MANTRA:
                rateValue = "-25%";    // Very slow, meditative
                pitchValue = "-8%";
                break;
            case TYPE_STOTRA:
                rateValue = "-12%";    // Flowing, melodic
                pitchValue = "-3%";
                break;
            default:
                rateValue = "-15%";
                pitchValue = "-5%";
                break;
        }
    }

    private boolean isSectionHeader(String line) {
        String stripped = line.replaceAll("[॥।\\|\\s]", "").trim();
        for (String header : SECTION_HEADERS) {
            if (stripped.equalsIgnoreCase(header)) return true;
        }
        return stripped.equalsIgnoreCase("Doha") || stripped.equalsIgnoreCase("Chaupai");
    }

    private boolean isVerseEndMarker(String line) {
        return line.matches(".*॥\\s*\\d+\\s*॥\\s*$") ||
                line.matches("^[॥।\\|\\s\\d०-९]+$");
    }

    private int getBreakAfterLine(String line, String[] lines, int index, String lastRefrain) {
        String nextLine = (index + 1 < lines.length) ? lines[index + 1].trim() : null;

        if (nextLine != null && isSectionHeader(nextLine)) return BREAK_SECTION_HEADER_MS;
        if (line.endsWith("॥") || line.endsWith("।।")) return BREAK_STANZA_MS;
        if (lastRefrain != null && line.equals(lastRefrain)) return BREAK_REFRAIN_MS;

        return BREAK_NORMAL_MS;
    }

    /**
     * Wrap deity names found in the line with SSML emphasis tags.
     */
    private String applyEmphasis(String line) {
        String escaped = escapeXml(line);
        for (String deity : DEITY_NAMES) {
            if (escaped.contains(deity)) {
                escaped = escaped.replace(deity,
                        "<emphasis level=\"moderate\">" + deity + "</emphasis>");
            }
        }
        return escaped;
    }

    private String escapeXml(String text) {
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
