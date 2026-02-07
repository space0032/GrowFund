package com.growfund.seedtowealth.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.growfund.seedtowealth.model.Farm;

@Dao
public interface FarmDao {
    @Query("SELECT * FROM farms LIMIT 1")
    Farm getMyFarm();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFarm(Farm farm);

    @Query("DELETE FROM farms")
    void clearFarms();
}
