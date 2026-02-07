package com.growfund.seedtowealth.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.growfund.seedtowealth.model.RandomEvent;

import java.util.List;

@Dao
public interface RandomEventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(RandomEvent event);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<RandomEvent> events);

    @Query("SELECT * FROM random_events WHERE active = 1 ORDER BY createdAt DESC")
    List<RandomEvent> getActiveEvents();

    @Query("SELECT * FROM random_events WHERE active = 1 ORDER BY createdAt DESC")
    LiveData<List<RandomEvent>> getActiveEventsLiveData();

    @Query("SELECT * FROM random_events ORDER BY createdAt DESC LIMIT 30")
    List<RandomEvent> getRecentEvents();

    @Query("SELECT * FROM random_events WHERE id = :eventId")
    RandomEvent getEventById(Long eventId);

    @Query("DELETE FROM random_events WHERE active = 0 AND createdAt < :cutoffDate")
    void deleteOldInactiveEvents(String cutoffDate);

    @Query("DELETE FROM random_events")
    void deleteAll();

    @Query("UPDATE random_events SET active = 0 WHERE endTime < :currentTime")
    void deactivateExpiredEvents(String currentTime);
}
