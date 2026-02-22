package com.divyapath.app.data.local.dao;
import androidx.lifecycle.LiveData; import androidx.room.*; import com.divyapath.app.data.local.entity.StotraEntity; import java.util.List;
@Dao public interface StotraDao {
    @Insert(onConflict=OnConflictStrategy.REPLACE) void insertAll(List<StotraEntity> stotras);
    @Insert(onConflict=OnConflictStrategy.REPLACE) void insert(StotraEntity stotra);
    @Query("SELECT * FROM stotras") LiveData<List<StotraEntity>> getAllStotras();
    @Query("SELECT * FROM stotras WHERE id = :id") LiveData<StotraEntity> getStotraById(int id);
    @Query("SELECT * FROM stotras WHERE deityId = :deityId") LiveData<List<StotraEntity>> getStotrasByDeity(int deityId);
    @Query("SELECT COUNT(*) FROM stotras") int getCount();
    @Query("SELECT * FROM stotras WHERE title LIKE '%' || :query || '%' OR titleHindi LIKE '%' || :query || '%'") LiveData<List<StotraEntity>> searchStotras(String query);

    // Audio source methods
    @Query("UPDATE stotras SET archiveOrgUrl = :url WHERE title = :title")
    void updateArchiveOrgUrlByTitle(String title, String url);
    @Query("UPDATE stotras SET isCached = :cached, cachedFilePath = :path WHERE id = :id")
    void updateCacheStatus(int id, boolean cached, String path);
    @Query("UPDATE stotras SET audioSource = :source WHERE id = :id")
    void updateAudioSource(int id, String source);
}
