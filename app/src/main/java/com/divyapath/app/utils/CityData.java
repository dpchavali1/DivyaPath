package com.divyapath.app.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Static database of 60 cities across 7 regions for Panchang location selection.
 * Provides search, region filtering, and Haversine distance-based closest city lookup.
 */
public class CityData {

    public static class City {
        private final String name;
        private final String countryCode;
        private final String countryName;
        private final double latitude;
        private final double longitude;
        private final String timezone;
        private final String region;

        public City(String name, String countryCode, String countryName,
                    double latitude, double longitude, String timezone, String region) {
            this.name = name;
            this.countryCode = countryCode;
            this.countryName = countryName;
            this.latitude = latitude;
            this.longitude = longitude;
            this.timezone = timezone;
            this.region = region;
        }

        public String getName() { return name; }
        public String getCountryCode() { return countryCode; }
        public String getCountryName() { return countryName; }
        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }
        public String getTimezone() { return timezone; }
        public String getRegion() { return region; }
    }

    private static final List<City> ALL_CITIES;

    static {
        List<City> cities = new ArrayList<>();

        // ---- India (30 cities) ----
        cities.add(new City("Mumbai", "IN", "India", 19.0760, 72.8777, "Asia/Kolkata", "India"));
        cities.add(new City("Delhi", "IN", "India", 28.7041, 77.1025, "Asia/Kolkata", "India"));
        cities.add(new City("Bangalore", "IN", "India", 12.9716, 77.5946, "Asia/Kolkata", "India"));
        cities.add(new City("Chennai", "IN", "India", 13.0827, 80.2707, "Asia/Kolkata", "India"));
        cities.add(new City("Hyderabad", "IN", "India", 17.3850, 78.4867, "Asia/Kolkata", "India"));
        cities.add(new City("Kolkata", "IN", "India", 22.5726, 88.3639, "Asia/Kolkata", "India"));
        cities.add(new City("Pune", "IN", "India", 18.5204, 73.8567, "Asia/Kolkata", "India"));
        cities.add(new City("Ahmedabad", "IN", "India", 23.0225, 72.5714, "Asia/Kolkata", "India"));
        cities.add(new City("Varanasi", "IN", "India", 25.3176, 82.9739, "Asia/Kolkata", "India"));
        cities.add(new City("Tirupati", "IN", "India", 13.6288, 79.4192, "Asia/Kolkata", "India"));
        cities.add(new City("Jaipur", "IN", "India", 26.9124, 75.7873, "Asia/Kolkata", "India"));
        cities.add(new City("Lucknow", "IN", "India", 26.8467, 80.9462, "Asia/Kolkata", "India"));
        cities.add(new City("Bhopal", "IN", "India", 23.2599, 77.4126, "Asia/Kolkata", "India"));
        cities.add(new City("Indore", "IN", "India", 22.7196, 75.8577, "Asia/Kolkata", "India"));
        cities.add(new City("Nagpur", "IN", "India", 21.1458, 79.0882, "Asia/Kolkata", "India"));
        cities.add(new City("Surat", "IN", "India", 21.1702, 72.8311, "Asia/Kolkata", "India"));
        cities.add(new City("Coimbatore", "IN", "India", 11.0168, 76.9558, "Asia/Kolkata", "India"));
        cities.add(new City("Kochi", "IN", "India", 9.9312, 76.2673, "Asia/Kolkata", "India"));
        cities.add(new City("Mysore", "IN", "India", 12.2958, 76.6394, "Asia/Kolkata", "India"));
        cities.add(new City("Amritsar", "IN", "India", 31.6340, 74.8723, "Asia/Kolkata", "India"));
        cities.add(new City("Prayagraj", "IN", "India", 25.4358, 81.8463, "Asia/Kolkata", "India"));
        cities.add(new City("Mathura", "IN", "India", 27.4924, 77.6737, "Asia/Kolkata", "India"));
        cities.add(new City("Dwarka", "IN", "India", 22.2394, 68.9678, "Asia/Kolkata", "India"));
        cities.add(new City("Puri", "IN", "India", 19.8049, 85.8178, "Asia/Kolkata", "India"));
        cities.add(new City("Somnath", "IN", "India", 20.8880, 70.4011, "Asia/Kolkata", "India"));
        cities.add(new City("Shirdi", "IN", "India", 19.7647, 74.4776, "Asia/Kolkata", "India"));
        cities.add(new City("Nashik", "IN", "India", 20.0059, 73.7897, "Asia/Kolkata", "India"));
        cities.add(new City("Haridwar", "IN", "India", 29.9457, 78.1642, "Asia/Kolkata", "India"));
        cities.add(new City("Rishikesh", "IN", "India", 30.0869, 78.2676, "Asia/Kolkata", "India"));
        cities.add(new City("Ujjain", "IN", "India", 23.1828, 75.7772, "Asia/Kolkata", "India"));

        // ---- USA & Canada (12 cities) ----
        cities.add(new City("New Jersey", "US", "United States", 40.0583, -74.4057, "America/New_York", "USA & Canada"));
        cities.add(new City("New York", "US", "United States", 40.7128, -74.0060, "America/New_York", "USA & Canada"));
        cities.add(new City("Chicago", "US", "United States", 41.8781, -87.6298, "America/Chicago", "USA & Canada"));
        cities.add(new City("Houston", "US", "United States", 29.7604, -95.3698, "America/Chicago", "USA & Canada"));
        cities.add(new City("Dallas", "US", "United States", 32.7767, -96.7970, "America/Chicago", "USA & Canada"));
        cities.add(new City("San Jose", "US", "United States", 37.3382, -121.8863, "America/Los_Angeles", "USA & Canada"));
        cities.add(new City("Los Angeles", "US", "United States", 34.0522, -118.2437, "America/Los_Angeles", "USA & Canada"));
        cities.add(new City("Atlanta", "US", "United States", 33.7490, -84.3880, "America/New_York", "USA & Canada"));
        cities.add(new City("Washington DC", "US", "United States", 38.9072, -77.0369, "America/New_York", "USA & Canada"));
        cities.add(new City("Toronto", "CA", "Canada", 43.6532, -79.3832, "America/Toronto", "USA & Canada"));
        cities.add(new City("Vancouver", "CA", "Canada", 49.2827, -123.1207, "America/Vancouver", "USA & Canada"));
        cities.add(new City("Edison NJ", "US", "United States", 40.5187, -74.4121, "America/New_York", "USA & Canada"));

        // ---- Middle East (5 cities) ----
        cities.add(new City("Dubai", "AE", "United Arab Emirates", 25.2048, 55.2708, "Asia/Dubai", "Middle East"));
        cities.add(new City("Abu Dhabi", "AE", "United Arab Emirates", 24.4539, 54.3773, "Asia/Dubai", "Middle East"));
        cities.add(new City("Bahrain", "BH", "Bahrain", 26.0667, 50.5577, "Asia/Bahrain", "Middle East"));
        cities.add(new City("Doha", "QA", "Qatar", 25.2854, 51.5310, "Asia/Qatar", "Middle East"));
        cities.add(new City("Muscat", "OM", "Oman", 23.5859, 58.4059, "Asia/Muscat", "Middle East"));

        // ---- UK & Europe (5 cities) ----
        cities.add(new City("London", "GB", "United Kingdom", 51.5074, -0.1278, "Europe/London", "UK & Europe"));
        cities.add(new City("Birmingham UK", "GB", "United Kingdom", 52.4862, -1.8904, "Europe/London", "UK & Europe"));
        cities.add(new City("Amsterdam", "NL", "Netherlands", 52.3676, 4.9041, "Europe/Amsterdam", "UK & Europe"));
        cities.add(new City("Frankfurt", "DE", "Germany", 50.1109, 8.6821, "Europe/Berlin", "UK & Europe"));
        cities.add(new City("Paris", "FR", "France", 48.8566, 2.3522, "Europe/Paris", "UK & Europe"));

        // ---- Australia & NZ (3 cities) ----
        cities.add(new City("Sydney", "AU", "Australia", -33.8688, 151.2093, "Australia/Sydney", "Australia & NZ"));
        cities.add(new City("Melbourne", "AU", "Australia", -37.8136, 144.9631, "Australia/Sydney", "Australia & NZ"));
        cities.add(new City("Auckland", "NZ", "New Zealand", -36.8485, 174.7633, "Pacific/Auckland", "Australia & NZ"));

        // ---- Africa (3 cities) ----
        cities.add(new City("Mauritius", "MU", "Mauritius", -20.3484, 57.5522, "Indian/Mauritius", "Africa"));
        cities.add(new City("Johannesburg", "ZA", "South Africa", -26.2041, 28.0473, "Africa/Johannesburg", "Africa"));
        cities.add(new City("Nairobi", "KE", "Kenya", -1.2921, 36.8219, "Africa/Nairobi", "Africa"));

        // ---- Southeast Asia (2 cities) ----
        cities.add(new City("Singapore", "SG", "Singapore", 1.3521, 103.8198, "Asia/Singapore", "Southeast Asia"));
        cities.add(new City("Kuala Lumpur", "MY", "Malaysia", 3.1390, 101.6869, "Asia/Kuala_Lumpur", "Southeast Asia"));

        ALL_CITIES = Collections.unmodifiableList(cities);
    }

    /** Returns all cities belonging to the specified region. */
    public static List<City> getCitiesByRegion(String region) {
        List<City> result = new ArrayList<>();
        for (City city : ALL_CITIES) {
            if (city.getRegion().equalsIgnoreCase(region)) {
                result.add(city);
            }
        }
        return result;
    }

    /** Returns the full list of all 60 cities. */
    public static List<City> getAllCities() {
        return ALL_CITIES;
    }

    /** Returns the list of region names. */
    public static List<String> getRegions() {
        return Arrays.asList(
                "India", "USA & Canada", "Middle East",
                "UK & Europe", "Australia & NZ", "Africa", "Southeast Asia"
        );
    }

    /** Searches cities by name or country name (case-insensitive partial match). */
    public static List<City> searchCities(String query) {
        List<City> result = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            return result;
        }
        String lowerQuery = query.toLowerCase().trim();
        for (City city : ALL_CITIES) {
            if (city.getName().toLowerCase().contains(lowerQuery)
                    || city.getCountryName().toLowerCase().contains(lowerQuery)) {
                result.add(city);
            }
        }
        return result;
    }

    /** Finds the closest city to given coordinates using Haversine formula. */
    public static City findClosest(double lat, double lon) {
        City closest = null;
        double minDistance = Double.MAX_VALUE;
        for (City city : ALL_CITIES) {
            double distance = haversineDistance(lat, lon, city.getLatitude(), city.getLongitude());
            if (distance < minDistance) {
                minDistance = distance;
                closest = city;
            }
        }
        return closest;
    }

    private static double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
