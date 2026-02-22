package com.divyapath.app.data.local.entity;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
@Entity(tableName = "deities")
public class DeityEntity {
    @PrimaryKey(autoGenerate = true) private int id;
    private String name; private String hindiName; private String imageUrl;
    private String description; private int dayOfWeek; private String colorTheme;
    public DeityEntity() {}
    @Ignore
    public DeityEntity(String name, String hindiName, String imageUrl, String description, int dayOfWeek, String colorTheme) {
        this.name=name; this.hindiName=hindiName; this.imageUrl=imageUrl; this.description=description; this.dayOfWeek=dayOfWeek; this.colorTheme=colorTheme;
    }
    public int getId(){return id;} public void setId(int id){this.id=id;}
    public String getName(){return name;} public void setName(String n){this.name=n;}
    public String getHindiName(){return hindiName;} public void setHindiName(String n){this.hindiName=n;}
    public String getImageUrl(){return imageUrl;} public void setImageUrl(String u){this.imageUrl=u;}
    public String getDescription(){return description;} public void setDescription(String d){this.description=d;}
    public int getDayOfWeek(){return dayOfWeek;} public void setDayOfWeek(int d){this.dayOfWeek=d;}
    public String getColorTheme(){return colorTheme;} public void setColorTheme(String c){this.colorTheme=c;}
}
