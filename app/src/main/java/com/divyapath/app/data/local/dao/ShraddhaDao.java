package com.divyapath.app.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.divyapath.app.data.local.entity.ShraddhaEntity;

import java.util.List;

@Dao
public interface ShraddhaDao {

    @Insert
    long insert(ShraddhaEntity shraddha);

    @Update
    void update(ShraddhaEntity shraddha);

    @Delete
    void delete(ShraddhaEntity shraddha);

    @Query("SELECT * FROM shraddha ORDER BY lunarMonth, tithiIndex")
    LiveData<List<ShraddhaEntity>> getAllShraddha();

    @Query("SELECT * FROM shraddha WHERE id = :id")
    LiveData<ShraddhaEntity> getShraddhaById(int id);

    @Query("SELECT * FROM shraddha WHERE id = :id")
    ShraddhaEntity getShraddhaByIdSync(int id);

    @Query("DELETE FROM shraddha WHERE id = :id")
    void deleteById(int id);

    @Query("SELECT * FROM shraddha WHERE tithiIndex = :tithiIndex")
    List<ShraddhaEntity> getShraddhaByTithi(int tithiIndex);
}
