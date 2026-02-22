package com.divyapath.app.ui.seva;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.divyapath.app.utils.PreferenceManager;
import com.divyapath.app.utils.SevaData;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SevaViewModel extends AndroidViewModel {

    private final PreferenceManager preferenceManager;
    private final MutableLiveData<SevaData.SevaItem> todaysSeva = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isCompletedToday = new MutableLiveData<>();
    private final MutableLiveData<Integer> streak = new MutableLiveData<>();
    private final MutableLiveData<Integer> monthlyCount = new MutableLiveData<>();
    private final MutableLiveData<List<SevaData.SevaItem>> allSevas = new MutableLiveData<>();

    public SevaViewModel(@NonNull Application application) {
        super(application);
        preferenceManager = new PreferenceManager(application);
        loadData();
    }

    private void loadData() {
        todaysSeva.setValue(SevaData.getTodaysSeva());
        isCompletedToday.setValue(isSevaCompletedToday());
        streak.setValue(computeStreak());
        monthlyCount.setValue(computeMonthlyCount());
        allSevas.setValue(SevaData.getAllSevas());
    }

    public void markDone() {
        String todayKey = getTodayKey();
        preferenceManager.getSharedPreferences().edit()
                .putBoolean("seva_" + todayKey, true).apply();
        isCompletedToday.setValue(true);
        streak.setValue(computeStreak());
        monthlyCount.setValue(computeMonthlyCount());
    }

    private boolean isSevaCompletedToday() {
        return preferenceManager.getSharedPreferences()
                .getBoolean("seva_" + getTodayKey(), false);
    }

    private int computeStreak() {
        int count = 0;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        // If today is done, include today
        if (isSevaCompletedToday()) {
            count = 1;
            cal.add(Calendar.DAY_OF_YEAR, -1);
        }

        // Check backwards
        for (int i = 0; i < 365; i++) {
            String key = sdf.format(cal.getTime());
            if (preferenceManager.getSharedPreferences().getBoolean("seva_" + key, false)) {
                count++;
                cal.add(Calendar.DAY_OF_YEAR, -1);
            } else {
                break;
            }
        }
        return count;
    }

    private int computeMonthlyCount() {
        int count = 0;
        Calendar cal = Calendar.getInstance();
        int currentMonth = cal.get(Calendar.MONTH);
        int currentYear = cal.get(Calendar.YEAR);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        cal.set(Calendar.DAY_OF_MONTH, 1);
        while (cal.get(Calendar.MONTH) == currentMonth && cal.get(Calendar.YEAR) == currentYear) {
            String key = sdf.format(cal.getTime());
            if (preferenceManager.getSharedPreferences().getBoolean("seva_" + key, false)) {
                count++;
            }
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }
        return count;
    }

    private String getTodayKey() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
    }

    public LiveData<SevaData.SevaItem> getTodaysSeva() { return todaysSeva; }
    public LiveData<Boolean> getIsCompletedToday() { return isCompletedToday; }
    public LiveData<Integer> getStreak() { return streak; }
    public LiveData<Integer> getMonthlyCount() { return monthlyCount; }
    public LiveData<List<SevaData.SevaItem>> getAllSevas() { return allSevas; }
}
