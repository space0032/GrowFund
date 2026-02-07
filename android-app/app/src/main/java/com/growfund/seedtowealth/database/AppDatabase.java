package com.growfund.seedtowealth.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.growfund.seedtowealth.model.Crop;
import com.growfund.seedtowealth.model.Farm;
import com.growfund.seedtowealth.model.Investment;
import com.growfund.seedtowealth.model.RandomEvent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = { Farm.class, Crop.class, Investment.class, RandomEvent.class }, version = 4, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract FarmDao farmDao();

    public abstract CropDao cropDao();

    public abstract InvestmentDao investmentDao();

    public abstract RandomEventDao randomEventDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "growfund_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
