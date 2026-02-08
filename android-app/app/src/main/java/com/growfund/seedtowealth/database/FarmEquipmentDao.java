package com.growfund.seedtowealth.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.growfund.seedtowealth.model.FarmEquipment;

import java.util.List;

@Dao
public interface FarmEquipmentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FarmEquipment farmEquipment);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<FarmEquipment> farmEquipment);

    @Query("SELECT * FROM farm_equipment WHERE farm_id = :farmId")
    LiveData<List<FarmEquipment>> getFarmEquipment(Long farmId);

    @Query("SELECT * FROM farm_equipment WHERE farm_id = :farmId AND active = 1")
    LiveData<List<FarmEquipment>> getActiveFarmEquipment(Long farmId);

    @Query("SELECT * FROM farm_equipment WHERE farm_id = :farmId AND active = 1 AND durability_remaining > 0")
    LiveData<List<FarmEquipment>> getUsableFarmEquipment(Long farmId);

    @Query("SELECT * FROM farm_equipment WHERE id = :id")
    LiveData<FarmEquipment> getFarmEquipmentById(Long id);

    @Query("DELETE FROM farm_equipment WHERE farm_id = :farmId")
    void deleteByFarmId(Long farmId);

    @Query("DELETE FROM farm_equipment")
    void deleteAll();
}
