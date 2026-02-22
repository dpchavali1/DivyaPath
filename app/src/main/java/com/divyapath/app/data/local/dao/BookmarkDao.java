package com.divyapath.app.data.local.dao;
import androidx.lifecycle.LiveData; import androidx.room.*; import com.divyapath.app.data.local.entity.BookmarkEntity; import java.util.List;
@Dao public interface BookmarkDao {
    @Insert(onConflict=OnConflictStrategy.REPLACE) void insert(BookmarkEntity bookmark);
    @Delete void delete(BookmarkEntity bookmark);
    @Query("DELETE FROM bookmarks WHERE contentType = :type AND contentId = :contentId") void deleteByContent(String type, int contentId);
    @Query("SELECT * FROM bookmarks ORDER BY timestamp DESC") LiveData<List<BookmarkEntity>> getAllBookmarks();
    @Query("SELECT * FROM bookmarks WHERE contentType = :type") LiveData<List<BookmarkEntity>> getBookmarksByType(String type);
    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE contentType = :type AND contentId = :contentId)") LiveData<Boolean> isBookmarked(String type, int contentId);
    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE contentType = :type AND contentId = :contentId)") boolean isBookmarkedSync(String type, int contentId);
}
