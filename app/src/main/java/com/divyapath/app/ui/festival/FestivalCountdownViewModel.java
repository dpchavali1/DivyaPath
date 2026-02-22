package com.divyapath.app.ui.festival;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.divyapath.app.utils.FestivalMissionData;
import com.divyapath.app.utils.PreferenceManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FestivalCountdownViewModel extends AndroidViewModel {

    private String festivalName;
    private int daysRemaining;
    private List<List<FestivalMissionData.MissionTask>> allDays;
    private final PreferenceManager prefs;

    private final MutableLiveData<Integer> selectedDay = new MutableLiveData<>(0);
    private final MutableLiveData<List<FestivalMissionData.MissionTask>> currentTasks = new MutableLiveData<>();
    private final MutableLiveData<Set<Integer>> completedTasks = new MutableLiveData<>(new HashSet<>());
    private final MutableLiveData<int[]> overallProgress = new MutableLiveData<>(); // [completed, total]

    public FestivalCountdownViewModel(@NonNull Application application) {
        super(application);
        prefs = new PreferenceManager(application);
    }

    public void init(String festivalName, int daysRemaining) {
        this.festivalName = festivalName;
        this.daysRemaining = daysRemaining;
        this.allDays = FestivalMissionData.getMissions(festivalName);

        if (allDays == null || allDays.isEmpty()) return;

        selectDay(0);
        updateOverallProgress();
    }

    public void selectDay(int dayIndex) {
        if (allDays == null || dayIndex < 0 || dayIndex >= allDays.size()) return;
        selectedDay.setValue(dayIndex);
        currentTasks.setValue(allDays.get(dayIndex));
        loadCompletedForDay(dayIndex);
    }

    public void setTaskCompleted(int dayIndex, int taskIndex, boolean done) {
        String key = "mission_" + festivalName + "_d" + dayIndex + "_t" + taskIndex;
        prefs.getSharedPreferences().edit().putBoolean(key, done).apply();
        loadCompletedForDay(dayIndex);
        updateOverallProgress();
    }

    private void loadCompletedForDay(int dayIndex) {
        Set<Integer> completed = new HashSet<>();
        if (allDays != null && dayIndex < allDays.size()) {
            List<FestivalMissionData.MissionTask> tasks = allDays.get(dayIndex);
            for (int i = 0; i < tasks.size(); i++) {
                String key = "mission_" + festivalName + "_d" + dayIndex + "_t" + i;
                if (prefs.getSharedPreferences().getBoolean(key, false)) {
                    completed.add(i);
                }
            }
        }
        completedTasks.setValue(completed);
    }

    private void updateOverallProgress() {
        if (allDays == null) return;
        int total = 0;
        int done = 0;
        for (int d = 0; d < allDays.size(); d++) {
            List<FestivalMissionData.MissionTask> tasks = allDays.get(d);
            total += tasks.size();
            for (int t = 0; t < tasks.size(); t++) {
                String key = "mission_" + festivalName + "_d" + d + "_t" + t;
                if (prefs.getSharedPreferences().getBoolean(key, false)) done++;
            }
        }
        overallProgress.setValue(new int[]{done, total});
    }

    public int getTotalDays() {
        return allDays != null ? allDays.size() : 0;
    }

    public String getFestivalName() { return festivalName; }
    public int getDaysRemaining() { return daysRemaining; }
    public LiveData<Integer> getSelectedDay() { return selectedDay; }
    public LiveData<List<FestivalMissionData.MissionTask>> getCurrentTasks() { return currentTasks; }
    public LiveData<Set<Integer>> getCompletedTasks() { return completedTasks; }
    public LiveData<int[]> getOverallProgress() { return overallProgress; }
}
