package com.divyapath.app.data.local.dao;
import androidx.lifecycle.LiveData; import androidx.room.*; import com.divyapath.app.data.local.entity.MantraEntity; import java.util.List;
@Dao public interface MantraDao {
    @Insert(onConflict=OnConflictStrategy.REPLACE) void insertAll(List<MantraEntity> mantras);
    @Insert(onConflict=OnConflictStrategy.REPLACE) void insert(MantraEntity mantra);
    @Query("SELECT * FROM mantras") LiveData<List<MantraEntity>> getAllMantras();
    @Query("SELECT * FROM mantras WHERE id = :id") LiveData<MantraEntity> getMantraById(int id);
    @Query("SELECT * FROM mantras WHERE category = :category") LiveData<List<MantraEntity>> getMantrasByCategory(String category);
    @Query("SELECT DISTINCT category FROM mantras") LiveData<List<String>> getAllCategories();
    @Query("SELECT * FROM mantras WHERE deityId = :deityId") LiveData<List<MantraEntity>> getMantrasByDeity(int deityId);
    @Query("SELECT COUNT(*) FROM mantras") int getCount();
    @Query("SELECT * FROM mantras WHERE title LIKE '%' || :query || '%' OR sanskrit LIKE '%' || :query || '%'") LiveData<List<MantraEntity>> searchMantras(String query);

    // Audio source methods
    @Query("UPDATE mantras SET archiveOrgUrl = :url WHERE title = :title")
    void updateArchiveOrgUrlByTitle(String title, String url);
    @Query("UPDATE mantras SET isCached = :cached, cachedFilePath = :path WHERE id = :id")
    void updateCacheStatus(int id, boolean cached, String path);
    @Query("UPDATE mantras SET audioSource = :source WHERE id = :id")
    void updateAudioSource(int id, String source);
}
