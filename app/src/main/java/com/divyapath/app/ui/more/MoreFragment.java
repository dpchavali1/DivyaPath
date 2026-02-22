package com.divyapath.app.ui.more;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.divyapath.app.R;

public class MoreFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_more, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.card_japa).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_moreFragment_to_japaCounter));
        view.findViewById(R.id.card_puja).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_moreFragment_to_pujaTierSelection));
        view.findViewById(R.id.card_chalisa).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_moreFragment_to_chalisaListFragment));
        view.findViewById(R.id.card_shraddha).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_moreFragment_to_shraddhaList));
        view.findViewById(R.id.card_darshan).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_moreFragment_to_darshanFragment));
        view.findViewById(R.id.card_bhajan).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_moreFragment_to_bhajanListFragment));
        view.findViewById(R.id.card_stotra).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_moreFragment_to_stotraListFragment));
        view.findViewById(R.id.card_store).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_moreFragment_to_storeFragment));
        view.findViewById(R.id.card_settings).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_moreFragment_to_settingsFragment));
        view.findViewById(R.id.card_seva).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_moreFragment_to_sevaFragment));
        view.findViewById(R.id.card_wallpaper).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_moreFragment_to_wallpaperFragment));
        view.findViewById(R.id.card_astrology).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_moreFragment_to_astrologyFragment));
        view.findViewById(R.id.card_pandit).setOnClickListener(v ->
                startActivity(new android.content.Intent(android.content.Intent.ACTION_VIEW,
                        android.net.Uri.parse("https://www.99pandit.com/?ref=divyapath"))));
    }
}
