package com.divyapath.app.data.local.entity;
import androidx.room.Entity; import androidx.room.ForeignKey; import androidx.room.Index; import androidx.room.PrimaryKey;
@Entity(tableName="bhajans", foreignKeys=@ForeignKey(entity=DeityEntity.class,parentColumns="id",childColumns="deityId",onDelete=ForeignKey.CASCADE), indices=@Index("deityId"))
public class BhajanEntity {
    @PrimaryKey(autoGenerate=true) private int id;
    private int deityId; private String title; private String titleHindi;
    private String lyricsHindi; private String lyricsEnglish; private String audioUrl;
    private int duration; private String category; private String language;
    // Audio source fields (added in DB v9)
    private String archiveOrgUrl;
    private String iskconUrl;
    private String localAssetName;
    private String audioSource;
    private boolean isCached;
    private String cachedFilePath;

    public BhajanEntity(){}
    public int getId(){return id;} public void setId(int id){this.id=id;}
    public int getDeityId(){return deityId;} public void setDeityId(int d){this.deityId=d;}
    public String getTitle(){return title;} public void setTitle(String t){this.title=t;}
    public String getTitleHindi(){return titleHindi;} public void setTitleHindi(String t){this.titleHindi=t;}
    public String getLyricsHindi(){return lyricsHindi;} public void setLyricsHindi(String l){this.lyricsHindi=l;}
    public String getLyricsEnglish(){return lyricsEnglish;} public void setLyricsEnglish(String l){this.lyricsEnglish=l;}
    public String getAudioUrl(){return audioUrl;} public void setAudioUrl(String a){this.audioUrl=a;}
    public int getDuration(){return duration;} public void setDuration(int d){this.duration=d;}
    public String getCategory(){return category;} public void setCategory(String c){this.category=c;}
    public String getLanguage(){return language;} public void setLanguage(String l){this.language=l;}
    public String getArchiveOrgUrl(){return archiveOrgUrl;} public void setArchiveOrgUrl(String u){this.archiveOrgUrl=u;}
    public String getIskconUrl(){return iskconUrl;} public void setIskconUrl(String u){this.iskconUrl=u;}
    public String getLocalAssetName(){return localAssetName;} public void setLocalAssetName(String n){this.localAssetName=n;}
    public String getAudioSource(){return audioSource;} public void setAudioSource(String s){this.audioSource=s;}
    public boolean isCached(){return isCached;} public void setCached(boolean c){this.isCached=c;}
    public String getCachedFilePath(){return cachedFilePath;} public void setCachedFilePath(String p){this.cachedFilePath=p;}
}
