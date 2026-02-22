package com.divyapath.app.data.local.dao;
import androidx.lifecycle.LiveData; import androidx.room.*; import com.divyapath.app.data.local.entity.DeityEntity; import java.util.List;
@Dao public interface DeityDao {
    @Insert(onConflict=OnConflictStrategy.REPLACE) void insertAll(List<DeityEntity> deities);
    @Insert(onConflict=OnConflictStrategy.REPLACE) void insert(DeityEntity deity);
    @Query("SELECT * FROM deities") LiveData<List<DeityEntity>> getAllDeities();
    @Query("SELECT * FROM deities WHERE id = :id") LiveData<DeityEntity> getDeityById(int id);
    @Query("SELECT * FROM deities WHERE dayOfWeek = :dayOfWeek LIMIT 1") LiveData<DeityEntity> getDeityByDay(int dayOfWeek);
    @Query("SELECT * FROM deities WHERE dayOfWeek = :dayOfWeek LIMIT 1") DeityEntity getDeityByDaySync(int dayOfWeek);
    @Query("SELECT COUNT(*) FROM deities") int getCount();
}
