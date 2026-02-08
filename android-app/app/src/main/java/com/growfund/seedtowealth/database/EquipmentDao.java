package com.growfund.seedtowealth.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.growfund.seedtowealth.model.Equipment;

import java.util.List;

@Dao
public interface EquipmentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Equipment equipment);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Equipment> equipment);

    @Query("SELECT * FROM equipment")
    LiveData<List<Equipment>> getAllEquipment();

    @Query("SELECT * FROM equipment WHERE equipment_type = :type")
    LiveData<List<Equipment>> getEquipmentByType(String type);

    @Query("SELECT * FROM equipment WHERE id = :id")
    LiveData<Equipment> getEquipmentById(Long id);

    @Query("SELECT * FROM equipment WHERE cost <= :maxCost")
    LiveData<List<Equipment>> getAffordableEquipment(Long maxCost);

    @Query("DELETE FROM equipment")
    void deleteAll();
}
