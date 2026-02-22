package com.divyapath.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class PreferenceManager {
    public static final String VOICE_FEMALE = "female";
    public static final String VOICE_MALE = "male";

    public static final String AARTI_PLAYBACK_AUTO = "auto";
    public static final String AARTI_PLAYBACK_SING = "sing";
    public static final String AARTI_PLAYBACK_READ = "read";

    private final SharedPreferences prefs;

    public PreferenceManager(Context ctx) {
        prefs = ctx.getSharedPreferences("divyapath_prefs", Context.MODE_PRIVATE);
    }

    public SharedPreferences getSharedPreferences() { return prefs; }

    public String getUserName() { return prefs.getString("user_name", "Bhakt"); }
    public void setUserName(String n) { prefs.edit().putString("user_name", n).apply(); }

    public String getLanguage() { return prefs.getString("language", "hi"); }
    public void setLanguage(String l) { prefs.edit().putString("language", l).apply(); }

    public boolean isMorningNotificationEnabled() { return prefs.getBoolean("morning_notif", true); }
    public void setMorningNotification(boolean e) { prefs.edit().putBoolean("morning_notif", e).apply(); }

    public boolean isEveningNotificationEnabled() { return prefs.getBoolean("evening_notif", false); }
    public void setEveningNotification(boolean e) { prefs.edit().putBoolean("evening_notif", e).apply(); }

    public int getNotificationHour() { return prefs.getInt("notif_hour", 6); }
    public int getNotificationMinute() { return prefs.getInt("notif_minute", 0); }
    public void setNotificationTime(int h, int m) {
        prefs.edit().putInt("notif_hour", h).putInt("notif_minute", m).apply();
    }

    public float getFontSize() { return prefs.getFloat("font_size", 18f); }
    public void setFontSize(float s) { prefs.edit().putFloat("font_size", s).apply(); }

    public String getTheme() { return prefs.getString("theme", "light"); }
    public void setTheme(String t) { prefs.edit().putString("theme", t).apply(); }

    public boolean isFirstLaunch() { return prefs.getBoolean("first_launch", true); }
    public void setFirstLaunch(boolean f) { prefs.edit().putBoolean("first_launch", f).apply(); }

    public int getAdCounter() { return prefs.getInt("ad_counter", 0); }
    public void incrementAdCounter() { prefs.edit().putInt("ad_counter", getAdCounter() + 1).apply(); }
    public void resetAdCounter() { prefs.edit().putInt("ad_counter", 0).apply(); }

    public String getVoiceGender() {
        String gender = prefs.getString("voice_gender", VOICE_FEMALE);
        if (VOICE_MALE.equals(gender)) {
            return VOICE_MALE;
        }
        return VOICE_FEMALE;
    }

    public void setVoiceGender(String g) {
        String value = VOICE_MALE.equals(g) ? VOICE_MALE : VOICE_FEMALE;
        prefs.edit().putString("voice_gender", value).apply();
    }

    // Location for Panchang
    public double getLocationLat() { return Double.longBitsToDouble(prefs.getLong("loc_lat", Double.doubleToLongBits(28.6139))); }
    public double getLocationLon() { return Double.longBitsToDouble(prefs.getLong("loc_lon", Double.doubleToLongBits(77.2090))); }
    public void setLocation(double lat, double lon) {
        prefs.edit().putLong("loc_lat", Double.doubleToRawLongBits(lat))
                    .putLong("loc_lon", Double.doubleToRawLongBits(lon)).apply();
    }
    public String getLocationName() { return prefs.getString("loc_name", "New Delhi"); }
    public void setLocationName(String n) { prefs.edit().putString("loc_name", n).apply(); }

    // Location mode: "gps", "city", "manual"
    public String getLocationMode() { return prefs.getString("location_mode", "city"); }
    public void setLocationMode(String mode) { prefs.edit().putString("location_mode", mode).apply(); }

    public String getLocationTimezone() { return prefs.getString("loc_timezone", "Asia/Kolkata"); }
    public void setLocationTimezone(String tz) { prefs.edit().putString("loc_timezone", tz).apply(); }

    public String getLocationCountryCode() { return prefs.getString("loc_country_code", "IN"); }
    public void setLocationCountryCode(String cc) { prefs.edit().putString("loc_country_code", cc).apply(); }

    // Timezone display: "local", "ist", "device"
    public String getTimezoneDisplay() { return prefs.getString("timezone_display", "local"); }
    public void setTimezoneDisplay(String td) { prefs.edit().putString("timezone_display", td).apply(); }

    public String getEffectiveTimezone() {
        String timezoneDisplay = getTimezoneDisplay();
        if ("ist".equals(timezoneDisplay)) {
            return "Asia/Kolkata";
        }
        if ("device".equals(timezoneDisplay)) {
            return TimeZone.getDefault().getID();
        }
        return getLocationTimezone();
    }

    public void setFullLocation(String name, String countryCode, double lat, double lon, String timezone) {
        prefs.edit()
            .putString("loc_name", name)
            .putString("loc_country_code", countryCode)
            .putLong("loc_lat", Double.doubleToRawLongBits(lat))
            .putLong("loc_lon", Double.doubleToRawLongBits(lon))
            .putString("loc_timezone", timezone)
            .apply();
    }

    // Voice speed for TTS (0.5 to 1.5)
    public float getVoiceSpeed() { return prefs.getFloat("voice_speed", 0.85f); }
    public void setVoiceSpeed(float s) { prefs.edit().putFloat("voice_speed", s).apply(); }

    // Playback speed (0.75 to 1.5)
    public float getPlaybackSpeed() { return prefs.getFloat("playback_speed", 1.0f); }
    public void setPlaybackSpeed(float s) { prefs.edit().putFloat("playback_speed", s).apply(); }

    // Repeat mode: 0=off, 1=one, 2=all
    public int getRepeatMode() { return prefs.getInt("repeat_mode", 0); }
    public void setRepeatMode(int m) { prefs.edit().putInt("repeat_mode", m).apply(); }

    // Audio quality
    public String getAudioQuality() { return prefs.getString("audio_quality", "medium"); }
    public void setAudioQuality(String q) { prefs.edit().putString("audio_quality", q).apply(); }

    // Sleep timer default (minutes)
    public int getSleepTimerDefault() { return prefs.getInt("sleep_timer_default", 30); }
    public void setSleepTimerDefault(int m) { prefs.edit().putInt("sleep_timer_default", m).apply(); }

    public String getAartiPlaybackMode() {
        String mode = prefs.getString("aarti_playback_mode", AARTI_PLAYBACK_AUTO);
        if (AARTI_PLAYBACK_SING.equals(mode) || AARTI_PLAYBACK_READ.equals(mode)) {
            return mode;
        }
        return AARTI_PLAYBACK_AUTO;
    }

    public void setAartiPlaybackMode(String mode) {
        String value;
        if (AARTI_PLAYBACK_SING.equals(mode)) {
            value = AARTI_PLAYBACK_SING;
        } else if (AARTI_PLAYBACK_READ.equals(mode)) {
            value = AARTI_PLAYBACK_READ;
        } else {
            value = AARTI_PLAYBACK_AUTO;
        }
        prefs.edit().putString("aarti_playback_mode", value).apply();
    }

    // Audio preset: "normal", "clarity", "night"
    public String getAudioPreset() { return prefs.getString("audio_preset", "normal"); }
    public void setAudioPreset(String p) { prefs.edit().putString("audio_preset", p).apply(); }

    // Loudness normalization
    public boolean isLoudnessNormalizationEnabled() { return prefs.getBoolean("loudness_normalization", true); }
    public void setLoudnessNormalization(boolean e) { prefs.edit().putBoolean("loudness_normalization", e).apply(); }

    // Crossfade duration in ms (0 = disabled/gapless)
    public int getCrossfadeDurationMs() { return prefs.getInt("crossfade_ms", 0); }
    public void setCrossfadeDurationMs(int ms) { prefs.edit().putInt("crossfade_ms", ms).apply(); }

    // Audio profile per content type: "default", "festive", "meditative", "chanting"
    public String getAudioProfile(String contentType) {
        return prefs.getString("audio_profile_" + contentType, "default");
    }
    public void setAudioProfile(String contentType, String profile) {
        prefs.edit().putString("audio_profile_" + contentType, profile).apply();
    }

    // Smart prefetch enabled
    public boolean isSmartPrefetchEnabled() { return prefs.getBoolean("smart_prefetch", true); }
    public void setSmartPrefetchEnabled(boolean e) { prefs.edit().putBoolean("smart_prefetch", e).apply(); }

    // Chanting coach mode
    public boolean isChantingCoachEnabled() { return prefs.getBoolean("chanting_coach", false); }
    public void setChantingCoachEnabled(boolean e) { prefs.edit().putBoolean("chanting_coach", e).apply(); }

    // Soundscape type: "none", "temple", "nature", "om_drone"
    public String getSoundscape() { return prefs.getString("soundscape", "none"); }
    public void setSoundscape(String s) { prefs.edit().putString("soundscape", s).apply(); }

    // Natural Voice (Edge TTS) settings — free, no API key needed
    public boolean isCloudTtsEnabled() { return prefs.getBoolean("cloud_tts_enabled", true); }
    public void setCloudTtsEnabled(boolean e) { prefs.edit().putBoolean("cloud_tts_enabled", e).apply(); }

    // Voice override (empty = auto based on gender preference)
    public String getCloudTtsVoice() { return prefs.getString("cloud_tts_voice", ""); }
    public void setCloudTtsVoice(String v) { prefs.edit().putString("cloud_tts_voice", v).apply(); }

    public boolean isCloudTtsCacheEnabled() { return prefs.getBoolean("cloud_tts_cache", true); }
    public void setCloudTtsCacheEnabled(boolean e) { prefs.edit().putBoolean("cloud_tts_cache", e).apply(); }

    // Auto wallpaper settings
    public boolean isAutoWallpaperEnabled() { return prefs.getBoolean("auto_wallpaper_enabled", false); }
    public void setAutoWallpaperEnabled(boolean e) { prefs.edit().putBoolean("auto_wallpaper_enabled", e).apply(); }

    public long getAutoWallpaperIntervalMs() { return prefs.getLong("auto_wallpaper_interval", 24 * 60 * 60 * 1000L); }
    public void setAutoWallpaperIntervalMs(long ms) { prefs.edit().putLong("auto_wallpaper_interval", ms).apply(); }

    public String getAutoWallpaperCategory() { return prefs.getString("auto_wallpaper_category", "All"); }
    public void setAutoWallpaperCategory(String c) { prefs.edit().putString("auto_wallpaper_category", c).apply(); }

    // === Japa Counter ===

    private String todayKey() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
    }

    public void setJapaBeadCount(int count) { prefs.edit().putInt("japa_current_bead", count).apply(); }
    public int getJapaBeadCount() { return prefs.getInt("japa_current_bead", 0); }

    public void setJapaSessionMalas(int count) { prefs.edit().putInt("japa_session_malas", count).apply(); }
    public int getJapaSessionMalas() { return prefs.getInt("japa_session_malas", 0); }

    public void setJapaTodayTotal(int count) { prefs.edit().putInt("japa_today_total_" + todayKey(), count).apply(); }
    public int getJapaTodayTotal() { return prefs.getInt("japa_today_total_" + todayKey(), 0); }

    public void addJapaLifetimeTotal(int malas) {
        long current = prefs.getLong("japa_lifetime_total", 0);
        prefs.edit().putLong("japa_lifetime_total", current + malas).apply();
    }
    public long getJapaLifetimeTotal() { return prefs.getLong("japa_lifetime_total", 0); }

    public void setJapaTarget(int target) { prefs.edit().putInt("japa_target_malas", target).apply(); }
    public int getJapaTarget() { return prefs.getInt("japa_target_malas", 3); }

    public void setJapaMantra(String mantra) { prefs.edit().putString("japa_selected_mantra", mantra).apply(); }
    public String getJapaMantra() { return prefs.getString("japa_selected_mantra", "Om Namah Shivaya"); }
}
