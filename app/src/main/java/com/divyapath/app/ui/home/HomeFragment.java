package com.divyapath.app.ui.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.divyapath.app.R;
import com.divyapath.app.audio.AudioPlayerManager;
import com.divyapath.app.audio.AudioTrack;
import com.divyapath.app.databinding.FragmentHomeBinding;
import com.divyapath.app.ui.adapters.AartiHorizontalAdapter;
import com.divyapath.app.utils.DeityIconMapper;
import com.divyapath.app.utils.FestivalMissionData;
import com.divyapath.app.utils.PreferenceManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    private AartiHorizontalAdapter aartiAdapter;
    private AartiHorizontalAdapter popularAartiAdapter;
    private PanchangInsightAdapter insightAdapter;
    private AudioPlayerManager audioPlayerManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        audioPlayerManager = AudioPlayerManager.getInstance(requireContext());

        setupAartiRecyclerView();
        setupPopularAartis();
        setupInsightsRecyclerView();
        observeViewModel();
        observeAudioPlayer();
        setupClickListeners();
        setupAdBanner();
    }

    private void setupAartiRecyclerView() {
        aartiAdapter = new AartiHorizontalAdapter(aarti -> {
            Bundle args = new Bundle();
            args.putInt("contentId", aarti.getId());
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_homeFragment_to_aartiDetailFragment, args);
        });

        binding.rvTodaysAartis.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvTodaysAartis.setAdapter(aartiAdapter);
    }

    private void setupInsightsRecyclerView() {
        insightAdapter = new PanchangInsightAdapter();
        binding.rvPanchangInsights.setLayoutManager(
                new LinearLayoutManager(requireContext()));
        binding.rvPanchangInsights.setAdapter(insightAdapter);
    }

    private void setupPopularAartis() {
        popularAartiAdapter = new AartiHorizontalAdapter(aarti -> {
            Bundle args = new Bundle();
            args.putInt("contentId", aarti.getId());
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_homeFragment_to_aartiDetailFragment, args);
        });
        binding.rvPopularAartis.setLayoutManager(
                new LinearLayoutManager(requireContext()));
        binding.rvPopularAartis.setAdapter(popularAartiAdapter);
    }

    private void observeViewModel() {
        // Greeting
        viewModel.getGreeting().observe(getViewLifecycleOwner(), greeting ->
                binding.tvGreeting.setText(greeting));

        // Date
        viewModel.getDateText().observe(getViewLifecycleOwner(), date ->
                binding.tvDate.setText(date));

        // Panchang
        viewModel.getPanchangData().observe(getViewLifecycleOwner(), this::updatePanchang);

        // Panchang Insights
        viewModel.getPanchangInsights().observe(getViewLifecycleOwner(), insights -> {
            if (insights != null && !insights.isEmpty()) {
                binding.layoutInsights.setVisibility(View.VISIBLE);
                insightAdapter.submitList(insights);
            } else {
                binding.layoutInsights.setVisibility(View.GONE);
            }
        });

        // Today's Deity
        viewModel.getTodaysDeity().observe(getViewLifecycleOwner(), deity -> {
            if (deity != null) {
                binding.tvDeityName.setText(deity.getName());
                binding.tvDeityNameHindi.setText(deity.getHindiName());
                binding.tvDeityDescription.setText(deity.getDescription());

                int imageRes = getDeityImageResource(deity.getImageUrl());
                if (imageRes != 0) {
                    binding.ivDeity.setImageResource(imageRes);
                }
            }
        });

        // Today's Aartis (deity-specific for the day)
        viewModel.getTodaysAartis().observe(getViewLifecycleOwner(), aartis -> {
            if (aartis != null) {
                aartiAdapter.submitList(aartis);
            }
        });

        // Popular Aartis (separate dataset)
        viewModel.getPopularAartis().observe(getViewLifecycleOwner(), aartis -> {
            if (aartis != null) {
                popularAartiAdapter.submitList(aartis);
            }
        });

        // Daily Shloka
        viewModel.getDailyShloka().observe(getViewLifecycleOwner(), shloka -> {
            if (shloka != null) {
                binding.tvShlokaSanskrit.setText(shloka.sanskrit);
                binding.tvShlokaTranslation.setText(shloka.translation);
                binding.tvShlokaSource.setText(shloka.source);
            }
        });

        // Today's Festival (conditional)
        viewModel.getTodaysFestival().observe(getViewLifecycleOwner(), festival -> {
            if (festival != null) {
                binding.cardFestival.setVisibility(View.VISIBLE);
                binding.tvFestivalName.setText(
                        festival.getNameHindi() != null ? festival.getNameHindi() : festival.getName());
                binding.tvFestivalDescription.setText(festival.getDescription());
            } else {
                binding.cardFestival.setVisibility(View.GONE);
            }
        });

        // Today's Seva
        viewModel.getTodaysSeva().observe(getViewLifecycleOwner(), seva -> {
            if (seva != null) {
                binding.tvSevaHomeTitle.setText(seva.title);
            }
        });
        viewModel.getSevaCompletedToday().observe(getViewLifecycleOwner(), done ->
                binding.tvSevaHomeStatus.setText(Boolean.TRUE.equals(done) ? "Done" : "Pending"));
        binding.cardSeva.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_sevaFragment));

        // Next Festival Countdown (show prep card if within 7 days and has missions)
        viewModel.getNextFestival().observe(getViewLifecycleOwner(), festival -> {
            if (festival != null && festival.getDate() != null) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    Date festDate = sdf.parse(festival.getDate());
                    Date today = sdf.parse(sdf.format(new Date()));
                    if (festDate != null && today != null) {
                        long diff = festDate.getTime() - today.getTime();
                        int daysRemaining = (int) TimeUnit.MILLISECONDS.toDays(diff);
                        String name = festival.getName();
                        if (daysRemaining >= 0 && daysRemaining <= 7 && FestivalMissionData.hasMissions(name)) {
                            binding.cardFestivalCountdown.setVisibility(View.VISIBLE);
                            binding.tvCountdownLabel.setText(daysRemaining + " days to " + name);
                            binding.btnStartPrep.setOnClickListener(v -> {
                                Bundle args = new Bundle();
                                args.putString("festivalName", name);
                                args.putInt("daysRemaining", daysRemaining);
                                Navigation.findNavController(v)
                                        .navigate(R.id.action_homeFragment_to_festivalCountdown, args);
                            });
                        } else {
                            binding.cardFestivalCountdown.setVisibility(View.GONE);
                        }
                    }
                } catch (Exception e) {
                    binding.cardFestivalCountdown.setVisibility(View.GONE);
                }
            } else {
                binding.cardFestivalCountdown.setVisibility(View.GONE);
            }
        });
    }

    private void observeAudioPlayer() {
        // Continue Listening card — observe current track from singleton
        audioPlayerManager.getCurrentTrackLiveData().observe(getViewLifecycleOwner(), track -> {
            if (track != null) {
                binding.cardContinueListening.setVisibility(View.VISIBLE);
                binding.tvContinueTitle.setText(track.getTitle());
                binding.tvContinueSubtitle.setText(
                        track.getDeityName() != null ? track.getDeityName() : track.getSubtitle());
            } else {
                binding.cardContinueListening.setVisibility(View.GONE);
            }
        });

        // Update play/pause icon on Continue Listening card
        audioPlayerManager.getIsPlayingLiveData().observe(getViewLifecycleOwner(), isPlaying -> {
            if (binding.btnContinuePlay != null) {
                binding.btnContinuePlay.setImageResource(
                        Boolean.TRUE.equals(isPlaying) ? R.drawable.ic_pause : R.drawable.ic_play);
            }
        });
    }

    private void updatePanchang(Map<String, String> panchang) {
        if (panchang == null) return;

        binding.tvTithi.setText(panchang.get("tithi"));
        binding.tvNakshatra.setText(panchang.get("nakshatra"));
        binding.tvSunrise.setText(panchang.get("sunrise"));
        binding.tvSunset.setText(panchang.get("sunset"));
        binding.tvRahukaal.setText(panchang.get("rahukaal"));
        binding.tvAbhijit.setText(panchang.get("abhijit_muhurat"));
        binding.tvVara.setText(panchang.get("vara"));
        binding.tvYoga.setText(panchang.get("yoga"));

        // Show location indicator with effective timezone
        PreferenceManager prefs = new PreferenceManager(requireContext());
        String locName = prefs.getLocationName();
        String tz = prefs.getEffectiveTimezone();
        try {
            TimeZone timezone = TimeZone.getTimeZone(tz);
            String shortTz = timezone.getDisplayName(false, TimeZone.SHORT);
            binding.tvPanchangLocation.setText("Showing for " + locName + ", " + shortTz);
        } catch (Exception e) {
            binding.tvPanchangLocation.setText("Showing for " + locName);
        }
    }

    private void setupClickListeners() {
        // Quick action buttons
        binding.btnQuickAarti.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_aartiListFragment));

        binding.btnQuickChalisa.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_chalisaListFragment));

        binding.btnQuickMantra.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.nav_mantra));

        binding.btnQuickCalendar.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.nav_panchang));

        // Panchang "View All"
        binding.tvPanchangMore.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.nav_panchang));

        // Aartis "View All"
        binding.tvAartisViewAll.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_aartiListFragment));

        // Deity card click
        binding.cardDeity.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_aartiListFragment));

        // Continue Listening play button
        binding.btnContinuePlay.setOnClickListener(v ->
                audioPlayerManager.togglePlayPause());

        // Continue Listening card tap — open full player
        binding.cardContinueListening.setOnClickListener(v -> {
            try {
                Navigation.findNavController(v).navigate(R.id.audioPlayerFragment);
            } catch (Exception e) {
                android.util.Log.w("HomeFragment", "Navigation to audio player failed", e);
            }
        });

        // Affiliate banner — Astrotalk
        binding.btnAstrotalk.setOnClickListener(v ->
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.astrotalk.com/?ref=divyapath"))));
    }

    private void setupAdBanner() {
        AdView adView = new AdView(requireContext());
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(getString(R.string.admob_banner_id));
        binding.adContainer.addView(adView);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private int getDeityImageResource(String imageUrl) {
        return DeityIconMapper.getIconForImageUrl(imageUrl);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
