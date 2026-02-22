package com.divyapath.app.data.local.dao;
import androidx.lifecycle.LiveData; import androidx.room.*; import com.divyapath.app.data.local.entity.BhajanEntity; import java.util.List;
@Dao public interface BhajanDao {
    @Insert(onConflict=OnConflictStrategy.REPLACE) void insertAll(List<BhajanEntity> bhajans);
    @Insert(onConflict=OnConflictStrategy.REPLACE) void insert(BhajanEntity bhajan);
    @Query("SELECT * FROM bhajans") LiveData<List<BhajanEntity>> getAllBhajans();
    @Query("SELECT * FROM bhajans WHERE id = :id") LiveData<BhajanEntity> getBhajanById(int id);
    @Query("SELECT * FROM bhajans WHERE category = :category") LiveData<List<BhajanEntity>> getBhajansByCategory(String category);
    @Query("SELECT * FROM bhajans WHERE deityId = :deityId") LiveData<List<BhajanEntity>> getBhajansByDeity(int deityId);
    @Query("SELECT DISTINCT category FROM bhajans") LiveData<List<String>> getAllCategories();
    @Query("SELECT COUNT(*) FROM bhajans") int getCount();
    @Query("SELECT * FROM bhajans WHERE title LIKE '%' || :query || '%' OR titleHindi LIKE '%' || :query || '%'") LiveData<List<BhajanEntity>> searchBhajans(String query);

    // Audio source methods
    @Query("UPDATE bhajans SET archiveOrgUrl = :url WHERE title = :title")
    void updateArchiveOrgUrlByTitle(String title, String url);
    @Query("UPDATE bhajans SET isCached = :cached, cachedFilePath = :path WHERE id = :id")
    void updateCacheStatus(int id, boolean cached, String path);
    @Query("UPDATE bhajans SET audioSource = :source WHERE id = :id")
    void updateAudioSource(int id, String source);
}
