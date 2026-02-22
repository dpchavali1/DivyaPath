package com.divyapath.app.ui.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.divyapath.app.R;
import com.divyapath.app.data.local.entity.AartiEntity;
import com.divyapath.app.data.local.entity.ChalisaEntity;
import com.divyapath.app.data.local.entity.MantraEntity;
import com.divyapath.app.data.repository.DivyaPathRepository;
import com.divyapath.app.databinding.FragmentSearchBinding;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private static final String[] POPULAR = {
            "Ganesh", "Hanuman", "Shiva", "Lakshmi", "Om", "Gayatri", "Durga", "Krishna"
    };

    private FragmentSearchBinding binding;
    private DivyaPathRepository repo;
    private SearchResultAdapter adapter;

    private LiveData<List<AartiEntity>> aartiSource;
    private LiveData<List<ChalisaEntity>> chalisaSource;
    private LiveData<List<MantraEntity>> mantraSource;
    private int searchGeneration = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repo = new DivyaPathRepository(requireActivity().getApplication());
        adapter = new SearchResultAdapter(item -> {
            Bundle args = new Bundle();
            args.putInt("contentId", item.id);
            Navigation.findNavController(view).navigate(item.navDestination, args);
        });

        binding.rvSearchResults.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvSearchResults.setAdapter(adapter);

        for (String term : POPULAR) {
            Chip chip = new Chip(requireContext());
            chip.setText(term);
            chip.setCheckable(false);
            chip.setChipBackgroundColorResource(R.color.cream_background);
            chip.setTextColor(getResources().getColor(R.color.saffron_primary, null));
            chip.setOnClickListener(v -> binding.searchView.setQuery(term, true));
            binding.chipPopular.addView(chip);
        }

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (query.length() >= 2) {
                    performSearch(query);
                } else {
                    clearResults();
                }
                return true;
            }
        });

        binding.toolbarSearch.setNavigationOnClickListener(v ->
                Navigation.findNavController(view).popBackStack());
    }

    private void performSearch(String query) {
        final int generation = ++searchGeneration;
        detachSearchSources();

        final boolean[] loaded = {false, false, false};
        final List<SearchResultAdapter.SearchItem> aartiResults = new ArrayList<>();
        final List<SearchResultAdapter.SearchItem> chalisaResults = new ArrayList<>();
        final List<SearchResultAdapter.SearchItem> mantraResults = new ArrayList<>();

        aartiSource = repo.searchAartis(query);
        chalisaSource = repo.searchChalisas(query);
        mantraSource = repo.searchMantras(query);

        aartiSource.observe(getViewLifecycleOwner(), aartis -> {
            if (!isActiveGeneration(generation)) return;
            aartiResults.clear();
            if (aartis != null) {
                for (AartiEntity a : aartis) {
                    aartiResults.add(new SearchResultAdapter.SearchItem(
                            a.getId(),
                            a.getTitle(),
                            a.getTitleHindi() != null ? a.getTitleHindi() : "",
                            "Aarti",
                            R.id.aartiDetailFragment
                    ));
                }
            }
            loaded[0] = true;
            renderSearchResultsIfReady(generation, loaded, aartiResults, chalisaResults, mantraResults);
        });

        chalisaSource.observe(getViewLifecycleOwner(), chalisas -> {
            if (!isActiveGeneration(generation)) return;
            chalisaResults.clear();
            if (chalisas != null) {
                for (ChalisaEntity c : chalisas) {
                    chalisaResults.add(new SearchResultAdapter.SearchItem(
                            c.getId(),
                            c.getTitle(),
                            c.getTitleHindi() != null ? c.getTitleHindi() : "",
                            "Chalisa",
                            R.id.chalisaDetailFragment
                    ));
                }
            }
            loaded[1] = true;
            renderSearchResultsIfReady(generation, loaded, aartiResults, chalisaResults, mantraResults);
        });

        mantraSource.observe(getViewLifecycleOwner(), mantras -> {
            if (!isActiveGeneration(generation)) return;
            mantraResults.clear();
            if (mantras != null) {
                for (MantraEntity m : mantras) {
                    mantraResults.add(new SearchResultAdapter.SearchItem(
                            m.getId(),
                            m.getTitle(),
                            m.getSanskrit() != null ? m.getSanskrit() : "",
                            "Mantra",
                            R.id.mantraDetailFragment
                    ));
                }
            }
            loaded[2] = true;
            renderSearchResultsIfReady(generation, loaded, aartiResults, chalisaResults, mantraResults);
        });
    }

    private boolean isActiveGeneration(int generation) {
        return generation == searchGeneration && binding != null;
    }

    private void renderSearchResultsIfReady(
            int generation,
            boolean[] loaded,
            List<SearchResultAdapter.SearchItem> aartiResults,
            List<SearchResultAdapter.SearchItem> chalisaResults,
            List<SearchResultAdapter.SearchItem> mantraResults
    ) {
        if (!isActiveGeneration(generation)) return;
        if (!loaded[0] || !loaded[1] || !loaded[2]) return;

        List<SearchResultAdapter.SearchItem> all = new ArrayList<>(
                aartiResults.size() + chalisaResults.size() + mantraResults.size());
        all.addAll(aartiResults);
        all.addAll(chalisaResults);
        all.addAll(mantraResults);

        adapter.submitList(all);
        binding.rvSearchResults.setVisibility(all.isEmpty() ? View.GONE : View.VISIBLE);
        binding.tvNoResults.setVisibility(all.isEmpty() ? View.VISIBLE : View.GONE);
        binding.tvSearchHint.setVisibility(View.GONE);
        binding.chipPopular.setVisibility(View.GONE);
    }

    private void clearResults() {
        searchGeneration++;
        detachSearchSources();
        adapter.submitList(new ArrayList<>());
        binding.rvSearchResults.setVisibility(View.GONE);
        binding.tvNoResults.setVisibility(View.GONE);
        binding.tvSearchHint.setVisibility(View.VISIBLE);
        binding.chipPopular.setVisibility(View.VISIBLE);
    }

    private void detachSearchSources() {
        if (aartiSource != null) {
            aartiSource.removeObservers(getViewLifecycleOwner());
            aartiSource = null;
        }
        if (chalisaSource != null) {
            chalisaSource.removeObservers(getViewLifecycleOwner());
            chalisaSource = null;
        }
        if (mantraSource != null) {
            mantraSource.removeObservers(getViewLifecycleOwner());
            mantraSource = null;
        }
    }

    @Override
    public void onDestroyView() {
        detachSearchSources();
        super.onDestroyView();
        binding = null;
    }
}
