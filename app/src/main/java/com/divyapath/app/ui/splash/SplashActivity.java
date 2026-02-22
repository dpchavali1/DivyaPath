package com.divyapath.app.ui.splash;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.divyapath.app.MainActivity;
import com.divyapath.app.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;
    private boolean proceededToMain = false;
    private final ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                    granted -> navigateToMain());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        startSplashAnimation();
    }

    private void startSplashAnimation() {
        View omSymbol = binding.tvOmSymbol;
        View appNameHindi = binding.tvAppNameHindi;
        View appName = binding.tvAppName;
        View tagline = binding.tvTagline;

        // Om symbol: fade in + scale up with overshoot
        ObjectAnimator omFadeIn = ObjectAnimator.ofFloat(omSymbol, "alpha", 0f, 1f);
        omFadeIn.setDuration(800);

        ObjectAnimator omScaleX = ObjectAnimator.ofFloat(omSymbol, "scaleX", 0.3f, 1f);
        omScaleX.setDuration(800);
        omScaleX.setInterpolator(new OvershootInterpolator(1.5f));

        ObjectAnimator omScaleY = ObjectAnimator.ofFloat(omSymbol, "scaleY", 0.3f, 1f);
        omScaleY.setDuration(800);
        omScaleY.setInterpolator(new OvershootInterpolator(1.5f));

        // Glow pulse effect on Om
        ObjectAnimator omGlowUp = ObjectAnimator.ofFloat(omSymbol, "scaleX", 1f, 1.1f);
        omGlowUp.setDuration(400);
        omGlowUp.setRepeatCount(1);
        omGlowUp.setRepeatMode(ObjectAnimator.REVERSE);

        ObjectAnimator omGlowUpY = ObjectAnimator.ofFloat(omSymbol, "scaleY", 1f, 1.1f);
        omGlowUpY.setDuration(400);
        omGlowUpY.setRepeatCount(1);
        omGlowUpY.setRepeatMode(ObjectAnimator.REVERSE);

        // App name Hindi: fade in + slide up
        ObjectAnimator hindiFadeIn = ObjectAnimator.ofFloat(appNameHindi, "alpha", 0f, 1f);
        hindiFadeIn.setDuration(600);

        ObjectAnimator hindiSlideUp = ObjectAnimator.ofFloat(appNameHindi, "translationY", 30f, 0f);
        hindiSlideUp.setDuration(600);
        hindiSlideUp.setInterpolator(new AccelerateDecelerateInterpolator());

        // App name English: fade in
        ObjectAnimator engFadeIn = ObjectAnimator.ofFloat(appName, "alpha", 0f, 1f);
        engFadeIn.setDuration(500);

        ObjectAnimator engSlideUp = ObjectAnimator.ofFloat(appName, "translationY", 20f, 0f);
        engSlideUp.setDuration(500);

        // Tagline: fade in
        ObjectAnimator taglineFadeIn = ObjectAnimator.ofFloat(tagline, "alpha", 0f, 1f);
        taglineFadeIn.setDuration(500);

        // Build animation sequence
        AnimatorSet omAppear = new AnimatorSet();
        omAppear.playTogether(omFadeIn, omScaleX, omScaleY);

        AnimatorSet omGlow = new AnimatorSet();
        omGlow.playTogether(omGlowUp, omGlowUpY);

        AnimatorSet hindiAppear = new AnimatorSet();
        hindiAppear.playTogether(hindiFadeIn, hindiSlideUp);

        AnimatorSet engAppear = new AnimatorSet();
        engAppear.playTogether(engFadeIn, engSlideUp);

        AnimatorSet fullSequence = new AnimatorSet();
        fullSequence.playSequentially(omAppear, omGlow, hindiAppear, engAppear, taglineFadeIn);
        fullSequence.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Wait a moment then transition to main
                binding.getRoot().postDelayed(() -> {
                    requestNotificationPermissionThenNavigate();
                }, 500);
            }
        });

        // Start after a short delay
        binding.getRoot().postDelayed(fullSequence::start, 300);
    }

    private void requestNotificationPermissionThenNavigate() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            navigateToMain();
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            navigateToMain();
            return;
        }

        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
    }

    private void navigateToMain() {
        if (proceededToMain) return;
        proceededToMain = true;
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
