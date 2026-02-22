package com.divyapath.app.utils;

import com.divyapath.app.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class PanchangInsightEngine {

    public static final int TYPE_DO = 0;
    public static final int TYPE_AVOID = 1;

    public static class PanchangInsight {
        public final int iconRes;
        public final String actionText;
        public final String explanation;
        public final String timeWindow;
        public final int type;

        public PanchangInsight(int iconRes, String actionText, String explanation, String timeWindow, int type) {
            this.iconRes = iconRes;
            this.actionText = actionText;
            this.explanation = explanation;
            this.timeWindow = timeWindow;
            this.type = type;
        }
    }

    public static class InsightResult {
        public final List<PanchangInsight> doInsights;
        public final List<PanchangInsight> avoidInsights;

        public InsightResult(List<PanchangInsight> doInsights, List<PanchangInsight> avoidInsights) {
            this.doInsights = doInsights;
            this.avoidInsights = avoidInsights;
        }

        public List<PanchangInsight> getCombined(int maxDo, int maxAvoid) {
            List<PanchangInsight> combined = new ArrayList<>();
            for (int i = 0; i < Math.min(maxDo, doInsights.size()); i++) combined.add(doInsights.get(i));
            for (int i = 0; i < Math.min(maxAvoid, avoidInsights.size()); i++) combined.add(avoidInsights.get(i));
            return combined;
        }
    }

    public static InsightResult generateInsights(Map<String, String> panchang, Calendar now) {
        List<PanchangInsight> doList = new ArrayList<>();
        List<PanchangInsight> avoidList = new ArrayList<>();

        if (panchang == null) return new InsightResult(doList, avoidList);

        String tithi = panchang.get("tithi");
        String nakshatra = panchang.get("nakshatra");
        String vara = panchang.get("vara");
        String yoga = panchang.get("yoga");
        String rahukaal = panchang.get("rahukaal");
        String abhijit = panchang.get("abhijit_muhurat");
        String brahmaMuhurat = panchang.get("brahma_muhurat");
        String gulikaal = panchang.get("gulikaal");

        int currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE);

        // Time-windowed rules (highest priority — currently active)
        if (isInTimeWindow(rahukaal, currentMinutes)) {
            avoidList.add(new PanchangInsight(R.drawable.ic_om_symbol,
                    "Avoid starting new ventures",
                    "Rahukaal is active — not ideal for new beginnings or important decisions",
                    rahukaal, TYPE_AVOID));
        }

        if (isInTimeWindow(abhijit, currentMinutes)) {
            doList.add(0, new PanchangInsight(R.drawable.ic_om_symbol,
                    "Best time for important work",
                    "Abhijit Muhurat is active — most auspicious time of the day",
                    abhijit, TYPE_DO));
        }

        if (isInTimeWindow(brahmaMuhurat, currentMinutes)) {
            doList.add(0, new PanchangInsight(R.drawable.ic_om_symbol,
                    "Meditate & pray",
                    "Brahma Muhurat — the divine hour for spiritual practice",
                    brahmaMuhurat, TYPE_DO));
        }

        if (isInTimeWindow(gulikaal, currentMinutes)) {
            avoidList.add(new PanchangInsight(R.drawable.ic_om_symbol,
                    "Avoid financial transactions",
                    "Gulikaal is active — not favorable for monetary dealings",
                    gulikaal, TYPE_AVOID));
        }

        // Tithi-based rules
        if ("Ekadashi".equals(tithi)) {
            doList.add(new PanchangInsight(R.drawable.ic_om_symbol,
                    "Observe Ekadashi fast",
                    "Chant Vishnu mantras and avoid grains today",
                    "All day", TYPE_DO));
        } else if ("Purnima".equals(tithi)) {
            doList.add(new PanchangInsight(R.drawable.ic_om_symbol,
                    "Give charity & donations",
                    "Purnima — full moon day is excellent for daan and satvik activities",
                    "All day", TYPE_DO));
        } else if ("Chaturthi".equals(tithi)) {
            doList.add(new PanchangInsight(R.drawable.ic_om_symbol,
                    "Worship Lord Ganesha",
                    "Chaturthi is dedicated to Ganesha — offer durva grass and modak",
                    "All day", TYPE_DO));
        } else if ("Ashtami".equals(tithi)) {
            doList.add(new PanchangInsight(R.drawable.ic_om_symbol,
                    "Worship Goddess Durga",
                    "Ashtami is auspicious for Devi worship and havan",
                    "All day", TYPE_DO));
        }

        // Check for Amavasya (not in the standard 15 tithis array but checking Purnima cycle)
        // The 15th tithi is Purnima, so we check for "Pratipada" after dark fortnight
        if (tithi != null && tithi.contains("Amavasya")) {
            avoidList.add(new PanchangInsight(R.drawable.ic_om_symbol,
                    "Avoid new projects",
                    "Amavasya — perform pitru tarpan and ancestor remembrance instead",
                    "All day", TYPE_AVOID));
            doList.add(new PanchangInsight(R.drawable.ic_om_symbol,
                    "Perform Pitru Tarpan",
                    "New moon day — offer water and prayers for ancestors",
                    "All day", TYPE_DO));
        }

        // Vara-based rules
        if ("Monday".equals(vara)) {
            doList.add(new PanchangInsight(R.drawable.ic_om_symbol,
                    "Worship Lord Shiva",
                    "Monday is Shiva's day — chant Om Namah Shivaya and offer water to Shivling",
                    "All day", TYPE_DO));
        } else if ("Tuesday".equals(vara)) {
            doList.add(new PanchangInsight(R.drawable.ic_om_symbol,
                    "Recite Hanuman Chalisa",
                    "Tuesday is Hanuman's day — visit temple and offer sindoor",
                    "All day", TYPE_DO));
        } else if ("Wednesday".equals(vara)) {
            doList.add(new PanchangInsight(R.drawable.ic_om_symbol,
                    "Worship Lord Ganesha",
                    "Wednesday is Budh-var — offer durva grass and green items",
                    "All day", TYPE_DO));
        } else if ("Thursday".equals(vara)) {
            doList.add(new PanchangInsight(R.drawable.ic_om_symbol,
                    "Visit Guru or temple",
                    "Thursday (Guruvar) — seek blessings, offer yellow items to Vishnu",
                    "All day", TYPE_DO));
        } else if ("Friday".equals(vara)) {
            doList.add(new PanchangInsight(R.drawable.ic_om_symbol,
                    "Worship Goddess Lakshmi",
                    "Friday (Shukravar) — light diya, offer white flowers for prosperity",
                    "All day", TYPE_DO));
        } else if ("Saturday".equals(vara)) {
            doList.add(new PanchangInsight(R.drawable.ic_om_symbol,
                    "Help the needy",
                    "Saturday (Shanivar) — donate oil, black items, and serve the poor",
                    "All day", TYPE_DO));
            avoidList.add(new PanchangInsight(R.drawable.ic_om_symbol,
                    "Avoid buying iron/oil",
                    "Shanivar — not auspicious for purchasing iron or oil for self",
                    "All day", TYPE_AVOID));
        } else if ("Sunday".equals(vara)) {
            doList.add(new PanchangInsight(R.drawable.ic_om_symbol,
                    "Offer water to Surya",
                    "Sunday (Ravivar) — offer arghya to the Sun God at sunrise",
                    "Morning", TYPE_DO));
        }

        // Nakshatra-based rules
        if (nakshatra != null) {
            switch (nakshatra) {
                case "Rohini": case "Pushya": case "Shravana": case "Hasta":
                    doList.add(new PanchangInsight(R.drawable.ic_om_symbol,
                            "Good for purchases",
                            nakshatra + " nakshatra — auspicious for buying, investing, and new ventures",
                            "All day", TYPE_DO));
                    break;
                case "Mula": case "Ardra": case "Ashlesha":
                    avoidList.add(new PanchangInsight(R.drawable.ic_om_symbol,
                            "Avoid long journeys",
                            nakshatra + " nakshatra — not ideal for travel or starting new work",
                            "All day", TYPE_AVOID));
                    break;
                case "Revati": case "Anuradha": case "Magha":
                    doList.add(new PanchangInsight(R.drawable.ic_om_symbol,
                            "Good for spiritual practices",
                            nakshatra + " nakshatra — excellent for puja, meditation, and devotion",
                            "All day", TYPE_DO));
                    break;
            }
        }

        // Yoga-based rules
        if (yoga != null) {
            switch (yoga) {
                case "Siddhi": case "Shubha": case "Shiva": case "Siddha":
                    doList.add(new PanchangInsight(R.drawable.ic_om_symbol,
                            "Auspicious for ceremonies",
                            yoga + " yoga — favorable for religious ceremonies and important events",
                            "All day", TYPE_DO));
                    break;
                case "Vyatipata": case "Vaidhriti":
                    avoidList.add(new PanchangInsight(R.drawable.ic_om_symbol,
                            "Avoid auspicious ceremonies",
                            yoga + " yoga — inauspicious for weddings, griha pravesh, and new beginnings",
                            "All day", TYPE_AVOID));
                    break;
                case "Vishkumbha": case "Ganda": case "Vyaghata":
                    avoidList.add(new PanchangInsight(R.drawable.ic_om_symbol,
                            "Exercise caution today",
                            yoga + " yoga — be mindful in decisions and avoid risky ventures",
                            "All day", TYPE_AVOID));
                    break;
            }
        }

        return new InsightResult(doList, avoidList);
    }

    private static boolean isInTimeWindow(String timeRange, int currentMinutes) {
        if (timeRange == null || timeRange.isEmpty() || "--".equals(timeRange)
                || "N/A".equals(timeRange)) return false;
        try {
            // Format: "HH:MM AM - HH:MM PM" or "HH:MM AM/PM - HH:MM AM/PM"
            String[] parts = timeRange.split("\\s*-\\s*");
            if (parts.length != 2) return false;
            int start = parseTimeToMinutes(parts[0].trim());
            int end = parseTimeToMinutes(parts[1].trim());
            return currentMinutes >= start && currentMinutes <= end;
        } catch (Exception e) {
            return false;
        }
    }

    private static int parseTimeToMinutes(String time) {
        // Parse "HH:MM AM/PM" or "HH:MM AM" format
        time = time.toUpperCase().trim();
        boolean pm = time.contains("PM");
        time = time.replace("AM", "").replace("PM", "").trim();
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0].trim());
        int minutes = Integer.parseInt(parts[1].trim());
        if (pm && hours != 12) hours += 12;
        if (!pm && hours == 12) hours = 0;
        return hours * 60 + minutes;
    }
}
