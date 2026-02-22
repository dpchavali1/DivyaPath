package com.divyapath.app.ui.panchang;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.divyapath.app.R;
import com.divyapath.app.utils.PanchangCalculator;
import com.divyapath.app.utils.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class PanchangCalendarFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_panchang_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        PreferenceManager prefs = new PreferenceManager(requireContext());
        CalendarView calendarView = view.findViewById(R.id.calendar_view);
        calendarView.setOnDateChangeListener((v, year, month, dayOfMonth) -> {
            String effectiveTimezone = prefs.getEffectiveTimezone();
            TimeZone timezone = TimeZone.getTimeZone(effectiveTimezone);

            Calendar selectedDate = Calendar.getInstance(timezone);
            selectedDate.set(Calendar.YEAR, year);
            selectedDate.set(Calendar.MONTH, month);
            selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            selectedDate.set(Calendar.HOUR_OF_DAY, 12);
            selectedDate.set(Calendar.MINUTE, 0);
            selectedDate.set(Calendar.SECOND, 0);
            selectedDate.set(Calendar.MILLISECOND, 0);

            Map<String, String> panchang = PanchangCalculator.getPanchangForDate(
                    selectedDate,
                    prefs.getLocationLat(),
                    prefs.getLocationLon(),
                    effectiveTimezone
            );

            view.findViewById(R.id.card_selected_day).setVisibility(View.VISIBLE);
            SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy, EEEE", Locale.getDefault());
            format.setTimeZone(timezone);
            ((TextView) view.findViewById(R.id.tv_selected_date)).setText(format.format(selectedDate.getTime()));
            ((TextView) view.findViewById(R.id.tv_cal_tithi)).setText(panchang.get("tithi"));
            ((TextView) view.findViewById(R.id.tv_cal_nakshatra)).setText(panchang.get("nakshatra"));
            ((TextView) view.findViewById(R.id.tv_cal_rahukaal)).setText(panchang.get("rahukaal"));
        });
    }
}
