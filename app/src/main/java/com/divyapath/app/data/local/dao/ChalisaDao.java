package com.divyapath.app.data.local.dao;
import androidx.lifecycle.LiveData; import androidx.room.*; import com.divyapath.app.data.local.entity.ChalisaEntity; import java.util.List;
@Dao public interface ChalisaDao {
    @Insert(onConflict=OnConflictStrategy.REPLACE) void insertAll(List<ChalisaEntity> chalisas);
    @Insert(onConflict=OnConflictStrategy.REPLACE) void insert(ChalisaEntity chalisa);
    @Query("SELECT * FROM chalisas") LiveData<List<ChalisaEntity>> getAllChalisas();
    @Query("SELECT * FROM chalisas WHERE id = :id") LiveData<ChalisaEntity> getChalisaById(int id);
    @Query("SELECT * FROM chalisas WHERE deityId = :deityId") LiveData<List<ChalisaEntity>> getChalisasByDeity(int deityId);
    @Query("SELECT COUNT(*) FROM chalisas") int getCount();
    @Query("SELECT * FROM chalisas WHERE title LIKE '%' || :query || '%' OR titleHindi LIKE '%' || :query || '%'") LiveData<List<ChalisaEntity>> searchChalisas(String query);

    // Audio source methods
    @Query("UPDATE chalisas SET archiveOrgUrl = :url WHERE title = :title")
    void updateArchiveOrgUrlByTitle(String title, String url);

    @Query("UPDATE chalisas SET iskconUrl = :url WHERE title = :title")
    void updateIskconUrlByTitle(String title, String url);

    @Query("UPDATE chalisas SET localAssetName = :assetName WHERE title = :title")
    void updateLocalAssetNameByTitle(String title, String assetName);

    @Query("UPDATE chalisas SET isCached = :cached, cachedFilePath = :path WHERE id = :id")
    void updateCacheStatus(int id, boolean cached, String path);

    @Query("UPDATE chalisas SET audioSource = :source WHERE id = :id")
    void updateAudioSource(int id, String source);
}
