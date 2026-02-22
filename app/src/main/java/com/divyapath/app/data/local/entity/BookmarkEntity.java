package com.divyapath.app.data.local.entity;
import androidx.room.Entity; import androidx.room.Ignore; import androidx.room.PrimaryKey;
@Entity(tableName="bookmarks")
public class BookmarkEntity {
    @PrimaryKey(autoGenerate=true) private int id;
    private String contentType; private int contentId; private long timestamp;
    public BookmarkEntity(){}
    @Ignore
    public BookmarkEntity(String contentType, int contentId){this.contentType=contentType;this.contentId=contentId;this.timestamp=System.currentTimeMillis();}
    public int getId(){return id;} public void setId(int id){this.id=id;}
    public String getContentType(){return contentType;} public void setContentType(String c){this.contentType=c;}
    public int getContentId(){return contentId;} public void setContentId(int c){this.contentId=c;}
    public long getTimestamp(){return timestamp;} public void setTimestamp(long t){this.timestamp=t;}
}
