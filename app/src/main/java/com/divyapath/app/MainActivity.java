package com.divyapath.app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.divyapath.app.audio.AudioPlayerManager;
import com.divyapath.app.audio.AudioTrack;
import com.divyapath.app.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;
    private AudioPlayerManager audioPlayerManager;

    // Mini player views
    private View miniPlayerCard;
    private ImageView miniPlayerImage;
    private TextView miniPlayerTitle;
    private TextView miniPlayerSubtitle;
    private ImageButton miniPlayerPrev;
    private ImageButton miniPlayerPlayPause;
    private ImageButton miniPlayerNext;
    private ProgressBar miniPlayerProgress;

    private final ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                // If denied, notifications simply won't appear
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        audioPlayerManager = AudioPlayerManager.getInstance(this);

        setupNavigation();
        setupMiniPlayer();
        observeAudioState();
        observeNetworkState();
        requestNotificationPermissionIfNeeded();
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        audioPlayerManager.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        audioPlayerManager.disconnect();
    }

    private void setupNavigation() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

            // Custom bottom nav item selection with proper back stack management
            binding.bottomNavigation.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                NavOptions navOptions = new NavOptions.Builder()
                        .setPopUpTo(R.id.nav_home, false)
                        .setLaunchSingleTop(true)
                        .setEnterAnim(androidx.navigation.ui.R.anim.nav_default_enter_anim)
                        .setExitAnim(androidx.navigation.ui.R.anim.nav_default_exit_anim)
                        .setPopEnterAnim(androidx.navigation.ui.R.anim.nav_default_pop_enter_anim)
                        .setPopExitAnim(androidx.navigation.ui.R.anim.nav_default_pop_exit_anim)
                        .build();
                try {
                    navController.navigate(itemId, null, navOptions);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            });

            // Re-selecting the same tab pops back to that tab's root
            binding.bottomNavigation.setOnItemReselectedListener(item -> {
                navController.popBackStack(item.getItemId(), false);
            });

            // Sync bottom nav highlight when destination changes
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                int id = destination.getId();

                // Update bottom nav selected state for top-level destinations
                if (id == R.id.nav_home || id == R.id.nav_aarti || id == R.id.nav_mantra
                        || id == R.id.nav_panchang || id == R.id.nav_more) {
                    binding.bottomNavigation.getMenu().findItem(id).setChecked(true);
                }

                // Hide bottom nav on detail/full-player screens
                if (id == R.id.aartiDetailFragment ||
                    id == R.id.chalisaDetailFragment ||
                    id == R.id.mantraDetailFragment ||
                    id == R.id.bhajanDetailFragment ||
                    id == R.id.stotraDetailFragment) {
                    binding.bottomNavigation.setVisibility(View.GONE);
                } else if (id == R.id.audioPlayerFragment) {
                    binding.bottomNavigation.setVisibility(View.GONE);
                    if (miniPlayerCard != null) miniPlayerCard.setVisibility(View.GONE);
                } else {
                    binding.bottomNavigation.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private void setupMiniPlayer() {
        // The mini player is included via <include> so we access views by ID
        miniPlayerCard = findViewById(R.id.mini_player_container);
        miniPlayerImage = findViewById(R.id.mini_player_image);
        miniPlayerTitle = findViewById(R.id.mini_player_title);
        miniPlayerSubtitle = findViewById(R.id.mini_player_subtitle);
        miniPlayerPrev = findViewById(R.id.mini_player_prev);
        miniPlayerPlayPause = findViewById(R.id.mini_player_play_pause);
        miniPlayerNext = findViewById(R.id.mini_player_next);
        miniPlayerProgress = findViewById(R.id.mini_player_progress);

        // Play/Pause
        if (miniPlayerPlayPause != null) {
            miniPlayerPlayPause.setOnClickListener(v -> audioPlayerManager.togglePlayPause());
        }

        // Skip next
        if (miniPlayerNext != null) {
            miniPlayerNext.setOnClickListener(v -> audioPlayerManager.skipToNext());
        }

        // Skip previous
        if (miniPlayerPrev != null) {
            miniPlayerPrev.setOnClickListener(v -> audioPlayerManager.skipToPrevious());
        }

        // Tap mini player body to open full-screen player
        if (miniPlayerCard != null) {
            // Use dedicated text area ID instead of fragile parent traversal
            View textArea = findViewById(R.id.mini_player_text_area);
            if (textArea != null) {
                textArea.setOnClickListener(v -> openFullPlayer());
            }
            // Also set on the image
            if (miniPlayerImage != null) {
                miniPlayerImage.setOnClickListener(v -> openFullPlayer());
            }
        }
    }

    private void observeNetworkState() {
        com.divyapath.app.utils.NetworkMonitor.getInstance(this).getIsConnected().observe(this, connected -> {
            if (binding.tvOfflineBanner != null) {
                binding.tvOfflineBanner.setVisibility(connected ? View.GONE : View.VISIBLE);
            }
        });
    }

    private void observeAudioState() {
        // Observe current track — show/hide mini player
        audioPlayerManager.getCurrentTrackLiveData().observe(this, track -> {
            if (track != null) {
                showMiniPlayer(track);
            } else {
                hideMiniPlayer();
            }
        });

        // Observe play/pause state
        audioPlayerManager.getIsPlayingLiveData().observe(this, isPlaying -> {
            if (miniPlayerPlayPause != null) {
                miniPlayerPlayPause.setImageResource(
                        isPlaying ? R.drawable.ic_pause : R.drawable.ic_play
                );
            }
        });

        // Observe progress
        audioPlayerManager.getProgressLiveData().observe(this, progress -> {
            if (miniPlayerProgress != null) {
                miniPlayerProgress.setProgress(progress != null ? progress : 0);
            }
        });
    }

    private void showMiniPlayer(AudioTrack track) {
        if (miniPlayerCard == null) return;

        // Don't show mini player if we're on the full-screen player
        if (navController != null && navController.getCurrentDestination() != null
                && navController.getCurrentDestination().getId() == R.id.audioPlayerFragment) {
            return;
        }

        miniPlayerCard.setVisibility(View.VISIBLE);

        if (miniPlayerTitle != null) {
            miniPlayerTitle.setText(track.getTitle());
        }
        if (miniPlayerSubtitle != null) {
            miniPlayerSubtitle.setText(track.getDeityName() != null ? track.getDeityName() : track.getSubtitle());
        }
    }

    private void hideMiniPlayer() {
        if (miniPlayerCard != null) {
            miniPlayerCard.setVisibility(View.GONE);
        }
    }

    private void openFullPlayer() {
        if (navController != null) {
            try {
                navController.navigate(R.id.audioPlayerFragment);
            } catch (Exception e) {
                android.util.Log.w("MainActivity", "Navigation to audio player failed", e);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}
