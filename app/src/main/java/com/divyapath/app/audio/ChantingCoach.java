package com.divyapath.app.audio;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.divyapath.app.R;
import com.divyapath.app.utils.PreferenceManager;

/**
 * Call-and-response chanting coach that turns passive listening into guided practice.
 *
 * Flow:
 *   1. TTS speaks a line (the "call")
 *   2. Bell chime plays
 *   3. Visual prompt shows "Your turn — repeat aloud"
 *   4. Timer countdown for user to repeat
 *   5. Bell chime again
 *   6. Next line plays
 *
 * Works with both TtsPlayerManager and DivyaPathTTSEngine as line-based callbacks.
 */
public class ChantingCoach {

    private static final String TAG = "ChantingCoach";

    public enum CoachState {
        IDLE,               // Not active
        LISTENING,          // TTS is speaking a line (call)
        WAITING_USER,       // Waiting for user to repeat (response)
        TRANSITIONING       // Brief pause before next line
    }

    // Default wait time for user to chant (milliseconds)
    private static final long DEFAULT_RESPONSE_TIME_MS = 4000;
    private static final long TRANSITION_PAUSE_MS = 800;

    private final Context appContext;
    private final PreferenceManager preferences;
    private MediaPlayer bellPlayer;
    private final Handler handler = new Handler(Looper.getMainLooper());

    // State
    private boolean enabled = false;
    private long responseTimeMs = DEFAULT_RESPONSE_TIME_MS;
    private int currentLineIndex = 0;
    private int totalLines = 0;
    private long responseStartTime = 0;

    // LiveData
    private final MutableLiveData<CoachState> coachState = new MutableLiveData<>(CoachState.IDLE);
    private final MutableLiveData<String> promptText = new MutableLiveData<>("");
    private final MutableLiveData<Integer> countdownSeconds = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> completedLines = new MutableLiveData<>(0);

    // Callback for controlling the TTS engine
    private CoachCallback callback;

    public interface CoachCallback {
        void onReadyForNextLine();    // Resume TTS for next line
        void onCoachPaused();         // TTS should pause
        void onCoachCompleted();      // All lines done
    }

    public ChantingCoach(@NonNull Context context) {
        this.appContext = context.getApplicationContext();
        this.preferences = new PreferenceManager(appContext);
    }

    public void setCallback(@Nullable CoachCallback callback) {
        this.callback = callback;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            coachState.postValue(CoachState.IDLE);
            handler.removeCallbacksAndMessages(null);
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setTotalLines(int total) {
        this.totalLines = total;
    }

    public void setResponseTimeMs(long ms) {
        this.responseTimeMs = Math.max(2000, Math.min(ms, 10000));
    }

    /**
     * Called when TTS starts speaking a line (the "call" phase).
     */
    public void onLineStarted(int lineIndex, String lineText) {
        if (!enabled) return;
        currentLineIndex = lineIndex;
        coachState.postValue(CoachState.LISTENING);
        promptText.postValue(lineText);
    }

    /**
     * Called when TTS finishes speaking a line. Start the "response" phase.
     */
    public void onLineCompleted(int lineIndex) {
        if (!enabled) return;

        // Play bell chime to signal user's turn
        playBellChime();

        coachState.postValue(CoachState.WAITING_USER);
        promptText.postValue("Your turn — repeat aloud");
        responseStartTime = System.currentTimeMillis();
        completedLines.postValue(lineIndex + 1);

        // Start countdown
        startCountdown();

        // After response time, transition to next line
        handler.postDelayed(() -> {
            if (!enabled) return;
            coachState.postValue(CoachState.TRANSITIONING);
            promptText.postValue("");

            // Play bell to end response phase
            playBellChime();

            handler.postDelayed(() -> {
                if (!enabled) return;
                if (callback != null) {
                    callback.onReadyForNextLine();
                }
            }, TRANSITION_PAUSE_MS);
        }, responseTimeMs);
    }

    /**
     * Called when user taps "Done" early to skip waiting.
     */
    public void userTappedReady() {
        if (!enabled || coachState.getValue() != CoachState.WAITING_USER) return;
        handler.removeCallbacksAndMessages(null);
        coachState.postValue(CoachState.TRANSITIONING);
        playBellChime();

        handler.postDelayed(() -> {
            if (callback != null) callback.onReadyForNextLine();
        }, TRANSITION_PAUSE_MS);
    }

    /**
     * Called when all lines are completed.
     */
    public void onAllLinesCompleted() {
        if (!enabled) return;
        coachState.postValue(CoachState.IDLE);
        promptText.postValue("Well done! Practice complete.");
        handler.removeCallbacksAndMessages(null);
        if (callback != null) callback.onCoachCompleted();
    }

    private void startCountdown() {
        int totalSeconds = (int) (responseTimeMs / 1000);
        countdownSeconds.postValue(totalSeconds);

        for (int i = 1; i <= totalSeconds; i++) {
            final int remaining = totalSeconds - i;
            handler.postDelayed(() -> countdownSeconds.postValue(remaining), i * 1000L);
        }
    }

    private void playBellChime() {
        try {
            if (bellPlayer != null) {
                bellPlayer.release();
            }
            bellPlayer = MediaPlayer.create(appContext, R.raw.bell_tone);
            if (bellPlayer != null) {
                bellPlayer.setVolume(0.5f, 0.5f);
                bellPlayer.setOnCompletionListener(MediaPlayer::release);
                bellPlayer.start();
            }
        } catch (Exception e) {
            Log.e(TAG, "Bell chime error", e);
        }
    }

    public void reset() {
        enabled = false;
        handler.removeCallbacksAndMessages(null);
        coachState.postValue(CoachState.IDLE);
        promptText.postValue("");
        countdownSeconds.postValue(0);
        completedLines.postValue(0);
        if (bellPlayer != null) {
            bellPlayer.release();
            bellPlayer = null;
        }
    }

    // --- LiveData ---

    public LiveData<CoachState> getCoachState() { return coachState; }
    public LiveData<String> getPromptText() { return promptText; }
    public LiveData<Integer> getCountdownSeconds() { return countdownSeconds; }
    public LiveData<Integer> getCompletedLines() { return completedLines; }
    public int getTotalLines() { return totalLines; }
    public int getCurrentLineIndex() { return currentLineIndex; }
}
