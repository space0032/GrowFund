package com.growfund.seedtowealth.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.growfund.seedtowealth.model.Crop;

import java.util.List;

@Dao
public interface CropDao {
    @Query("SELECT * FROM crops WHERE farm_id = :farmId")
    List<Crop> getCropsByFarmId(Long farmId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCrops(List<Crop> crops);

    @Query("DELETE FROM crops WHERE farm_id = :farmId")
    void deleteCropsByFarmId(Long farmId);

    @Transaction
    default void updateCropsForFarm(Long farmId, List<Crop> crops) {
        deleteCropsByFarmId(farmId);
        // Set farmId for all crops before inserting
        for (Crop crop : crops) {
            crop.setFarmId(farmId);
        }
        insertCrops(crops);
    }
}
