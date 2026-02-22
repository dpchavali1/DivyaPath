package com.divyapath.app.data.local.dao;
import androidx.lifecycle.LiveData; import androidx.room.*; import com.divyapath.app.data.local.entity.TempleEntity; import java.util.List;
@Dao public interface TempleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) void insertAll(List<TempleEntity> temples);
    @Query("SELECT * FROM temples") LiveData<List<TempleEntity>> getAllTemples();
    @Query("SELECT * FROM temples WHERE hasLiveDarshan = 1") LiveData<List<TempleEntity>> getLiveDarshanTemples();
    @Query("SELECT * FROM temples WHERE id = :id") LiveData<TempleEntity> getTempleById(int id);
    @Query("SELECT COUNT(*) FROM temples") int getCount();
}
