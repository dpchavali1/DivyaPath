package com.divyapath.app.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "shraddha")
public class ShraddhaEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String relationship;
    private int tithiIndex; // 0-29 (30 tithis in a lunar month)
    private int lunarMonth; // 1-12
    private boolean isAnnual;
    private int mantraId;
    private String notes;
    private long createdAt;

    public ShraddhaEntity() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRelationship() { return relationship; }
    public void setRelationship(String relationship) { this.relationship = relationship; }

    public int getTithiIndex() { return tithiIndex; }
    public void setTithiIndex(int tithiIndex) { this.tithiIndex = tithiIndex; }

    public int getLunarMonth() { return lunarMonth; }
    public void setLunarMonth(int lunarMonth) { this.lunarMonth = lunarMonth; }

    public boolean isAnnual() { return isAnnual; }
    public void setAnnual(boolean annual) { isAnnual = annual; }

    public int getMantraId() { return mantraId; }
    public void setMantraId(int mantraId) { this.mantraId = mantraId; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    // Helper methods
    public static String getTithiName(int index) {
        String[] tithis = {
                "Pratipada", "Dwitiya", "Tritiya", "Chaturthi", "Panchami",
                "Shashthi", "Saptami", "Ashtami", "Navami", "Dashami",
                "Ekadashi", "Dwadashi", "Trayodashi", "Chaturdashi", "Purnima",
                "Pratipada", "Dwitiya", "Tritiya", "Chaturthi", "Panchami",
                "Shashthi", "Saptami", "Ashtami", "Navami", "Dashami",
                "Ekadashi", "Dwadashi", "Trayodashi", "Chaturdashi", "Amavasya"
        };
        if (index >= 0 && index < tithis.length) return tithis[index];
        return "Unknown";
    }

    public static String getPakshaName(int tithiIndex) {
        return tithiIndex < 15 ? "Shukla Paksha" : "Krishna Paksha";
    }

    public static String getLunarMonthName(int month) {
        String[] months = {
                "", "Chaitra", "Vaishakha", "Jyeshtha", "Ashadha",
                "Shravana", "Bhadrapada", "Ashwin", "Kartik",
                "Margashirsha", "Pausha", "Magha", "Phalguna"
        };
        if (month >= 1 && month < months.length) return months[month];
        return "Unknown";
    }

    public static String[] getRelationships() {
        return new String[]{
                "Father", "Mother", "Grandfather", "Grandmother",
                "Uncle", "Aunt", "Brother", "Sister",
                "Spouse", "Son", "Daughter", "Guru", "Other"
        };
    }
}
