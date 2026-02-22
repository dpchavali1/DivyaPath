package com.divyapath.app.ui.japa;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.divyapath.app.utils.PreferenceManager;

/**
 * ViewModel for the Japa Counter (Mala Counter).
 * Tracks bead count (0-107), malas completed, daily/lifetime totals.
 * All state persisted via SharedPreferences.
 */
public class JapaCounterViewModel extends AndroidViewModel {

    private static final int BEADS_PER_MALA = 108;

    private final PreferenceManager prefs;

    private final MutableLiveData<Integer> currentBead = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> sessionMalas = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> todayTotal = new MutableLiveData<>(0);
    private final MutableLiveData<Long> lifetimeTotal = new MutableLiveData<>(0L);
    private final MutableLiveData<Integer> targetMalas = new MutableLiveData<>(3);
    private final MutableLiveData<String> selectedMantra = new MutableLiveData<>("Om Namah Shivaya");
    private final MutableLiveData<Boolean> malaJustCompleted = new MutableLiveData<>(false);

    public JapaCounterViewModel(@NonNull Application application) {
        super(application);
        prefs = new PreferenceManager(application);
        loadState();
    }

    private void loadState() {
        currentBead.setValue(prefs.getJapaBeadCount());
        sessionMalas.setValue(prefs.getJapaSessionMalas());
        todayTotal.setValue(prefs.getJapaTodayTotal());
        lifetimeTotal.setValue(prefs.getJapaLifetimeTotal());
        targetMalas.setValue(prefs.getJapaTarget());
        String mantra = prefs.getJapaMantra();
        if (mantra != null && !mantra.isEmpty()) {
            selectedMantra.setValue(mantra);
        }
    }

    /**
     * Increment the bead counter by one.
     * If we reach 108, auto-complete the mala.
     */
    public void incrementBead() {
        int bead = getIntValue(currentBead) + 1;
        if (bead >= BEADS_PER_MALA) {
            completeMala();
        } else {
            currentBead.setValue(bead);
            prefs.setJapaBeadCount(bead);
        }
    }

    /**
     * Complete a mala: reset bead to 0, increment all counters.
     */
    private void completeMala() {
        // Reset bead
        currentBead.setValue(0);
        prefs.setJapaBeadCount(0);

        // Increment session malas
        int session = getIntValue(sessionMalas) + 1;
        sessionMalas.setValue(session);
        prefs.setJapaSessionMalas(session);

        // Increment today's total
        int today = getIntValue(todayTotal) + 1;
        todayTotal.setValue(today);
        prefs.setJapaTodayTotal(today);

        // Increment lifetime total
        long lifetime = getLongValue(lifetimeTotal) + 1;
        lifetimeTotal.setValue(lifetime);
        prefs.addJapaLifetimeTotal(1);

        // Signal mala completion for UI feedback
        malaJustCompleted.setValue(true);
    }

    /**
     * Reset bead count and session malas (does NOT reset today's total or lifetime).
     */
    public void resetCounter() {
        currentBead.setValue(0);
        sessionMalas.setValue(0);
        prefs.setJapaBeadCount(0);
        prefs.setJapaSessionMalas(0);
    }

    public void setTarget(int target) {
        targetMalas.setValue(target);
        prefs.setJapaTarget(target);
    }

    public void setMantra(String mantra) {
        selectedMantra.setValue(mantra);
        prefs.setJapaMantra(mantra);
    }

    public void onMalaCompletionAcknowledged() {
        malaJustCompleted.setValue(false);
    }

    // LiveData getters
    public LiveData<Integer> getCurrentBead() { return currentBead; }
    public LiveData<Integer> getSessionMalas() { return sessionMalas; }
    public LiveData<Integer> getTodayTotal() { return todayTotal; }
    public LiveData<Long> getLifetimeTotal() { return lifetimeTotal; }
    public LiveData<Integer> getTargetMalas() { return targetMalas; }
    public LiveData<String> getSelectedMantra() { return selectedMantra; }
    public LiveData<Boolean> getMalaJustCompleted() { return malaJustCompleted; }

    private int getIntValue(MutableLiveData<Integer> liveData) {
        Integer val = liveData.getValue();
        return val != null ? val : 0;
    }

    private long getLongValue(MutableLiveData<Long> liveData) {
        Long val = liveData.getValue();
        return val != null ? val : 0L;
    }
}
