package com.divyapath.app.ui.settings;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import com.divyapath.app.DivyaPathApp;
import com.divyapath.app.R;
import com.divyapath.app.data.remote.api.GeocodingApiService;
import com.divyapath.app.data.remote.api.RetrofitClient;
import com.divyapath.app.data.remote.dto.GeocodingResponse;
import com.divyapath.app.data.remote.dto.GeocodingResult;
import com.divyapath.app.databinding.FragmentSettingsBinding;
import com.divyapath.app.utils.CityData;
import com.divyapath.app.utils.PanchangCalculator;
import com.divyapath.app.utils.PreferenceManager;
import com.divyapath.app.utils.ShareHelper;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsFragment extends Fragment {
    private static final String TAG = "SettingsFragment";
    private static final long SEARCH_DEBOUNCE_MS = 300;

    private FragmentSettingsBinding binding;
    private PreferenceManager prefs;
    private CityListAdapter cityAdapter;
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable pendingSearch;
    private Call<GeocodingResponse> currentApiCall;
    private boolean isSearchingApi = false;
    private FusedLocationProviderClient fusedLocationClient;

    private final ActivityResultLauncher<String> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    fetchGpsLocation();
                } else {
                    Toast.makeText(requireContext(), "Location permission denied. Using saved location.", Toast.LENGTH_SHORT).show();
                    fallbackToSavedLocation();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle b) {
        binding = FragmentSettingsBinding.inflate(i, c, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        super.onViewCreated(v, b);
        prefs = new PreferenceManager(requireContext());

        bindUserSettings();
        bindPlaybackSettings();
        bindAudioQualitySettings();
        bindFontSizeSettings();
        bindThemeSettings();
        bindLocationSettings();
        bindAboutSection();
        binding.toolbarSettings.setNavigationOnClickListener(x ->
                androidx.navigation.Navigation.findNavController(v).popBackStack());
    }

    private void bindUserSettings() {
        binding.etUserName.setText(prefs.getUserName());
        binding.switchNotification.setChecked(prefs.isMorningNotificationEnabled());
        binding.switchFestival.setChecked(prefs.isEveningNotificationEnabled());

        binding.etUserName.setOnFocusChangeListener((view, focused) -> {
            if (!focused) {
                String name = binding.etUserName.getText() == null
                        ? ""
                        : binding.etUserName.getText().toString().trim();
                if (!name.isEmpty()) {
                    prefs.setUserName(name);
                }
            }
        });
        binding.switchNotification.setOnCheckedChangeListener((btn, on) -> {
            prefs.setMorningNotification(on);
            if (on) com.divyapath.app.utils.NotificationScheduler.scheduleMorningReminder(requireContext(), 6, 0);
            else androidx.work.WorkManager.getInstance(requireContext()).cancelUniqueWork("morning_reminder");
        });
        binding.switchFestival.setOnCheckedChangeListener((btn, on) -> {
            prefs.setEveningNotification(on);
            if (on) {
                com.divyapath.app.utils.NotificationScheduler.scheduleEveningReminder(requireContext(), 19, 0);
            } else {
                androidx.work.WorkManager.getInstance(requireContext()).cancelUniqueWork("evening_reminder");
            }
            // Festival alerts are always active — independent of evening toggle
        });
    }

    private void bindPlaybackSettings() {
        switch (prefs.getAartiPlaybackMode()) {
            case PreferenceManager.AARTI_PLAYBACK_SING:
                binding.rbPlaybackSing.setChecked(true);
                break;
            case PreferenceManager.AARTI_PLAYBACK_READ:
                binding.rbPlaybackRead.setChecked(true);
                break;
            default:
                binding.rbPlaybackAuto.setChecked(true);
                break;
        }

        binding.rgAartiPlayback.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == binding.rbPlaybackSing.getId()) {
                prefs.setAartiPlaybackMode(PreferenceManager.AARTI_PLAYBACK_SING);
            } else if (checkedId == binding.rbPlaybackRead.getId()) {
                prefs.setAartiPlaybackMode(PreferenceManager.AARTI_PLAYBACK_READ);
            } else if (checkedId == binding.rbPlaybackAuto.getId()) {
                prefs.setAartiPlaybackMode(PreferenceManager.AARTI_PLAYBACK_AUTO);
            }
        });
    }

    private void bindAudioQualitySettings() {
        switch (prefs.getAudioQuality()) {
            case "low":
                binding.rbQualityLow.setChecked(true);
                break;
            case "high":
                binding.rbQualityHigh.setChecked(true);
                break;
            default:
                binding.rbQualityMedium.setChecked(true);
                break;
        }

        binding.rgAudioQuality.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == binding.rbQualityLow.getId()) {
                prefs.setAudioQuality("low");
            } else if (checkedId == binding.rbQualityHigh.getId()) {
                prefs.setAudioQuality("high");
            } else {
                prefs.setAudioQuality("medium");
            }
        });
    }

    private void bindFontSizeSettings() {
        float currentSize = prefs.getFontSize();
        binding.sliderFontSize.setValue(currentSize);
        binding.tvFontPreview.setTextSize(currentSize);

        binding.sliderFontSize.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                prefs.setFontSize(value);
                binding.tvFontPreview.setTextSize(value);
            }
        });
    }

    private void bindThemeSettings() {
        binding.rgTheme.setOnCheckedChangeListener((g, id) -> {
            String theme;
            if (id == binding.rbLight.getId()) {
                theme = "light";
            } else if (id == binding.rbDark.getId()) {
                theme = "dark";
            } else {
                theme = "saffron";
            }
            prefs.setTheme(theme);
            DivyaPathApp.applyTheme(theme);
        });

        switch (prefs.getTheme()) {
            case "dark":
                binding.rbDark.setChecked(true);
                break;
            case "saffron":
                binding.rbSaffron.setChecked(true);
                break;
            default:
                binding.rbLight.setChecked(true);
                break;
        }
    }

    private void bindLocationSettings() {
        // Setup city adapter
        cityAdapter = new CityListAdapter(city -> {
            prefs.setFullLocation(city.getName(), city.getCountryCode(),
                    city.getLatitude(), city.getLongitude(), city.getTimezone());
            prefs.setLocationMode("city");
            updateLocationPreview();
        });
        binding.rvCities.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvCities.setAdapter(cityAdapter);

        // Region chips
        setupRegionChips();

        // City search — debounced API call with offline fallback
        binding.etCitySearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                // Cancel any pending search
                if (pendingSearch != null) {
                    searchHandler.removeCallbacks(pendingSearch);
                }
                if (currentApiCall != null) {
                    currentApiCall.cancel();
                }

                String query = s.toString().trim();
                if (query.length() >= 2) {
                    // Debounce 300ms then search
                    pendingSearch = () -> searchCitiesWithApi(query);
                    searchHandler.postDelayed(pendingSearch, SEARCH_DEBOUNCE_MS);
                } else if (query.isEmpty()) {
                    setSearchLoading(false);
                    cityAdapter.setCities(CityData.getCitiesByRegion("India"));
                }
            }
        });

        // Location mode radio group
        String mode = prefs.getLocationMode();
        switch (mode) {
            case "gps":
                binding.rbLocationGps.setChecked(true);
                showGpsMode();
                break;
            case "manual":
                binding.rbLocationManual.setChecked(true);
                showManualMode();
                break;
            default:
                binding.rbLocationCity.setChecked(true);
                showCityMode();
                break;
        }

        binding.rgLocationMode.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_location_city) {
                prefs.setLocationMode("city");
                showCityMode();
            } else if (checkedId == R.id.rb_location_gps) {
                prefs.setLocationMode("gps");
                showGpsMode();
            } else if (checkedId == R.id.rb_location_manual) {
                prefs.setLocationMode("manual");
                showManualMode();
            }
        });

        // Manual coordinate apply button
        binding.btnApplyCoords.setOnClickListener(v -> {
            try {
                double lat = Double.parseDouble(binding.etLatitude.getText().toString().trim());
                double lon = Double.parseDouble(binding.etLongitude.getText().toString().trim());
                CityData.City closest = CityData.findClosest(lat, lon);
                if (closest != null) {
                    prefs.setFullLocation(closest.getName(), closest.getCountryCode(),
                            lat, lon, closest.getTimezone());
                } else {
                    prefs.setLocation(lat, lon);
                }
                updateLocationPreview();
            } catch (NumberFormatException ignored) {
                // Invalid input
            }
        });

        // Timezone display
        String tzDisplay = prefs.getTimezoneDisplay();
        switch (tzDisplay) {
            case "ist":
                binding.rbTzIst.setChecked(true);
                break;
            case "device":
                binding.rbTzDevice.setChecked(true);
                break;
            default:
                binding.rbTzLocal.setChecked(true);
                break;
        }
        binding.rgTimezoneDisplay.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_tz_ist) {
                prefs.setTimezoneDisplay("ist");
            } else if (checkedId == R.id.rb_tz_device) {
                prefs.setTimezoneDisplay("device");
            } else {
                prefs.setTimezoneDisplay("local");
            }
            updateLocationPreview();
        });

        // Set selected city in adapter
        cityAdapter.setSelectedCity(prefs.getLocationName());

        // Show initial cities (India region)
        cityAdapter.setCities(CityData.getCitiesByRegion("India"));

        // Show manual coords if applicable
        binding.etLatitude.setText(String.valueOf(prefs.getLocationLat()));
        binding.etLongitude.setText(String.valueOf(prefs.getLocationLon()));

        // Initial preview
        updateLocationPreview();
    }

    /**
     * Search cities using Open-Meteo Geocoding API, with offline fallback to CityData.
     */
    private void searchCitiesWithApi(String query) {
        if (binding == null) return;
        setSearchLoading(true);

        GeocodingApiService api = RetrofitClient.getGeocodingService();
        currentApiCall = api.searchCities(query, 15, "en");
        currentApiCall.enqueue(new Callback<GeocodingResponse>() {
            @Override
            public void onResponse(@NonNull Call<GeocodingResponse> call,
                                   @NonNull Response<GeocodingResponse> response) {
                if (binding == null) return;
                setSearchLoading(false);

                if (response.isSuccessful() && response.body() != null
                        && response.body().getResults() != null
                        && !response.body().getResults().isEmpty()) {
                    // Convert API results to CityData.City list
                    List<CityData.City> apiCities = new ArrayList<>();
                    for (GeocodingResult result : response.body().getResults()) {
                        apiCities.add(result.toCityDataCity());
                    }
                    cityAdapter.setCities(apiCities);
                } else {
                    // API returned empty — fall back to offline search
                    fallbackToOfflineSearch(query);
                }
            }

            @Override
            public void onFailure(@NonNull Call<GeocodingResponse> call, @NonNull Throwable t) {
                if (call.isCanceled()) return;
                if (binding == null) return;
                setSearchLoading(false);
                Log.d(TAG, "API search failed, falling back to offline: " + t.getMessage());
                fallbackToOfflineSearch(query);
            }
        });
    }

    private void fallbackToOfflineSearch(String query) {
        List<CityData.City> offlineResults = CityData.searchCities(query);
        cityAdapter.setCities(offlineResults);
    }

    private void setSearchLoading(boolean loading) {
        if (binding != null && binding.progressCitySearch != null) {
            binding.progressCitySearch.setVisibility(loading ? View.VISIBLE : View.GONE);
        }
    }

    private void setupRegionChips() {
        binding.chipGroupRegions.removeAllViews();
        for (String region : CityData.getRegions()) {
            Chip chip = new Chip(requireContext());
            chip.setText(region);
            chip.setCheckable(true);
            chip.setCheckedIconVisible(true);
            if ("India".equals(region)) chip.setChecked(true);
            chip.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                if (isChecked) {
                    binding.etCitySearch.setText("");
                    cityAdapter.setCities(CityData.getCitiesByRegion(region));
                }
            });
            binding.chipGroupRegions.addView(chip);
        }
    }

    private void showCityMode() {
        binding.layoutCityPicker.setVisibility(View.VISIBLE);
        binding.layoutManualCoords.setVisibility(View.GONE);
    }

    private void showGpsMode() {
        binding.layoutCityPicker.setVisibility(View.GONE);
        binding.layoutManualCoords.setVisibility(View.GONE);

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Check and request location permission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fetchGpsLocation();
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    @SuppressWarnings("MissingPermission")
    private void fetchGpsLocation() {
        if (fusedLocationClient == null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        }

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null && binding != null) {
                        double lat = location.getLatitude();
                        double lon = location.getLongitude();

                        // Find the closest known city for timezone and name
                        CityData.City closest = CityData.findClosest(lat, lon);
                        if (closest != null) {
                            prefs.setFullLocation(closest.getName(), closest.getCountryCode(),
                                    lat, lon, closest.getTimezone());
                        } else {
                            prefs.setLocation(lat, lon);
                        }
                        updateLocationPreview();
                        Log.d(TAG, "GPS location obtained: " + lat + ", " + lon);
                    } else {
                        fallbackToSavedLocation();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "GPS location failed", e);
                    if (binding != null) {
                        Toast.makeText(requireContext(), "Could not get GPS location. Using saved location.", Toast.LENGTH_SHORT).show();
                        fallbackToSavedLocation();
                    }
                });
    }

    private void fallbackToSavedLocation() {
        CityData.City closest = CityData.findClosest(prefs.getLocationLat(), prefs.getLocationLon());
        if (closest != null) {
            prefs.setFullLocation(closest.getName(), closest.getCountryCode(),
                    closest.getLatitude(), closest.getLongitude(), closest.getTimezone());
            updateLocationPreview();
        }
    }

    private void showManualMode() {
        binding.layoutCityPicker.setVisibility(View.GONE);
        binding.layoutManualCoords.setVisibility(View.VISIBLE);
    }

    /** Resolves the effective timezone based on the user's timezone display preference. */
    private String getEffectiveTimezone() {
        return prefs.getEffectiveTimezone();
    }

    private void updateLocationPreview() {
        String locName = prefs.getLocationName();
        String countryCode = prefs.getLocationCountryCode();
        String tz = getEffectiveTimezone();

        binding.tvLocationName.setText(locName + ", " + countryCode);
        try {
            TimeZone timezone = TimeZone.getTimeZone(tz);
            String shortName = timezone.getDisplayName(false, TimeZone.SHORT);
            binding.tvLocationTimezone.setText(tz + " (" + shortName + ")");
        } catch (Exception e) {
            binding.tvLocationTimezone.setText(tz);
        }

        // Get panchang data for preview
        try {
            Map<String, String> panchang = PanchangCalculator.getPanchangForLocation(
                    prefs.getLocationLat(), prefs.getLocationLon(), tz);
            binding.tvPreviewSunrise.setText("Sunrise: " + panchang.getOrDefault("sunrise", "--"));
            binding.tvPreviewSunset.setText("Sunset: " + panchang.getOrDefault("sunset", "--"));
            binding.tvPreviewRahukaal.setText("Rahukaal: " + panchang.getOrDefault("rahukaal", "--"));
        } catch (Exception e) {
            binding.tvPreviewSunrise.setText("Sunrise: --");
            binding.tvPreviewSunset.setText("Sunset: --");
            binding.tvPreviewRahukaal.setText("Rahukaal: --");
        }
    }

    private void bindAboutSection() {
        binding.btnRateUs.setOnClickListener(v -> {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" + requireContext().getPackageName())));
            } catch (Exception e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + requireContext().getPackageName())));
            }
        });

        binding.btnShareApp.setOnClickListener(v ->
                ShareHelper.shareApp(requireContext()));

        binding.btnPrivacyPolicy.setOnClickListener(v ->
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://divyapath.app/privacy"))));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (pendingSearch != null) {
            searchHandler.removeCallbacks(pendingSearch);
        }
        if (currentApiCall != null) {
            currentApiCall.cancel();
        }
        binding = null;
    }
}
