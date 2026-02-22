package com.divyapath.app.ui.settings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.divyapath.app.R;
import com.divyapath.app.utils.CityData;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class CityListAdapter extends RecyclerView.Adapter<CityListAdapter.CityViewHolder> {

    private List<CityData.City> cities = new ArrayList<>();
    private String selectedCityName = "";
    private OnCitySelectedListener listener;

    public interface OnCitySelectedListener {
        void onCitySelected(CityData.City city);
    }

    public CityListAdapter(OnCitySelectedListener listener) {
        this.listener = listener;
    }

    public void setCities(List<CityData.City> cities) {
        this.cities = cities;
        notifyDataSetChanged();
    }

    public void setSelectedCity(String cityName) {
        this.selectedCityName = cityName != null ? cityName : "";
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_city, parent, false);
        return new CityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CityViewHolder holder, int position) {
        CityData.City city = cities.get(position);
        holder.tvCityName.setText(city.getName());
        // Show "State, Country" for richer context (region carries state for API results)
        String region = city.getRegion();
        String countryName = city.getCountryName();
        if (region != null && !region.isEmpty() && !region.equals(countryName)
                && !region.equals("India") && !region.equals("USA & Canada")
                && !region.equals("Middle East") && !region.equals("UK & Europe")
                && !region.equals("Australia & NZ") && !region.equals("Africa")
                && !region.equals("Southeast Asia")) {
            holder.tvCityCountry.setText(region + ", " + countryName);
        } else {
            holder.tvCityCountry.setText(countryName);
        }

        // Show short timezone abbreviation
        try {
            TimeZone tz = TimeZone.getTimeZone(city.getTimezone());
            holder.tvTimezone.setText(tz.getDisplayName(false, TimeZone.SHORT));
        } catch (Exception e) {
            holder.tvTimezone.setText(city.getTimezone());
        }

        boolean isSelected = city.getName().equals(selectedCityName);
        holder.ivSelected.setVisibility(isSelected ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            selectedCityName = city.getName();
            notifyDataSetChanged();
            if (listener != null) {
                listener.onCitySelected(city);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cities.size();
    }

    static class CityViewHolder extends RecyclerView.ViewHolder {
        TextView tvCityName, tvCityCountry, tvTimezone;
        ImageView ivSelected;

        CityViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCityName = itemView.findViewById(R.id.tv_city_name);
            tvCityCountry = itemView.findViewById(R.id.tv_city_country);
            tvTimezone = itemView.findViewById(R.id.tv_city_timezone);
            ivSelected = itemView.findViewById(R.id.iv_city_selected);
        }
    }
}
