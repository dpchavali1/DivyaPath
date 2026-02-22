package com.divyapath.app.data.local.dao;
import androidx.lifecycle.LiveData; import androidx.room.*; import com.divyapath.app.data.local.entity.FestivalEntity; import java.util.List;
@Dao public interface FestivalDao {
    @Insert(onConflict=OnConflictStrategy.REPLACE) void insertAll(List<FestivalEntity> festivals);
    @Query("SELECT * FROM festivals ORDER BY date ASC") LiveData<List<FestivalEntity>> getAllFestivals();
    @Query("SELECT * FROM festivals WHERE date = :date") LiveData<FestivalEntity> getFestivalByDate(String date);
    @Query("SELECT * FROM festivals WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC") LiveData<List<FestivalEntity>> getFestivalsBetween(String startDate, String endDate);
    @Query("SELECT * FROM festivals WHERE date >= :today ORDER BY date ASC LIMIT 1") LiveData<FestivalEntity> getNextFestival(String today);
    @Query("SELECT COUNT(*) FROM festivals") int getCount();
    @Query("SELECT * FROM festivals WHERE date = :date") FestivalEntity getFestivalByDateSync(String date);
}
