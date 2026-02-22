package com.divyapath.app.utils;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

/**
 * Singleton manager for AdMob ads (interstitial, rewarded, banner).
 * Interstitials are shown every {@link #INTERSTITIAL_FREQUENCY} content closures.
 */
public class AdManager {

    private static volatile AdManager INSTANCE;

    private InterstitialAd interstitialAd;
    private RewardedAd rewardedAd;
    private int contentCloseCount = 0;

    private static final int INTERSTITIAL_FREQUENCY = 3;
    private static final String INTERSTITIAL_ID = "ca-app-pub-4962910048695842/2672343176";
    private static final String REWARDED_ID = "ca-app-pub-4962910048695842/8104128679";
    public static final String BANNER_ID = "ca-app-pub-4962910048695842/5477965331";

    private AdManager() {}

    public static AdManager getInstance() {
        if (INSTANCE == null) {
            synchronized (AdManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AdManager();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Convenience overload — context is unused since AdManager is stateless regarding context.
     */
    public static AdManager getInstance(Context context) {
        return getInstance();
    }

    // --- Reward Callback ---

    public interface RewardCallback {
        void onRewarded();
    }

    // --- Interstitial Ads ---

    public void loadInterstitial(Context context) {
        AdRequest req = new AdRequest.Builder().build();
        InterstitialAd.load(context, INTERSTITIAL_ID, req, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd ad) {
                interstitialAd = ad;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError e) {
                interstitialAd = null;
            }
        });
    }

    /**
     * Called when the user closes a content screen (aarti detail, chalisa, etc.).
     * Shows an interstitial every {@link #INTERSTITIAL_FREQUENCY} closures.
     */
    public void onContentClosed(Activity activity) {
        contentCloseCount++;
        if (contentCloseCount % INTERSTITIAL_FREQUENCY == 0 && interstitialAd != null) {
            interstitialAd.show(activity);
            interstitialAd = null;
            loadInterstitial(activity);
        }
    }

    // --- Rewarded Ads ---

    public void loadRewarded(Context context) {
        AdRequest req = new AdRequest.Builder().build();
        RewardedAd.load(context, REWARDED_ID, req, new RewardedAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull RewardedAd ad) {
                rewardedAd = ad;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError e) {
                rewardedAd = null;
            }
        });
    }

    /**
     * Show a rewarded ad with a simple callback.
     * Callers should check {@link #isRewardedReady()} before calling.
     */
    public void showRewardedAd(Activity activity, RewardCallback callback) {
        if (rewardedAd != null) {
            rewardedAd.show(activity, reward -> {
                if (callback != null) callback.onRewarded();
            });
            rewardedAd = null;
            loadRewarded(activity);
        }
        // If ad not loaded, do NOT grant reward — caller should check isRewardedReady() first
    }

    /**
     * Show a rewarded ad with the standard Google listener.
     * Returns true if the ad was shown, false if not ready.
     */
    public boolean showRewarded(Activity activity, OnUserEarnedRewardListener listener) {
        if (rewardedAd != null) {
            rewardedAd.show(activity, listener);
            rewardedAd = null;
            loadRewarded(activity);
            return true;
        }
        return false;
    }

    public boolean isRewardedReady() {
        return rewardedAd != null;
    }
}
