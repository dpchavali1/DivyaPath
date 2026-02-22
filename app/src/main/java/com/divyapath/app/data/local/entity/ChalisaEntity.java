package com.divyapath.app.data.local.entity;
import androidx.room.Entity; import androidx.room.ForeignKey; import androidx.room.Index; import androidx.room.PrimaryKey;
@Entity(tableName="chalisas", foreignKeys=@ForeignKey(entity=DeityEntity.class,parentColumns="id",childColumns="deityId",onDelete=ForeignKey.CASCADE), indices=@Index("deityId"))
public class ChalisaEntity {
    @PrimaryKey(autoGenerate=true) private int id;
    private int deityId; private String title; private String titleHindi;
    private String content; private String contentEnglish; private String audioUrl; private int totalVerses;
    // Audio source fields (added in DB v8)
    private String archiveOrgUrl;
    private String iskconUrl;
    private String localAssetName;
    private String audioSource;    // "local", "archive_org", "iskcon", "tts"
    private boolean isCached;
    private String cachedFilePath;

    public ChalisaEntity(){}
    public int getId(){return id;} public void setId(int id){this.id=id;}
    public int getDeityId(){return deityId;} public void setDeityId(int d){this.deityId=d;}
    public String getTitle(){return title;} public void setTitle(String t){this.title=t;}
    public String getTitleHindi(){return titleHindi;} public void setTitleHindi(String t){this.titleHindi=t;}
    public String getContent(){return content;} public void setContent(String c){this.content=c;}
    public String getContentEnglish(){return contentEnglish;} public void setContentEnglish(String c){this.contentEnglish=c;}
    public String getAudioUrl(){return audioUrl;} public void setAudioUrl(String a){this.audioUrl=a;}
    public int getTotalVerses(){return totalVerses;} public void setTotalVerses(int t){this.totalVerses=t;}
    // New getters/setters
    public String getArchiveOrgUrl(){return archiveOrgUrl;} public void setArchiveOrgUrl(String u){this.archiveOrgUrl=u;}
    public String getIskconUrl(){return iskconUrl;} public void setIskconUrl(String u){this.iskconUrl=u;}
    public String getLocalAssetName(){return localAssetName;} public void setLocalAssetName(String n){this.localAssetName=n;}
    public String getAudioSource(){return audioSource;} public void setAudioSource(String s){this.audioSource=s;}
    public boolean isCached(){return isCached;} public void setCached(boolean c){this.isCached=c;}
    public String getCachedFilePath(){return cachedFilePath;} public void setCachedFilePath(String p){this.cachedFilePath=p;}
}
