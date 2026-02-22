package com.divyapath.app.ui.player;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.media3.common.Player;
import androidx.navigation.Navigation;

import com.divyapath.app.R;
import com.divyapath.app.audio.AudioPlayerManager;
import com.divyapath.app.audio.AudioSourceResolver;
import com.divyapath.app.audio.AudioTrack;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.Slider;

/**
 * Full-screen audio player with deity image, track info, scrollable lyrics,
 * seekbar, playback controls, speed selector, and sleep timer.
 */
public class AudioPlayerFragment extends Fragment {

    private AudioPlayerManager playerManager;

    // Views
    private MaterialToolbar toolbar;
    private ImageView deityImage;
    private TextView trackTitle;
    private TextView trackSubtitle;
    private Chip chipSource;
    private View lyricsCard;
    private TextView lyricsText;
    private Slider seekbar;
    private TextView currentTime;
    private TextView totalTime;
    private ImageButton btnRepeat;
    private ImageButton btnPrev;
    private FloatingActionButton fabPlayPause;
    private ImageButton btnNext;
    private ImageButton btnShuffle;
    private Chip chipSpeed;
    private Chip chipSleep;

    // State
    private boolean isSeeking = false;
    private int currentRepeatMode = Player.REPEAT_MODE_OFF;
    private boolean shuffleEnabled = false;
    private float currentSpeed = 1.0f;
    private static final float[] SPEED_OPTIONS = {0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f};
    private static final String[] SPEED_LABELS = {"0.5x", "0.75x", "1.0x", "1.25x", "1.5x", "2.0x"};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playerManager = AudioPlayerManager.getInstance(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_audio_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindViews(view);
        setupToolbar();
        setupControls();
        observePlayerState();
    }

