package com.divyapath.app.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.divyapath.app.data.local.entity.AartiEntity;

import java.util.List;

@Dao
public interface AartiDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<AartiEntity> aartis);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AartiEntity aarti);

    @Query("SELECT * FROM aartis")
    LiveData<List<AartiEntity>> getAllAartis();

    @Query("SELECT * FROM aartis WHERE id = :id")
    LiveData<AartiEntity> getAartiById(int id);

    @Query("SELECT * FROM aartis WHERE deityId = :deityId")
    LiveData<List<AartiEntity>> getAartisByDeity(int deityId);

    @Query("SELECT * FROM aartis LIMIT :limit")
    LiveData<List<AartiEntity>> getTodaysAartis(int limit);

    @Query("UPDATE aartis SET audioUrl = :audioUrl WHERE title = :title AND (audioUrl IS NULL OR audioUrl = '')")
    void updateAudioUrlByTitle(String title, String audioUrl);

    @Query("SELECT COUNT(*) FROM aartis")
    int getCount();

    @Query("SELECT * FROM aartis WHERE title LIKE '%' || :query || '%' OR titleHindi LIKE '%' || :query || '%'")
    LiveData<List<AartiEntity>> searchAartis(String query);

    // --- Audio source methods ---

    @Query("UPDATE aartis SET archiveOrgUrl = :url WHERE title = :title")
    void updateArchiveOrgUrlByTitle(String title, String url);

    @Query("UPDATE aartis SET iskconUrl = :url WHERE title = :title")
    void updateIskconUrlByTitle(String title, String url);

    @Query("UPDATE aartis SET localAssetName = :assetName WHERE title = :title")
    void updateLocalAssetNameByTitle(String title, String assetName);

    @Query("UPDATE aartis SET isCached = :cached, cachedFilePath = :path WHERE id = :id")
    void updateCacheStatus(int id, boolean cached, String path);

    @Query("UPDATE aartis SET audioSource = :source WHERE id = :id")
    void updateAudioSource(int id, String source);

    // --- Popular / Bookmarked queries ---

    @Query("SELECT * FROM aartis WHERE id IN (SELECT contentId FROM bookmarks WHERE contentType = 'aarti') LIMIT :limit")
    LiveData<List<AartiEntity>> getBookmarkedAartis(int limit);

    @Query("SELECT * FROM aartis ORDER BY id DESC LIMIT :limit")
    LiveData<List<AartiEntity>> getPopularAartis(int limit);

    // --- Synchronous lookup for background workers ---

    @Query("SELECT id FROM aartis WHERE title = :title LIMIT 1")
    int getIdByTitle(String title);
}
