package com.growfund.seedtowealth.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.growfund.seedtowealth.database.AppDatabase;
import com.growfund.seedtowealth.database.CropDao;
import com.growfund.seedtowealth.database.FarmDao;
import com.growfund.seedtowealth.model.Crop;
import com.growfund.seedtowealth.model.Farm;
import com.growfund.seedtowealth.network.ApiClient;

import java.util.List;
import java.util.concurrent.ExecutorService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FarmRepository {
    private static final String TAG = "FarmRepository";

    private FarmDao farmDao;
    private CropDao cropDao;
    private ExecutorService executor;

    public FarmRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        farmDao = db.farmDao();
        cropDao = db.cropDao();
        executor = AppDatabase.databaseWriteExecutor;
    }

    public void getFarm(final RepositoryCallback<Farm> callback) {
        // 1. Fetch from Local DB First
        executor.execute(() -> {
            Farm localFarm = farmDao.getMyFarm();
            if (localFarm != null) {
                callback.onLocalData(localFarm);
            }

            // 2. Fetch from API
            ApiClient.getApiService().getMyFarm().enqueue(new Callback<Farm>() {
                @Override
                public void onResponse(Call<Farm> call, Response<Farm> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Farm remoteFarm = response.body();
                        // 3. Update Local DB
                        executor.execute(() -> {
                            farmDao.insertFarm(remoteFarm);
                            callback.onSuccess(remoteFarm);
                        });
                    } else {
                        callback.onError("Failed to fetch farm: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Farm> call, Throwable t) {
                    callback.onError("Network error: " + t.getMessage());
                }
            });
        });
    }

    public void getCrops(Long farmId, final RepositoryCallback<List<Crop>> callback) {
        // 1. Fetch from Local DB First
        executor.execute(() -> {
            List<Crop> localCrops = cropDao.getCropsByFarmId(farmId);
            if (localCrops != null && !localCrops.isEmpty()) {
                callback.onLocalData(localCrops);
            }

            // 2. Fetch from API
            ApiClient.getApiService().getCrops(farmId).enqueue(new Callback<List<Crop>>() {
                @Override
                public void onResponse(Call<List<Crop>> call, Response<List<Crop>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Crop> remoteCrops = response.body();
                        // 3. Update Local DB
                        executor.execute(() -> {
                            cropDao.updateCropsForFarm(farmId, remoteCrops);
                            callback.onSuccess(remoteCrops);
                        });
                    } else {
                        callback.onError("Failed to fetch crops: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<List<Crop>> call, Throwable t) {
                    callback.onError("Network error: " + t.getMessage());
                }
            });
        });
    }

    public interface RepositoryCallback<T> {
        void onLocalData(T data); // Optional: Called when local data is available

        void onSuccess(T data);

        void onError(String message);
    }
}
