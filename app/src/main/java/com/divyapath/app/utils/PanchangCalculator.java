package com.divyapath.app.utils;

import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Location-aware Panchang calculator.
 * Calculates sunrise/sunset using Jean Meeus simplified algorithm,
 * then derives Rahukaal, Gulikaal, Abhijit Muhurat, Brahma Muhurat.
 */
public class PanchangCalculator {

    private static final String TAG = "PanchangCalculator";

    private static final String[] TITHIS = {"Pratipada", "Dwitiya", "Tritiya", "Chaturthi", "Panchami",
            "Shashthi", "Saptami", "Ashtami", "Navami", "Dashami",
            "Ekadashi", "Dwadashi", "Trayodashi", "Chaturdashi", "Purnima"};

    private static final String[] NAKSHATRAS = {"Ashwini", "Bharani", "Krittika", "Rohini", "Mrigashira",
            "Ardra", "Punarvasu", "Pushya", "Ashlesha", "Magha",
            "Purva Phalguni", "Uttara Phalguni", "Hasta", "Chitra", "Swati",
            "Vishakha", "Anuradha", "Jyeshtha", "Mula", "Purva Ashadha",
            "Uttara Ashadha", "Shravana", "Dhanishtha", "Shatabhisha",
            "Purva Bhadrapada", "Uttara Bhadrapada", "Revati"};

    private static final String[] YOGAS = {"Vishkumbha", "Priti", "Ayushman", "Saubhagya", "Shobhana",
            "Atiganda", "Sukarma", "Dhriti", "Shula", "Ganda",
            "Vriddhi", "Dhruva", "Vyaghata", "Harshana", "Vajra",
            "Siddhi", "Vyatipata", "Variyan", "Parigha", "Shiva",
            "Siddha", "Sadhya", "Shubha", "Shukla", "Brahma",
            "Indra", "Vaidhriti"};

    private static final String[] KARANAS = {"Bava", "Balava", "Kaulava", "Taitila", "Gara",
            "Vanija", "Vishti", "Shakuni", "Chatushpada", "Naga", "Kimstughna"};

    private static final String[] VARAS = {"", "Sunday", "Monday", "Tuesday", "Wednesday",
            "Thursday", "Friday", "Saturday"};

    // Rahukaal part index for Sun=1 through Sat=7
    // Day divided into 8 equal parts from sunrise to sunset
    // Rahu occupies: Sun=8th, Mon=2nd, Tue=7th, Wed=5th, Thu=6th, Fri=4th, Sat=3rd
    private static final int[] RAHU_PARTS = {0, 7, 1, 6, 4, 5, 3, 2}; // index 0 unused

    // Gulikaal: Sun=7th, Mon=6th, Tue=5th, Wed=4th, Thu=3rd, Fri=2nd, Sat=1st
    private static final int[] GULI_PARTS = {0, 6, 5, 4, 3, 2, 1, 0};

    /**
     * Get panchang for today using default location (Delhi).
     */
    public static Map<String, String> getTodaysPanchang() {
        return getPanchangForLocation(28.6139, 77.2090, "Asia/Kolkata");
    }

