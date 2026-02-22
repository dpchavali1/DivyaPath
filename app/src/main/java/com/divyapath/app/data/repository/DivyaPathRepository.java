package com.divyapath.app.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.divyapath.app.data.local.DivyaPathDatabase;
import com.divyapath.app.data.local.dao.*;
import com.divyapath.app.data.local.entity.*;

import java.util.List;

public class DivyaPathRepository {

    private final DeityDao deityDao;
    private final AartiDao aartiDao;
    private final ChalisaDao chalisaDao;
    private final MantraDao mantraDao;
    private final FestivalDao festivalDao;
    private final BookmarkDao bookmarkDao;
    private final com.divyapath.app.data.local.dao.TempleDao templeDao;
    private final BhajanDao bhajanDao;
    private final StotraDao stotraDao;
    private final ShraddhaDao shraddhaDao;

    public DivyaPathRepository(Application application) {
        DivyaPathDatabase db = DivyaPathDatabase.getDatabase(application);
        deityDao = db.deityDao();
        aartiDao = db.aartiDao();
        chalisaDao = db.chalisaDao();
        mantraDao = db.mantraDao();
        festivalDao = db.festivalDao();
        bookmarkDao = db.bookmarkDao();
        templeDao = db.templeDao();
        bhajanDao = db.bhajanDao();
        stotraDao = db.stotraDao();
        shraddhaDao = db.shraddhaDao();
    }

    // Deity operations
    public LiveData<List<DeityEntity>> getAllDeities() {
        return deityDao.getAllDeities();
    }

    public LiveData<DeityEntity> getDeityById(int id) {
        return deityDao.getDeityById(id);
    }

    public LiveData<DeityEntity> getDeityByDay(int dayOfWeek) {
        return deityDao.getDeityByDay(dayOfWeek);
    }

    // Aarti operations
    public LiveData<List<AartiEntity>> getAllAartis() {
        return aartiDao.getAllAartis();
    }

    public LiveData<AartiEntity> getAartiById(int id) {
        return aartiDao.getAartiById(id);
    }

    public LiveData<List<AartiEntity>> getAartisByDeity(int deityId) {
        return aartiDao.getAartisByDeity(deityId);
    }

    public LiveData<List<AartiEntity>> getTodaysAartis(int limit) {
        return aartiDao.getTodaysAartis(limit);
    }

    public LiveData<List<AartiEntity>> getPopularAartis(int limit) {
        return aartiDao.getPopularAartis(limit);
    }

    public LiveData<List<AartiEntity>> getBookmarkedAartis(int limit) {
        return aartiDao.getBookmarkedAartis(limit);
    }

    // Chalisa operations
    public LiveData<List<ChalisaEntity>> getAllChalisas() {
        return chalisaDao.getAllChalisas();
    }

    public LiveData<ChalisaEntity> getChalisaById(int id) {
        return chalisaDao.getChalisaById(id);
    }

    public LiveData<List<ChalisaEntity>> getChalisasByDeity(int deityId) {
        return chalisaDao.getChalisasByDeity(deityId);
    }

    // Mantra operations
    public LiveData<List<MantraEntity>> getAllMantras() {
        return mantraDao.getAllMantras();
    }

    public LiveData<MantraEntity> getMantraById(int id) {
        return mantraDao.getMantraById(id);
    }

    public LiveData<List<MantraEntity>> getMantrasByCategory(String category) {
        return mantraDao.getMantrasByCategory(category);
    }

    public LiveData<List<String>> getAllMantraCategories() {
        return mantraDao.getAllCategories();
    }

    // Festival operations
    public LiveData<List<FestivalEntity>> getAllFestivals() {
        return festivalDao.getAllFestivals();
    }

    public LiveData<FestivalEntity> getFestivalByDate(String date) {
        return festivalDao.getFestivalByDate(date);
    }

    public LiveData<List<FestivalEntity>> getFestivalsBetween(String startDate, String endDate) {
        return festivalDao.getFestivalsBetween(startDate, endDate);
    }

    public LiveData<FestivalEntity> getNextFestival(String today) {
        return festivalDao.getNextFestival(today);
    }

