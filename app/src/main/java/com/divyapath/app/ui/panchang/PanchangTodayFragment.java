package com.divyapath.app.ui.panchang;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.divyapath.app.R;
import com.divyapath.app.utils.PanchangCalculator;
import com.divyapath.app.utils.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class PanchangTodayFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_panchang_today, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        PreferenceManager prefs = new PreferenceManager(requireContext());
        String effectiveTimezone = prefs.getEffectiveTimezone();

        Map<String, String> panchang = PanchangCalculator.getPanchangForLocation(
                prefs.getLocationLat(),
                prefs.getLocationLon(),
                effectiveTimezone
        );

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone(effectiveTimezone));

        TextView tvDate = view.findViewById(R.id.tv_today_date);
        TextView tvVara = view.findViewById(R.id.tv_today_vara);
        tvDate.setText(dateFormat.format(new Date()));
        tvVara.setText(panchang.get("vara"));

        ((TextView) view.findViewById(R.id.tv_tithi)).setText(panchang.get("tithi"));
        ((TextView) view.findViewById(R.id.tv_nakshatra)).setText(panchang.get("nakshatra"));
        ((TextView) view.findViewById(R.id.tv_yoga)).setText(panchang.get("yoga"));
        ((TextView) view.findViewById(R.id.tv_karana)).setText(panchang.get("karana"));
        ((TextView) view.findViewById(R.id.tv_sunrise)).setText(panchang.get("sunrise"));
        ((TextView) view.findViewById(R.id.tv_sunset)).setText(panchang.get("sunset"));
        ((TextView) view.findViewById(R.id.tv_moonrise)).setText(panchang.get("moonrise"));
        ((TextView) view.findViewById(R.id.tv_moonset)).setText(panchang.get("moonset"));
        ((TextView) view.findViewById(R.id.tv_abhijit)).setText(panchang.get("abhijit_muhurat"));
        ((TextView) view.findViewById(R.id.tv_rahukaal)).setText(panchang.get("rahukaal"));
        ((TextView) view.findViewById(R.id.tv_gulikaal)).setText(panchang.get("gulikaal"));
        ((TextView) view.findViewById(R.id.tv_yamghant)).setText(panchang.get("yamghant"));
    }
}