    /**
     * Get panchang for today at the specified location.
     */
    public static Map<String, String> getPanchangForLocation(double lat, double lon, String timezone) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(timezone));
        return getPanchangForDate(cal, lat, lon, timezone);
    }

    /**
     * Get panchang using the user's saved location preferences.
     */
    public static Map<String, String> getPanchangForUser(Context context) {
        PreferenceManager pm = new PreferenceManager(context);
        double lat = pm.getLocationLat();
        double lon = pm.getLocationLon();
        String tz = pm.getEffectiveTimezone();
        return getPanchangForLocation(lat, lon, tz);
    }

    /**
     * Full panchang calculation for a given date and location.
     */
    public static Map<String, String> getPanchangForDate(Calendar c, double lat, double lon, String timezone) {
        int dayOfYear = c.get(Calendar.DAY_OF_YEAR);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        TimeZone tz = TimeZone.getTimeZone(timezone);
        SimpleDateFormat timeFmt = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        timeFmt.setTimeZone(tz);

        Map<String, String> p = new HashMap<>();

        // Calculate Julian Day Number for astronomical calculations
        double jd = calendarToJD(c);

        // Calculate true Sun and Moon longitudes using simplified astronomical formulas
        double sunLong = calculateSunLongitude(jd);
        double moonLong = calculateMoonLongitude(jd);

        // Tithi: based on Moon-Sun angular difference (each Tithi = 12 degrees)
        double moonSunDiff = moonLong - sunLong;
        if (moonSunDiff < 0) moonSunDiff += 360.0;
        int tithiIndex = (int) (moonSunDiff / 12.0);
        // Tithi 0-14 = Shukla Paksha, 15-29 = Krishna Paksha
        String paksha = tithiIndex < 15 ? "Shukla" : "Krishna";
        int tithiInPaksha = tithiIndex % 15;
        p.put("tithi", paksha + " " + TITHIS[tithiInPaksha]);

        // Nakshatra: based on Moon's longitude (each Nakshatra = 13°20' = 13.333°)
        int nakshatraIndex = (int) (moonLong / (360.0 / 27.0));
        nakshatraIndex = Math.max(0, Math.min(nakshatraIndex, 26));
        p.put("nakshatra", NAKSHATRAS[nakshatraIndex]);

        // Yoga: based on sum of Sun and Moon longitudes (each Yoga = 13°20')
        double yogaSum = sunLong + moonLong;
        if (yogaSum >= 360.0) yogaSum -= 360.0;
        int yogaIndex = (int) (yogaSum / (360.0 / 27.0));
        yogaIndex = Math.max(0, Math.min(yogaIndex, 26));
        p.put("yoga", YOGAS[yogaIndex]);

        // Karana: half of a Tithi (each Karana = 6 degrees of Moon-Sun difference)
        int karanaIndex = (int) (moonSunDiff / 6.0) % 60;
        // Map to the 11 Karana names (7 repeating + 4 fixed)
        if (karanaIndex == 0) {
            p.put("karana", KARANAS[10]); // Kimstughna (fixed, 1st half of Shukla Pratipada)
        } else if (karanaIndex == 57) {
            p.put("karana", KARANAS[7]); // Shakuni
        } else if (karanaIndex == 58) {
            p.put("karana", KARANAS[8]); // Chatushpada
        } else if (karanaIndex == 59) {
            p.put("karana", KARANAS[9]); // Naga
        } else {
            p.put("karana", KARANAS[(karanaIndex - 1) % 7]); // Bava through Vishti cycle
        }

        p.put("vara", VARAS[dayOfWeek]);

        // Calculate sunrise and sunset using Meeus algorithm
        double[] sunTimes = calculateSunriseSunset(lat, lon, c, tz);
        double sunriseHours = sunTimes[0];
        double sunsetHours = sunTimes[1];

        // Format sunrise/sunset
        p.put("sunrise", formatHours(sunriseHours));
        p.put("sunset", formatHours(sunsetHours));

        // Moonrise/Moonset (approximate — shift by ~50 min per day)
        double moonriseBase = 18.0 + (dayOfYear % 30) * 0.833; // rough approximation
        if (moonriseBase >= 24) moonriseBase -= 24;
        p.put("moonrise", formatHours(moonriseBase));
        p.put("moonset", formatHours(moonriseBase > 12 ? moonriseBase - 12 : moonriseBase + 12));

        // Daylight duration in minutes
        double daylightMinutes = (sunsetHours - sunriseHours) * 60;
        double partDuration = daylightMinutes / 8.0;

        // Rahukaal
        if (dayOfWeek >= 1 && dayOfWeek <= 7) {
            int rahuPart = RAHU_PARTS[dayOfWeek];
            double rahuStartMin = sunriseHours * 60 + rahuPart * partDuration;
            double rahuEndMin = rahuStartMin + partDuration;
            p.put("rahukaal", formatMinutes(rahuStartMin) + " - " + formatMinutes(rahuEndMin));

            // Gulikaal
            int guliPart = GULI_PARTS[dayOfWeek];
            double guliStartMin = sunriseHours * 60 + guliPart * partDuration;
            double guliEndMin = guliStartMin + partDuration;
            p.put("gulikaal", formatMinutes(guliStartMin) + " - " + formatMinutes(guliEndMin));
        }

        // Yamghant (simplified — 3 parts after Gulikaal)
        p.put("yamghant", calculateYamghant(dayOfWeek, sunriseHours, partDuration));

        // Abhijit Muhurat: midpoint of day ± 24 minutes
        double midDay = (sunriseHours + sunsetHours) / 2.0;
        double abhijitStartMin = midDay * 60 - 24;
        double abhijitEndMin = midDay * 60 + 24;
        // Not available on Wednesdays
        if (dayOfWeek == Calendar.WEDNESDAY) {
            p.put("abhijit_muhurat", "Not available");
        } else {
            p.put("abhijit_muhurat", formatMinutes(abhijitStartMin) + " - " + formatMinutes(abhijitEndMin));
        }

        // Brahma Muhurat: 96 minutes before sunrise, duration 48 minutes
        double brahmaStartMin = sunriseHours * 60 - 96;
        double brahmaEndMin = brahmaStartMin + 48;
        p.put("brahma_muhurat", formatMinutes(brahmaStartMin) + " - " + formatMinutes(brahmaEndMin));

        // Location info
        p.put("location_lat", String.valueOf(lat));
        p.put("location_lon", String.valueOf(lon));
        p.put("timezone", timezone);

        return p;
    }

    /**
     * Calculate sunrise and sunset using simplified Jean Meeus algorithm.
     * Returns {sunriseHours, sunsetHours} in local time (fractional hours).
     */
    private static double[] calculateSunriseSunset(double lat, double lon, Calendar cal, TimeZone tz) {
        int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);
        int year = cal.get(Calendar.YEAR);

        // Fractional year (gamma) in radians
        double gamma = (2.0 * Math.PI / 365.0) * (dayOfYear - 1);

        // Equation of time (minutes)
        double eqTime = 229.18 * (0.000075 + 0.001868 * Math.cos(gamma)
                - 0.032077 * Math.sin(gamma)
                - 0.014615 * Math.cos(2 * gamma)
                - 0.040849 * Math.sin(2 * gamma));

        // Solar declination (radians)
        double decl = 0.006918 - 0.399912 * Math.cos(gamma) + 0.070257 * Math.sin(gamma)
                - 0.006758 * Math.cos(2 * gamma) + 0.000907 * Math.sin(2 * gamma)
                - 0.002697 * Math.cos(3 * gamma) + 0.00148 * Math.sin(3 * gamma);

        double latRad = Math.toRadians(lat);

        // Hour angle (degrees)
        double zenith = 90.833; // Official zenith for sunrise/sunset
        double cosHA = (Math.cos(Math.toRadians(zenith)) / (Math.cos(latRad) * Math.cos(decl)))
                - Math.tan(latRad) * Math.tan(decl);

        // Clamp for polar regions
        if (cosHA > 1) {
            // Sun never rises
            return new double[]{6.0, 18.0}; // fallback
        } else if (cosHA < -1) {
            // Sun never sets
            return new double[]{4.0, 22.0}; // fallback
        }

        double ha = Math.toDegrees(Math.acos(cosHA));

        // Sunrise and sunset in UTC minutes from midnight
        double sunriseUTC = 720 - 4 * (lon + ha) - eqTime;
        double sunsetUTC = 720 - 4 * (lon - ha) - eqTime;

        // Convert to local time
        int offsetMs = tz.getOffset(cal.getTimeInMillis());
        double offsetMinutes = offsetMs / 60000.0;

        double sunriseLocal = (sunriseUTC + offsetMinutes) / 60.0; // hours
        double sunsetLocal = (sunsetUTC + offsetMinutes) / 60.0;

        // Clamp to reasonable range
        sunriseLocal = Math.max(3.0, Math.min(sunriseLocal, 10.0));
        sunsetLocal = Math.max(15.0, Math.min(sunsetLocal, 22.0));

        return new double[]{sunriseLocal, sunsetLocal};
    }

    private static String calculateYamghant(int dayOfWeek, double sunriseHours, double partDuration) {
        // Yamghant parts: Sun=4, Mon=3, Tue=2, Wed=1, Thu=0(=8th), Fri=7, Sat=6
        int[] yamParts = {0, 3, 2, 1, 0, 7, 6, 5};
        if (dayOfWeek < 1 || dayOfWeek > 7) return "";
        int part = yamParts[dayOfWeek];
        double startMin = sunriseHours * 60 + part * partDuration;
        double endMin = startMin + partDuration;
        return formatMinutes(startMin) + " - " + formatMinutes(endMin);
    }

    /**
     * Format fractional hours to "hh:mm AM/PM"
     */
    private static String formatHours(double hours) {
        if (hours < 0) hours += 24;
        if (hours >= 24) hours -= 24;
        int h = (int) hours;
        int m = (int) ((hours - h) * 60);
        String ampm = h >= 12 ? "PM" : "AM";
        int displayH = h % 12;
        if (displayH == 0) displayH = 12;
        return String.format(Locale.ENGLISH, "%02d:%02d %s", displayH, m, ampm);
    }

    /**
     * Format minutes from midnight to "hh:mm AM/PM"
     */
    private static String formatMinutes(double totalMinutes) {
        if (totalMinutes < 0) totalMinutes += 1440;
        if (totalMinutes >= 1440) totalMinutes -= 1440;
        int h = (int) (totalMinutes / 60);
        int m = (int) (totalMinutes % 60);
        String ampm = h >= 12 ? "PM" : "AM";
        int displayH = h % 12;
        if (displayH == 0) displayH = 12;
        return String.format(Locale.ENGLISH, "%02d:%02d %s", displayH, m, ampm);
    }

    public static String getGreeting() {
        int h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (h >= 4 && h < 12) return "Good Morning";
        else if (h >= 12 && h < 16) return "Good Afternoon";
        else if (h >= 16 && h < 20) return "Good Evening";
        else return "Shubh Ratri";
    }

    public static int getTodaysDayOfWeek() {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
    }

    // ==================== Astronomical Calculation Methods ====================

    /**
     * Convert Calendar to Julian Day Number.
     * Based on Meeus "Astronomical Algorithms" Chapter 7.
     */
    private static double calendarToJD(Calendar cal) {
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1; // Calendar.MONTH is 0-based
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        double dayFraction = day + (hour + minute / 60.0) / 24.0;

        if (month <= 2) {
            year--;
            month += 12;
        }

        int A = year / 100;
        int B = 2 - A + A / 4;

        return (int) (365.25 * (year + 4716)) + (int) (30.6001 * (month + 1))
                + dayFraction + B - 1524.5;
    }

    /**
     * Calculate the Sun's ecliptic longitude (degrees) for a given Julian Day.
     * Simplified algorithm based on Meeus Chapter 25.
     */
    private static double calculateSunLongitude(double jd) {
        // Julian centuries from J2000.0
        double T = (jd - 2451545.0) / 36525.0;

        // Geometric mean longitude of the Sun (degrees)
        double L0 = 280.46646 + T * (36000.76983 + T * 0.0003032);
        L0 = normalizeDegrees(L0);

        // Mean anomaly of the Sun (degrees)
        double M = 357.52911 + T * (35999.05029 - T * 0.0001537);
        M = normalizeDegrees(M);
        double Mrad = Math.toRadians(M);

        // Equation of center
        double C = (1.914602 - T * (0.004817 + T * 0.000014)) * Math.sin(Mrad)
                + (0.019993 - T * 0.000101) * Math.sin(2 * Mrad)
                + 0.000289 * Math.sin(3 * Mrad);

        // Sun's true longitude
        double sunLong = L0 + C;

        // Apparent longitude (correct for nutation and aberration)
        double omega = 125.04 - 1934.136 * T;
        sunLong = sunLong - 0.00569 - 0.00478 * Math.sin(Math.toRadians(omega));

        return normalizeDegrees(sunLong);
    }

    /**
     * Calculate the Moon's ecliptic longitude (degrees) for a given Julian Day.
     * Simplified algorithm based on Meeus Chapter 47.
     */
    private static double calculateMoonLongitude(double jd) {
        // Julian centuries from J2000.0
        double T = (jd - 2451545.0) / 36525.0;
        double T2 = T * T;
        double T3 = T2 * T;

        // Moon's mean longitude (degrees)
        double Lp = 218.3164477 + 481267.88123421 * T - 0.0015786 * T2
                + T3 / 538841.0 - T3 * T / 65194000.0;
        Lp = normalizeDegrees(Lp);

        // Moon's mean elongation (degrees)
        double D = 297.8501921 + 445267.1114034 * T - 0.0018819 * T2
                + T3 / 545868.0 - T3 * T / 113065000.0;
        D = normalizeDegrees(D);

        // Sun's mean anomaly (degrees)
        double M = 357.5291092 + 35999.0502909 * T - 0.0001536 * T2
                + T3 / 24490000.0;
        M = normalizeDegrees(M);

        // Moon's mean anomaly (degrees)
        double Mp = 134.9633964 + 477198.8675055 * T + 0.0087414 * T2
                + T3 / 69699.0 - T3 * T / 14712000.0;
        Mp = normalizeDegrees(Mp);

        // Moon's argument of latitude (degrees)
        double F = 93.2720950 + 483202.0175233 * T - 0.0036539 * T2
                - T3 / 3526000.0 + T3 * T / 863310000.0;
        F = normalizeDegrees(F);

        // Convert to radians for trig
        double Drad = Math.toRadians(D);
        double Mrad = Math.toRadians(M);
        double Mprad = Math.toRadians(Mp);
        double Frad = Math.toRadians(F);

        // Sum of principal terms for longitude (simplified — top 10 terms)
        double sumL = 6288774 * Math.sin(Mprad)
                + 1274027 * Math.sin(2 * Drad - Mprad)
                + 658314 * Math.sin(2 * Drad)
                + 213618 * Math.sin(2 * Mprad)
                - 185116 * Math.sin(Mrad)
                - 114332 * Math.sin(2 * Frad)
                + 58793 * Math.sin(2 * Drad - 2 * Mprad)
                + 57066 * Math.sin(2 * Drad - Mrad - Mprad)
                + 53322 * Math.sin(2 * Drad + Mprad)
                + 45758 * Math.sin(2 * Drad - Mrad);

        // Moon's longitude
        double moonLong = Lp + sumL / 1000000.0;

        // Nutation correction (simplified)
        double omega = 125.04452 - 1934.136261 * T;
        double nutation = -17.2 / 3600.0 * Math.sin(Math.toRadians(omega));
        moonLong += nutation;

        return normalizeDegrees(moonLong);
    }

    /**
     * Normalize an angle to [0, 360) degrees.
     */
    private static double normalizeDegrees(double degrees) {
        degrees = degrees % 360.0;
        if (degrees < 0) degrees += 360.0;
        return degrees;
    }
}