    // Bookmark operations
    public LiveData<List<BookmarkEntity>> getAllBookmarks() {
        return bookmarkDao.getAllBookmarks();
    }

    public LiveData<Boolean> isBookmarked(String type, int contentId) {
        return bookmarkDao.isBookmarked(type, contentId);
    }

    public void addBookmark(String contentType, int contentId) {
        DivyaPathDatabase.databaseWriteExecutor.execute(() ->
                bookmarkDao.insert(new BookmarkEntity(contentType, contentId)));
    }

    public void removeBookmark(String contentType, int contentId) {
        DivyaPathDatabase.databaseWriteExecutor.execute(() ->
                bookmarkDao.deleteByContent(contentType, contentId));
    }

    // Search operations
    public LiveData<List<AartiEntity>> searchAartis(String query) {
        return aartiDao.searchAartis(query);
    }

    public LiveData<List<ChalisaEntity>> searchChalisas(String query) {
        return chalisaDao.searchChalisas(query);
    }

    public LiveData<List<MantraEntity>> searchMantras(String query) {
        return mantraDao.searchMantras(query);
    }

    // Bhajan operations
    public LiveData<List<BhajanEntity>> getAllBhajans() {
        return bhajanDao.getAllBhajans();
    }

    public LiveData<BhajanEntity> getBhajanById(int id) {
        return bhajanDao.getBhajanById(id);
    }

    public LiveData<List<BhajanEntity>> getBhajansByCategory(String category) {
        return bhajanDao.getBhajansByCategory(category);
    }

    public LiveData<List<BhajanEntity>> searchBhajans(String query) {
        return bhajanDao.searchBhajans(query);
    }

    // Stotra operations
    public LiveData<List<StotraEntity>> getAllStotras() {
        return stotraDao.getAllStotras();
    }

    public LiveData<StotraEntity> getStotraById(int id) {
        return stotraDao.getStotraById(id);
    }

    public LiveData<List<StotraEntity>> searchStotras(String query) {
        return stotraDao.searchStotras(query);
    }

    // Temple operations
    public LiveData<List<TempleEntity>> getAllTemples() {
        return templeDao.getAllTemples();
    }

    public LiveData<List<TempleEntity>> getLiveDarshanTemples() {
        return templeDao.getLiveDarshanTemples();
    }

    public LiveData<TempleEntity> getTempleById(int id) {
        return templeDao.getTempleById(id);
    }

    // Shraddha operations
    public LiveData<List<ShraddhaEntity>> getAllShraddha() {
        return shraddhaDao.getAllShraddha();
    }

    public LiveData<ShraddhaEntity> getShraddhaById(int id) {
        return shraddhaDao.getShraddhaById(id);
    }

    public void insertShraddha(ShraddhaEntity shraddha) {
        DivyaPathDatabase.databaseWriteExecutor.execute(() -> shraddhaDao.insert(shraddha));
    }

    public void updateShraddha(ShraddhaEntity shraddha) {
        DivyaPathDatabase.databaseWriteExecutor.execute(() -> shraddhaDao.update(shraddha));
    }

    public void deleteShraddha(ShraddhaEntity shraddha) {
        DivyaPathDatabase.databaseWriteExecutor.execute(() -> shraddhaDao.delete(shraddha));
    }

    public void deleteShraddhaById(int id) {
        DivyaPathDatabase.databaseWriteExecutor.execute(() -> shraddhaDao.deleteById(id));
    }

    // Audio cache operations
    public void updateAartiCacheStatus(int id, boolean cached, String path) {
        DivyaPathDatabase.databaseWriteExecutor.execute(() ->
                aartiDao.updateCacheStatus(id, cached, path));
    }

    public void updateChalisaCacheStatus(int id, boolean cached, String path) {
        DivyaPathDatabase.databaseWriteExecutor.execute(() ->
                chalisaDao.updateCacheStatus(id, cached, path));
    }

    public void updateAartiAudioSource(int id, String source) {
        DivyaPathDatabase.databaseWriteExecutor.execute(() ->
                aartiDao.updateAudioSource(id, source));
    }

    public void updateChalisaAudioSource(int id, String source) {
        DivyaPathDatabase.databaseWriteExecutor.execute(() ->
                chalisaDao.updateAudioSource(id, source));
    }
}
