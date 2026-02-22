package com.divyapath.app.data.remote.dto;

import com.divyapath.app.utils.CityData;
import com.google.gson.annotations.SerializedName;

/**
 * Maps to Open-Meteo Geocoding API response item.
 * Example: {"name":"Detroit","latitude":42.33,"longitude":-83.05,"country":"United States",
 *           "country_code":"US","timezone":"America/Detroit","admin1":"Michigan"}
 */
public class GeocodingResult {

    @SerializedName("name")
    private String name;

    @SerializedName("country")
    private String country;

    @SerializedName("country_code")
    private String countryCode;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    @SerializedName("timezone")
    private String timezone;

    @SerializedName("admin1")
    private String admin1; // State/Province

    @SerializedName("population")
    private long population;

    public String getName() { return name; }
    public String getCountry() { return country; }
    public String getCountryCode() { return countryCode; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getTimezone() { return timezone; }
    public String getAdmin1() { return admin1; }
    public long getPopulation() { return population; }

    /** Returns display name like "Detroit, Michigan" or "Mumbai, Maharashtra" */
    public String getDisplayName() {
        if (admin1 != null && !admin1.isEmpty()) {
            return name + ", " + admin1;
        }
        return name;
    }

    /** Convert to CityData.City for compatibility with existing code */
    public CityData.City toCityDataCity() {
        String region = admin1 != null ? admin1 : (country != null ? country : "");
        return new CityData.City(
                name,
                countryCode != null ? countryCode.toUpperCase() : "",
                country != null ? country : "",
                latitude, longitude,
                timezone != null ? timezone : "UTC",
                region
        );
    }
}
