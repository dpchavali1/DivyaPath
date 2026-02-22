package com.divyapath.app.data.local.entity;
import androidx.room.Entity; import androidx.room.PrimaryKey;
@Entity(tableName = "temples")
public class TempleEntity {
    @PrimaryKey(autoGenerate = true) private int id;
    private String name; private String nameHindi; private String location; private String youtubeUrl;
    private double latitude; private double longitude; private String timings; private String imageUrl;
    private String description; private boolean hasLiveDarshan;
    public TempleEntity(){}
    public int getId(){return id;} public void setId(int id){this.id=id;}
    public String getName(){return name;} public void setName(String n){this.name=n;}
    public String getNameHindi(){return nameHindi;} public void setNameHindi(String n){this.nameHindi=n;}
    public String getLocation(){return location;} public void setLocation(String l){this.location=l;}
    public String getYoutubeUrl(){return youtubeUrl;} public void setYoutubeUrl(String u){this.youtubeUrl=u;}
    public double getLatitude(){return latitude;} public void setLatitude(double l){this.latitude=l;}
    public double getLongitude(){return longitude;} public void setLongitude(double l){this.longitude=l;}
    public String getTimings(){return timings;} public void setTimings(String t){this.timings=t;}
    public String getImageUrl(){return imageUrl;} public void setImageUrl(String i){this.imageUrl=i;}
    public String getDescription(){return description;} public void setDescription(String d){this.description=d;}
    public boolean isHasLiveDarshan(){return hasLiveDarshan;} public void setHasLiveDarshan(boolean h){this.hasLiveDarshan=h;}
}
