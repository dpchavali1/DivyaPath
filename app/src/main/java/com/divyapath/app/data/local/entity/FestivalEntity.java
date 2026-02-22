package com.divyapath.app.data.local.entity;
import androidx.room.Entity; import androidx.room.PrimaryKey;
@Entity(tableName="festivals")
public class FestivalEntity {
    @PrimaryKey(autoGenerate=true) private int id;
    private String name; private String nameHindi; private String date;
    private String description; private int specialAartiId; private String imageUrl;
    public FestivalEntity(){}
    public int getId(){return id;} public void setId(int id){this.id=id;}
    public String getName(){return name;} public void setName(String n){this.name=n;}
    public String getNameHindi(){return nameHindi;} public void setNameHindi(String n){this.nameHindi=n;}
    public String getDate(){return date;} public void setDate(String d){this.date=d;}
    public String getDescription(){return description;} public void setDescription(String d){this.description=d;}
    public int getSpecialAartiId(){return specialAartiId;} public void setSpecialAartiId(int s){this.specialAartiId=s;}
    public String getImageUrl(){return imageUrl;} public void setImageUrl(String u){this.imageUrl=u;}
}
