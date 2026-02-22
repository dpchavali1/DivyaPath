package com.divyapath.app.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/**
 * LiveData wrapper for network connectivity status.
 * Observe to show/hide offline banners.
 */
public class NetworkMonitor {

    private static volatile NetworkMonitor INSTANCE;
    private final MutableLiveData<Boolean> isConnected = new MutableLiveData<>(true);
    private final ConnectivityManager connectivityManager;

    private NetworkMonitor(Context context) {
        connectivityManager = (ConnectivityManager)
                context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        registerCallback();
        // Check initial state
        checkCurrentNetwork();
    }

    public static NetworkMonitor getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (NetworkMonitor.class) {
                if (INSTANCE == null) {
                    INSTANCE = new NetworkMonitor(context);
                }
            }
        }
        return INSTANCE;
    }

    public LiveData<Boolean> getIsConnected() {
        return isConnected;
    }

    public boolean isCurrentlyConnected() {
        return Boolean.TRUE.equals(isConnected.getValue());
    }

    private void checkCurrentNetwork() {
        if (connectivityManager == null) {
            isConnected.postValue(false);
            return;
        }
        Network activeNetwork = connectivityManager.getActiveNetwork();
        if (activeNetwork == null) {
            isConnected.postValue(false);
            return;
        }
        NetworkCapabilities caps = connectivityManager.getNetworkCapabilities(activeNetwork);
        isConnected.postValue(caps != null &&
                (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                 caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                 caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)));
    }

    private void registerCallback() {
        if (connectivityManager == null) return;
        NetworkRequest request = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();

        connectivityManager.registerNetworkCallback(request, new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                isConnected.postValue(true);
            }

            @Override
            public void onLost(@NonNull Network network) {
                isConnected.postValue(false);
            }

            @Override
            public void onUnavailable() {
                isConnected.postValue(false);
            }
        });
    }
}
