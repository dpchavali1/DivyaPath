package com.divyapath.app.utils;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;

/**
 * Loads shlokas from assets/shlokas.json and returns the shloka-of-the-day
 * based on the day of the year (rotating through all 50).
 */
public class ShlokaLoader {

    private static final String TAG = "ShlokaLoader";
    private static final String ASSET_FILE = "shlokas.json";

    public static class Shloka {
        public final int id;
        public final int chapter;
        public final int verse;
        public final String sanskrit;
        public final String hindiMeaning;
        public final String englishMeaning;
        public final String source;

        public Shloka(int id, int chapter, int verse, String sanskrit,
                      String hindiMeaning, String englishMeaning, String source) {
            this.id = id;
            this.chapter = chapter;
            this.verse = verse;
            this.sanskrit = sanskrit;
            this.hindiMeaning = hindiMeaning;
            this.englishMeaning = englishMeaning;
            this.source = source;
        }
    }

    /**
     * Returns today's shloka by rotating through all available shlokas
     * based on the day of the year.
     */
    public static Shloka getTodaysShloka(Context context) {
        try {
            String json = loadJsonFromAsset(context);
            if (json == null) return getDefaultShloka();

            JSONObject root = new JSONObject(json);
            JSONArray shlokas = root.getJSONArray("shlokas");
            if (shlokas.length() == 0) return getDefaultShloka();

            int dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
            int index = dayOfYear % shlokas.length();

            JSONObject s = shlokas.getJSONObject(index);
            return new Shloka(
                    s.optInt("id", index + 1),
                    s.optInt("chapter", 0),
                    s.optInt("verse", 0),
                    s.optString("sanskrit", ""),
                    s.optString("hindiMeaning", ""),
                    s.optString("englishMeaning", ""),
                    s.optString("source", "Bhagavad Gita")
            );
        } catch (Exception e) {
            Log.e(TAG, "Error loading shloka", e);
            return getDefaultShloka();
        }
    }

    /**
     * Returns a shloka by specific index (0-based).
     */
    public static Shloka getShlokaByIndex(Context context, int index) {
        try {
            String json = loadJsonFromAsset(context);
            if (json == null) return getDefaultShloka();

            JSONObject root = new JSONObject(json);
            JSONArray shlokas = root.getJSONArray("shlokas");
            if (index < 0 || index >= shlokas.length()) return getDefaultShloka();

            JSONObject s = shlokas.getJSONObject(index);
            return new Shloka(
                    s.optInt("id", index + 1),
                    s.optInt("chapter", 0),
                    s.optInt("verse", 0),
                    s.optString("sanskrit", ""),
                    s.optString("hindiMeaning", ""),
                    s.optString("englishMeaning", ""),
                    s.optString("source", "Bhagavad Gita")
            );
        } catch (Exception e) {
            Log.e(TAG, "Error loading shloka by index", e);
            return getDefaultShloka();
        }
    }

    /**
     * Returns total number of shlokas available.
     */
    public static int getShlokaCount(Context context) {
        try {
            String json = loadJsonFromAsset(context);
            if (json == null) return 0;
            JSONObject root = new JSONObject(json);
            return root.getJSONArray("shlokas").length();
        } catch (Exception e) {
            return 0;
        }
    }

    private static String loadJsonFromAsset(Context context) {
        try {
            InputStream is = context.getAssets().open(ASSET_FILE);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (Exception e) {
            Log.e(TAG, "Could not load " + ASSET_FILE, e);
            return null;
        }
    }

    private static Shloka getDefaultShloka() {
        return new Shloka(1, 2, 47,
                "कर्मण्येवाधिकारस्ते मा फलेषु कदाचन ।\nमा कर्मफलहेतुर्भूर्मा ते सङ्गोऽस्त्वकर्मणि ॥",
                "तुम्हारा कर्म करने में ही अधिकार है, फल में कभी नहीं। कर्मफल की इच्छा कभी तुम्हारा उद्देश्य न हो और अकर्म में भी तुम्हारी आसक्ति न हो।",
                "You have the right to perform your duty, but never to the fruits of your actions.",
                "Bhagavad Gita 2.47");
    }
}
