package com.divyapath.app.data.local.entity;
import androidx.room.Entity; import androidx.room.ForeignKey; import androidx.room.Index; import androidx.room.PrimaryKey;
@Entity(tableName="mantras", foreignKeys=@ForeignKey(entity=DeityEntity.class,parentColumns="id",childColumns="deityId",onDelete=ForeignKey.CASCADE), indices=@Index("deityId"))
public class MantraEntity {
    @PrimaryKey(autoGenerate=true) private int id;
    private int deityId; private String title; private String sanskrit; private String hindiMeaning;
    private String englishTransliteration; private String benefits; private String audioUrl; private String category;
    private int recommendedCount = 108;
    // Audio source fields (added in DB v9)
    private String archiveOrgUrl;
    private String iskconUrl;
    private String localAssetName;
    private String audioSource;
    private boolean isCached;
    private String cachedFilePath;

    public MantraEntity(){}
    public int getId(){return id;} public void setId(int id){this.id=id;}
    public int getDeityId(){return deityId;} public void setDeityId(int d){this.deityId=d;}
    public String getTitle(){return title;} public void setTitle(String t){this.title=t;}
    public String getSanskrit(){return sanskrit;} public void setSanskrit(String s){this.sanskrit=s;}
    public String getHindiMeaning(){return hindiMeaning;} public void setHindiMeaning(String h){this.hindiMeaning=h;}
    public String getEnglishTransliteration(){return englishTransliteration;} public void setEnglishTransliteration(String e){this.englishTransliteration=e;}
    public String getBenefits(){return benefits;} public void setBenefits(String b){this.benefits=b;}
    public String getAudioUrl(){return audioUrl;} public void setAudioUrl(String a){this.audioUrl=a;}
    public String getCategory(){return category;} public void setCategory(String c){this.category=c;}
    public int getRecommendedCount(){return recommendedCount;} public void setRecommendedCount(int r){this.recommendedCount=r;}
    public String getArchiveOrgUrl(){return archiveOrgUrl;} public void setArchiveOrgUrl(String u){this.archiveOrgUrl=u;}
    public String getIskconUrl(){return iskconUrl;} public void setIskconUrl(String u){this.iskconUrl=u;}
    public String getLocalAssetName(){return localAssetName;} public void setLocalAssetName(String n){this.localAssetName=n;}
    public String getAudioSource(){return audioSource;} public void setAudioSource(String s){this.audioSource=s;}
    public boolean isCached(){return isCached;} public void setCached(boolean c){this.isCached=c;}
    public String getCachedFilePath(){return cachedFilePath;} public void setCachedFilePath(String p){this.cachedFilePath=p;}
}
