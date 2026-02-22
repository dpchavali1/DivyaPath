package com.divyapath.app.audio;

import android.speech.tts.TextToSpeech;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Sanskrit/Hindi pronunciation lexicon for improved TTS devotional authenticity.
 *
 * Generic Android TTS often mispronounces compound Sanskrit words, conjuncts,
 * and visarga/anusvara patterns. This lexicon provides phonetic hints by
 * substituting problematic words with pronunciation-friendly equivalents.
 *
 * Approach: Pre-process text before sending to TTS, replacing known
 * mispronounced words with phonetically spelled-out versions that
 * guide TTS to more accurate pronunciation.
 */
public class SanskritPronunciationLexicon {

    // Map of problematic words/patterns → phonetic replacements
    // These help TTS pronounce Sanskrit/Hindi devotional terms correctly
    private static final Map<String, String> LEXICON = new LinkedHashMap<>();

    static {
        // Deity names and epithets
        LEXICON.put("विघ्नहर्ता", "विघन हरता");
        LEXICON.put("विघ्नेश्वर", "विघनेश्वर");
        LEXICON.put("गजानन", "गजा नन");
        LEXICON.put("वक्रतुण्ड", "वक्र तुंड");
        LEXICON.put("एकदन्त", "एक दंत");
        LEXICON.put("लम्बोदर", "लम्बो दर");
        LEXICON.put("महाकाल", "महा काल");
        LEXICON.put("त्रिशूलधारी", "त्रिशूल धारी");
        LEXICON.put("नीलकण्ठ", "नील कंठ");
        LEXICON.put("चन्द्रशेखर", "चंद्र शेखर");
        LEXICON.put("पार्वतीनन्दन", "पार्वती नंदन");
        LEXICON.put("रघुनन्दन", "रघु नंदन");
        LEXICON.put("दशरथनन्दन", "दशरथ नंदन");
        LEXICON.put("जगन्नाथ", "जगन नाथ");
        LEXICON.put("विश्वनाथ", "विश्व नाथ");
        LEXICON.put("श्रीरामचन्द्र", "श्री रामचंद्र");
        LEXICON.put("महादेवा", "महा देवा");
        LEXICON.put("लक्ष्मीनारायण", "लक्ष्मी नारायण");
        LEXICON.put("सत्यनारायण", "सत्य नारायण");

        // Common mispronounced conjuncts
        LEXICON.put("कृपा", "कृ पा");
        LEXICON.put("भक्ति", "भक ती");
        LEXICON.put("मुक्ति", "मुक ती");
        LEXICON.put("शक्ति", "शक ती");
        LEXICON.put("युक्ति", "युक ती");
        LEXICON.put("सृष्टि", "सृष्टि");

        // Visarga and anusvara corrections
        LEXICON.put("दुःख", "दुख");
        LEXICON.put("अतः", "अतह");
        LEXICON.put("नमः", "नमह");

        // Chalisa-specific terms
        LEXICON.put("बज्रांगी", "बजरंगी");
        LEXICON.put("बिक्रम", "विक्रम");
        LEXICON.put("कपीस", "कपीश");
        LEXICON.put("तिहुँ", "तीहूं");
        LEXICON.put("बरनउँ", "बरनऊं");
        LEXICON.put("सुमिरौं", "सुमिरों");
        LEXICON.put("कीन्हीं", "कीन्हीं");
        LEXICON.put("सँहारे", "संहारे");
        LEXICON.put("सँवारे", "संवारे");

        // Aarti-specific terms
        LEXICON.put("ॐ", "ओम");
        LEXICON.put("ओ३म्", "ओम");
        LEXICON.put("ऊँ", "ओम");
    }

    /**
     * Process text through the pronunciation lexicon before TTS.
     * Replaces known mispronounced words with phonetically friendly versions.
     *
     * @param text original Hindi/Sanskrit text
     * @return processed text with pronunciation hints
     */
    public static String process(String text) {
        if (text == null || text.isEmpty()) return text;

        String processed = text;
        for (Map.Entry<String, String> entry : LEXICON.entrySet()) {
            processed = processed.replace(entry.getKey(), entry.getValue());
        }
        return processed;
    }

    // Cloud TTS handles Sanskrit better than Android TTS, so fewer substitutions needed.
    // Only deity names and sacred syllables need special handling via SSML phoneme hints.
    private static final Map<String, String> CLOUD_TTS_LEXICON = new LinkedHashMap<>();

    static {
        // Cloud TTS: only handle sacred syllables and anusvara/visarga
        CLOUD_TTS_LEXICON.put("ॐ", "ओम");
        CLOUD_TTS_LEXICON.put("ओ३म्", "ओम");
        CLOUD_TTS_LEXICON.put("ऊँ", "ओम");
        CLOUD_TTS_LEXICON.put("नमः", "नमह");
        CLOUD_TTS_LEXICON.put("अतः", "अतह");
        CLOUD_TTS_LEXICON.put("दुःख", "दुख");
    }

    /**
     * Process text for Cloud TTS. Cloud TTS handles Hindi/Sanskrit much better
     * than Android TTS, so minimal substitutions are needed — mainly sacred
     * syllables and visarga corrections.
     *
     * @param text original Hindi/Sanskrit text
     * @return processed text suitable for Cloud TTS SSML input
     */
    public static String processForCloudTts(String text) {
        if (text == null || text.isEmpty()) return text;

        String processed = text;
        for (Map.Entry<String, String> entry : CLOUD_TTS_LEXICON.entrySet()) {
            processed = processed.replace(entry.getKey(), entry.getValue());
        }
        return processed;
    }

    /**
     * Get the lexicon size for debugging/display.
     */
    public static int getLexiconSize() {
        return LEXICON.size();
    }

    /**
     * Add a custom pronunciation override at runtime.
     */
    public static void addEntry(String original, String phonetic) {
        if (original != null && phonetic != null) {
            LEXICON.put(original, phonetic);
        }
    }
}
