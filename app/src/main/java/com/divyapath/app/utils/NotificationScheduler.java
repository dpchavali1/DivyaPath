package com.divyapath.app.utils;

import android.content.Context;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Centralized scheduler for all background notification and periodic work tasks.
 * Uses WorkManager for reliable scheduling across app restarts and device reboots.
 */
public class NotificationScheduler {

    private static final String MORNING_WORK = "morning_reminder";
    private static final String EVENING_WORK = "evening_reminder";
    private static final String FESTIVAL_WORK = "festival_alert";
    private static final String PANCHANG_WORK = "panchang_prefetch";
    private static final String AUTO_WALLPAPER_WORK = "auto_wallpaper";

    /**
     * Schedule all background tasks based on user preferences.
     * Called from DivyaPathApp.onCreate() and when settings change.
     */
    public static void scheduleAll(Context context) {
        PreferenceManager prefs = new PreferenceManager(context);

        // Morning reminder
        if (prefs.isMorningNotificationEnabled()) {
            scheduleMorningReminder(context, prefs.getNotificationHour(), prefs.getNotificationMinute());
        } else {
            WorkManager.getInstance(context).cancelUniqueWork(MORNING_WORK);
        }

        // Evening reminder
        if (prefs.isEveningNotificationEnabled()) {
            scheduleEveningReminder(context, 19, 0);
        } else {
            WorkManager.getInstance(context).cancelUniqueWork(EVENING_WORK);
        }

        // Festival alerts are always scheduled independently of other notification toggles
        scheduleFestivalAlert(context);

        // Periodic background tasks
        schedulePanchangPrefetch(context);
        scheduleAutoWallpaper(context);
    }

    /**
     * Cancel all scheduled background tasks.
     */
    public static void cancelAll(Context context) {
        WorkManager.getInstance(context).cancelUniqueWork(MORNING_WORK);
        WorkManager.getInstance(context).cancelUniqueWork(EVENING_WORK);
        WorkManager.getInstance(context).cancelUniqueWork(FESTIVAL_WORK);
        WorkManager.getInstance(context).cancelUniqueWork(PANCHANG_WORK);
        WorkManager.getInstance(context).cancelUniqueWork(AUTO_WALLPAPER_WORK);
    }

    public static void scheduleMorningReminder(Context context, int hour, int minute) {
        long delay = getDelayUntil(hour, minute);
        PeriodicWorkRequest req = new PeriodicWorkRequest.Builder(
                MorningReminderWorker.class, 1, TimeUnit.DAYS)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag(MORNING_WORK)
                .build();
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                MORNING_WORK, ExistingPeriodicWorkPolicy.UPDATE, req);
    }

    public static void scheduleEveningReminder(Context context, int hour, int minute) {
        long delay = getDelayUntil(hour, minute);
        PeriodicWorkRequest req = new PeriodicWorkRequest.Builder(
                EveningReminderWorker.class, 1, TimeUnit.DAYS)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag(EVENING_WORK)
                .build();
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                EVENING_WORK, ExistingPeriodicWorkPolicy.UPDATE, req);
    }

    public static void scheduleFestivalAlert(Context context) {
        PeriodicWorkRequest req = new PeriodicWorkRequest.Builder(
                FestivalAlertWorker.class, 1, TimeUnit.DAYS)
                .setInitialDelay(getDelayUntil(0, 30), TimeUnit.MILLISECONDS)
                .addTag(FESTIVAL_WORK)
                .build();
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                FESTIVAL_WORK, ExistingPeriodicWorkPolicy.KEEP, req);
    }

    public static void schedulePanchangPrefetch(Context context) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        PeriodicWorkRequest req = new PeriodicWorkRequest.Builder(
                PeriodicPanchangWorker.class, 12, TimeUnit.HOURS)
                .setInitialDelay(1, TimeUnit.MINUTES)
                .addTag(PANCHANG_WORK)
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                PANCHANG_WORK, ExistingPeriodicWorkPolicy.KEEP, req);
    }

    public static void scheduleAutoWallpaper(Context context) {
        PreferenceManager pm = new PreferenceManager(context);
        if (!pm.isAutoWallpaperEnabled()) {
            WorkManager.getInstance(context).cancelUniqueWork(AUTO_WALLPAPER_WORK);
            return;
        }

        long intervalMs = pm.getAutoWallpaperIntervalMs();
        // WorkManager minimum periodic interval is 15 minutes
        long intervalMinutes = Math.max(15, intervalMs / (60 * 1000));

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        PeriodicWorkRequest req = new PeriodicWorkRequest.Builder(
                AutoWallpaperWorker.class, intervalMinutes, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .addTag(AUTO_WALLPAPER_WORK)
                .build();
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                AUTO_WALLPAPER_WORK, ExistingPeriodicWorkPolicy.UPDATE, req);
    }

    /**
     * Calculate milliseconds until the next occurrence of a given time.
     * If the time has already passed today, returns the delay until tomorrow's occurrence.
     */
    private static long getDelayUntil(int hour, int minute) {
        Calendar now = Calendar.getInstance();
        Calendar target = (Calendar) now.clone();
        target.set(Calendar.HOUR_OF_DAY, hour);
        target.set(Calendar.MINUTE, minute);
        target.set(Calendar.SECOND, 0);
        if (target.before(now)) {
            target.add(Calendar.DAY_OF_YEAR, 1);
        }
        return target.getTimeInMillis() - now.getTimeInMillis();
    }
}
