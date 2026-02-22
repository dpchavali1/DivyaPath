package com.divyapath.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Runs daily at midnight to pre-fetch panchang data for the next 7 days.
 * Stores computed panchang in SharedPreferences for quick offline access.
 */
public class PeriodicPanchangWorker extends Worker {

    public PeriodicPanchangWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            SharedPreferences prefs = getApplicationContext()
                    .getSharedPreferences("panchang_cache", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            Gson gson = new Gson();

            PreferenceManager prefMgr = new PreferenceManager(getApplicationContext());
            double lat = prefMgr.getLocationLat();
            double lon = prefMgr.getLocationLon();
            String tz = prefMgr.getEffectiveTimezone();
            PanchangCalculator calculator = new PanchangCalculator();
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(tz));

            for (int i = 0; i < 7; i++) {
                Map<String, String> panchang = calculator.getPanchangForDate(cal, lat, lon, tz);
                String dateKey = String.format("%04d-%02d-%02d",
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH) + 1,
                        cal.get(Calendar.DAY_OF_MONTH));
                editor.putString("panchang_" + dateKey, gson.toJson(panchang));
                cal.add(Calendar.DAY_OF_YEAR, 1);
            }

            editor.putLong("last_panchang_update", System.currentTimeMillis());
            editor.apply();

            return Result.success();
        } catch (Exception e) {
            return Result.retry();
        }
    }
}
