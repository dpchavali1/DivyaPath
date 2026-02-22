package com.divyapath.app.utils;

public class LocationPreference {
    private String cityName;
    private String countryCode;
    private String countryName;
    private double latitude;
    private double longitude;
    private String timezone;
    private boolean useGPS;
    private boolean useDeviceTimezone;

    public LocationPreference() {
        // Default to Delhi
        this.cityName = "New Delhi";
        this.countryCode = "IN";
        this.countryName = "India";
        this.latitude = 28.6139;
        this.longitude = 77.2090;
        this.timezone = "Asia/Kolkata";
        this.useGPS = false;
        this.useDeviceTimezone = false;
    }

    public LocationPreference(String cityName, String countryCode, String countryName,
                              double latitude, double longitude, String timezone,
                              boolean useGPS, boolean useDeviceTimezone) {
        this.cityName = cityName;
        this.countryCode = countryCode;
        this.countryName = countryName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timezone = timezone;
        this.useGPS = useGPS;
        this.useDeviceTimezone = useDeviceTimezone;
    }

    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }
    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
    public String getCountryName() { return countryName; }
    public void setCountryName(String countryName) { this.countryName = countryName; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }
    public boolean isUseGPS() { return useGPS; }
    public void setUseGPS(boolean useGPS) { this.useGPS = useGPS; }
    public boolean isUseDeviceTimezone() { return useDeviceTimezone; }
    public void setUseDeviceTimezone(boolean useDeviceTimezone) { this.useDeviceTimezone = useDeviceTimezone; }
}