    private void bindViews(View v) {
        toolbar = v.findViewById(R.id.toolbar_player);
        deityImage = v.findViewById(R.id.player_deity_image);
        trackTitle = v.findViewById(R.id.player_track_title);
        trackSubtitle = v.findViewById(R.id.player_track_subtitle);
        chipSource = v.findViewById(R.id.player_chip_source);
        lyricsCard = v.findViewById(R.id.player_lyrics_card);
        lyricsText = v.findViewById(R.id.player_lyrics_text);
        seekbar = v.findViewById(R.id.player_seekbar);
        currentTime = v.findViewById(R.id.player_current_time);
        totalTime = v.findViewById(R.id.player_total_time);
        btnRepeat = v.findViewById(R.id.player_btn_repeat);
        btnPrev = v.findViewById(R.id.player_btn_prev);
        fabPlayPause = v.findViewById(R.id.player_fab_play_pause);
        btnNext = v.findViewById(R.id.player_btn_next);
        btnShuffle = v.findViewById(R.id.player_btn_shuffle);
        chipSpeed = v.findViewById(R.id.player_chip_speed);
        chipSleep = v.findViewById(R.id.player_chip_sleep);
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(v).popBackStack());
    }

    private void setupControls() {
        // Play/Pause FAB
        fabPlayPause.setOnClickListener(v -> playerManager.togglePlayPause());

        // Skip
        btnNext.setOnClickListener(v -> playerManager.skipToNext());
        btnPrev.setOnClickListener(v -> playerManager.skipToPrevious());

        // Repeat
        btnRepeat.setOnClickListener(v -> {
            switch (currentRepeatMode) {
                case Player.REPEAT_MODE_OFF:
                    currentRepeatMode = Player.REPEAT_MODE_ALL;
                    btnRepeat.setColorFilter(requireContext().getColor(R.color.saffron_primary));
                    break;
                case Player.REPEAT_MODE_ALL:
                    currentRepeatMode = Player.REPEAT_MODE_ONE;
                    btnRepeat.setColorFilter(requireContext().getColor(R.color.deep_maroon));
                    break;
                default:
                    currentRepeatMode = Player.REPEAT_MODE_OFF;
                    btnRepeat.setColorFilter(requireContext().getColor(R.color.text_secondary));
                    break;
            }
            playerManager.setRepeatMode(currentRepeatMode);
        });

        // Shuffle
        btnShuffle.setOnClickListener(v -> {
            shuffleEnabled = !shuffleEnabled;
            btnShuffle.setColorFilter(requireContext().getColor(
                    shuffleEnabled ? R.color.saffron_primary : R.color.text_secondary));
            playerManager.setShuffleEnabled(shuffleEnabled);
        });

        // Seekbar
        seekbar.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
                isSeeking = true;
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                isSeeking = false;
                long duration = playerManager.getDuration();
                if (duration > 0) {
                    long newPos = (long) (slider.getValue() * duration / 100f);
                    playerManager.seekTo(newPos);
                }
            }
        });

        // Speed chip
        chipSpeed.setOnClickListener(v -> showSpeedDialog());

        // Sleep timer chip
        chipSleep.setOnClickListener(v -> showSleepTimerDialog());
    }

    private void observePlayerState() {
        // Current track
        playerManager.getCurrentTrackLiveData().observe(getViewLifecycleOwner(), track -> {
            if (track != null) {
                updateTrackInfo(track);
            }
        });

        // Playing state
        playerManager.getIsPlayingLiveData().observe(getViewLifecycleOwner(), isPlaying -> {
            fabPlayPause.setImageResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);
        });

        // Progress
        playerManager.getProgressLiveData().observe(getViewLifecycleOwner(), progress -> {
            if (!isSeeking && progress != null) {
                seekbar.setValue(Math.max(0, Math.min(progress, 100)));
            }
        });

        // Time labels
        playerManager.getCurrentTimeLiveData().observe(getViewLifecycleOwner(), time -> {
            if (currentTime != null) currentTime.setText(time);
        });

        playerManager.getTotalTimeLiveData().observe(getViewLifecycleOwner(), time -> {
            if (totalTime != null) totalTime.setText(time);
        });
    }

    private void updateTrackInfo(AudioTrack track) {
        trackTitle.setText(track.getTitle());
        trackSubtitle.setText(track.getDeityName() != null ? track.getDeityName() : track.getSubtitle());

        // Audio source badge
        String sourceLabel = track.getAudioSourceLabel();
        if (sourceLabel != null && !sourceLabel.isEmpty()) {
            chipSource.setVisibility(View.VISIBLE);
            chipSource.setText(sourceLabel);
            // Set badge color based on source type
            String sourceType = track.getAudioSourceType();
            int colorRes = R.color.abhijit_green;
            if ("archive_org".equals(sourceType) || "iskcon".equals(sourceType)) {
                colorRes = R.color.saffron_primary;
            } else if ("tts".equals(sourceType)) {
                colorRes = R.color.text_secondary;
            }
            chipSource.setChipBackgroundColorResource(colorRes);
        } else {
            chipSource.setVisibility(View.GONE);
        }

        // Show lyrics if available
        String lyrics = track.getLyricsHindi();
        if (lyrics == null || lyrics.isEmpty()) {
            lyrics = track.getLyricsEnglish();
        }
        if (lyrics != null && !lyrics.isEmpty()) {
            lyricsCard.setVisibility(View.VISIBLE);
            lyricsText.setText(lyrics);
        } else {
            lyricsCard.setVisibility(View.GONE);
        }
    }

    private void showSpeedDialog() {
        int currentIdx = 2; // default 1.0x
        for (int i = 0; i < SPEED_OPTIONS.length; i++) {
            if (Math.abs(SPEED_OPTIONS[i] - currentSpeed) < 0.01f) {
                currentIdx = i;
                break;
            }
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Playback Speed")
                .setSingleChoiceItems(SPEED_LABELS, currentIdx, (dialog, which) -> {
                    currentSpeed = SPEED_OPTIONS[which];
                    playerManager.setSpeed(currentSpeed);
                    chipSpeed.setText(SPEED_LABELS[which]);
                    dialog.dismiss();
                })
                .show();
    }

    private void showSleepTimerDialog() {
        String[] options = {"5 minutes", "10 minutes", "15 minutes", "30 minutes", "After this track", "Cancel timer"};
        long[] durations = {5 * 60_000L, 10 * 60_000L, 15 * 60_000L, 30 * 60_000L, -1, 0};

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Sleep Timer")
                .setItems(options, (dialog, which) -> {
                    if (durations[which] == 0) {
                        playerManager.cancelSleepTimer();
                        chipSleep.setText("Sleep Timer");
                    } else if (durations[which] == -1) {
                        playerManager.setSleepAfterTrack();
                        chipSleep.setText("After track");
                    } else {
                        playerManager.setSleepTimer(durations[which]);
                        chipSleep.setText(options[which]);
                    }
                })
                .show();
    }
}
