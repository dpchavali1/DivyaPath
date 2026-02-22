package com.divyapath.app;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.appcompat.app.AppCompatDelegate;

import com.divyapath.app.utils.ArchiveOrgValidator;
import com.divyapath.app.utils.NotificationScheduler;
import com.divyapath.app.utils.PreferenceManager;
import com.divyapath.app.utils.SmartPrefetchWorker;
import com.google.android.gms.ads.MobileAds;

public class DivyaPathApp extends Application {

    public static final String CHANNEL_DAILY_REMINDER = "daily_reminder";
    public static final String CHANNEL_FESTIVAL = "festival_alerts";
    public static final String CHANNEL_AUDIO_PLAYBACK = "audio_playback";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();

        // Apply saved theme preference
        PreferenceManager prefs = new PreferenceManager(this);
        applyTheme(prefs.getTheme());

        // Schedule notification workers while honoring persisted preferences.
        NotificationScheduler.scheduleAll(this);

        // Schedule weekly Archive.org URL validation
        ArchiveOrgValidator.scheduleWeeklyValidation(this);

        // Schedule daily smart prefetch (WiFi + charging only)
        SmartPrefetchWorker.scheduleDailyPrefetch(this);

        // Initialize AdMob
        MobileAds.initialize(this, initializationStatus -> {});
    }

    public static void applyTheme(String theme) {
        switch (theme) {
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case "saffron":
            case "light":
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
        }
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel dailyChannel = new NotificationChannel(
                    CHANNEL_DAILY_REMINDER,
                    "Daily Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            dailyChannel.setDescription("Daily morning and evening prayer reminders");

            NotificationChannel festivalChannel = new NotificationChannel(
                    CHANNEL_FESTIVAL,
                    "Festival Alerts",
                    NotificationManager.IMPORTANCE_HIGH
            );
            festivalChannel.setDescription("Upcoming festival notifications");

            NotificationChannel audioChannel = new NotificationChannel(
                    CHANNEL_AUDIO_PLAYBACK,
                    "Audio Playback",
                    NotificationManager.IMPORTANCE_LOW
            );
            audioChannel.setDescription("Persistent notification while playing aarti, chalisa, or mantra audio");

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(dailyChannel);
                manager.createNotificationChannel(festivalChannel);
                manager.createNotificationChannel(audioChannel);
            }
        }
    }
}
