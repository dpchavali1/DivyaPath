package com.divyapath.app.utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.divyapath.app.data.local.DivyaPathDatabase;
import com.divyapath.app.data.local.DatabaseSeeder;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * Weekly WorkManager job that validates Archive.org streaming URLs are still accessible.
 * Runs a HEAD request on each known archive.org URL; if a URL returns non-200,
 * logs a warning. In production, this could trigger a fallback or re-seed.
 *
 * Also re-runs the backfill to pick up any new URLs added in app updates.
 */
public class ArchiveOrgValidator extends Worker {

    private static final String TAG = "ArchiveOrgValidator";
    private static final String WORK_NAME = "archive_org_validator";
    private static final int CONNECT_TIMEOUT_MS = 10_000;

    public ArchiveOrgValidator(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "Starting Archive.org URL validation");

        // Re-run backfill to ensure all URLs are up to date
        try {
            DivyaPathDatabase db = DivyaPathDatabase.getDatabase(getApplicationContext());
            DatabaseSeeder.seedDatabase(db);
        } catch (Exception e) {
            Log.e(TAG, "Database backfill failed", e);
        }

        // Validate known archive.org URLs
        String[] urls = {
                "https://archive.org/download/PretrajSarkarKiAarti/Jai-Ganesh-Deva.mp3",
                "https://archive.org/download/PretrajSarkarKiAarti/Jai-Shiv-Omkara.mp3",
                "https://archive.org/download/PretrajSarkarKiAarti/Om-Jai-Jagdish-Hare-Bijender-Chauhan.mp3",
                "https://archive.org/download/PretrajSarkarKiAarti/Jai-Lakshmi-Mata.mp3",
                "https://archive.org/download/PretrajSarkarKiAarti/Jai%20Ambe%20Gauri-f.mp3",
                "https://archive.org/download/PretrajSarkarKiAarti/Aarti-Kije-Hanuman-Lala-Ki.mp3",
                "https://archive.org/download/PretrajSarkarKiAarti/Kunj-Vihari-Ki.mp3",
                "https://archive.org/download/PretrajSarkarKiAarti/Saraswati-Mata-Aarti.mp3",
                "https://archive.org/download/PretrajSarkarKiAarti/Surya%20Dev%20Ji%20Ki%20Aarti.mp3",
                "https://archive.org/download/PretrajSarkarKiAarti/Ram%20Ji%20Ki.mp3",
                "https://archive.org/download/ShreeHanumanChalisa_201510/05%20Shree%20Hanuman%20Chalisa.mp3"
        };

        int successCount = 0;
        int failCount = 0;

        for (String urlStr : urls) {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
                conn.setRequestMethod("HEAD");
                conn.setConnectTimeout(CONNECT_TIMEOUT_MS);
                conn.setReadTimeout(CONNECT_TIMEOUT_MS);
                conn.setInstanceFollowRedirects(true);
                int code = conn.getResponseCode();
                conn.disconnect();

                if (code >= 200 && code < 400) {
                    successCount++;
                } else {
                    failCount++;
                    Log.w(TAG, "URL returned " + code + ": " + urlStr);
                }
            } catch (Exception e) {
                failCount++;
                Log.w(TAG, "URL check failed: " + urlStr, e);
            }
        }

        Log.d(TAG, "Validation complete: " + successCount + " OK, " + failCount + " failed");
        return Result.success();
    }

    /**
     * Schedule the weekly validation job. Call from DivyaPathApp.onCreate().
     */
    public static void scheduleWeeklyValidation(@NonNull Context context) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build();

        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                ArchiveOrgValidator.class, 7, TimeUnit.DAYS)
                .setConstraints(constraints)
                .setInitialDelay(1, TimeUnit.DAYS) // Don't run immediately on first install
                .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
        );
    }
}
