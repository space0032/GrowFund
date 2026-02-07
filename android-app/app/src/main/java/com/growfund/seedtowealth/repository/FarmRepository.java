package com.growfund.seedtowealth.repository;

import android.app.Application;
import android.util.Log;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.growfund.seedtowealth.network.ApiClient;
import com.growfund.seedtowealth.database.AppDatabase;
import com.growfund.seedtowealth.database.CropDao;
import com.growfund.seedtowealth.database.FarmDao;
import com.growfund.seedtowealth.model.Crop;
import com.growfund.seedtowealth.model.Farm;

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
    private Handler mainHandler;

    public FarmRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        farmDao = db.farmDao();
        cropDao = db.cropDao();
        executor = AppDatabase.databaseWriteExecutor;
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public void getFarm(final RepositoryCallback<Farm> callback) {
        // 1. Fetch from Local DB First
        executor.execute(() -> {
            Farm localFarm = farmDao.getMyFarm();
            if (localFarm != null) {
                mainHandler.post(() -> callback.onLocalData(localFarm));
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
                            mainHandler.post(() -> callback.onSuccess(remoteFarm));
                        });
                    } else {
                        mainHandler.post(() -> callback.onError("Failed to fetch farm: " + response.code()));
                    }
                }

                @Override
                public void onFailure(Call<Farm> call, Throwable t) {
                    mainHandler.post(() -> callback.onError("Network error: " + t.getMessage()));
                }
            });
        });
    }

    public void getCrops(Long farmId, final RepositoryCallback<List<Crop>> callback) {
        // 1. Fetch from Local DB First
        executor.execute(() -> {
            List<Crop> localCrops = cropDao.getCropsByFarmId(farmId);
            if (localCrops != null && !localCrops.isEmpty()) {
                mainHandler.post(() -> callback.onLocalData(localCrops));
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
                            mainHandler.post(() -> callback.onSuccess(remoteCrops));
                        });
                    } else {
                        mainHandler.post(() -> callback.onError("Failed to fetch crops: " + response.code()));
                    }
                }

                @Override
                public void onFailure(Call<List<Crop>> call, Throwable t) {
                    mainHandler.post(() -> callback.onError("Network error: " + t.getMessage()));
                }
            });
        });
    }

    public void getCrop(Long cropId, final RepositoryCallback<Crop> callback) {
        // 1. Try local DB first
        executor.execute(() -> {
            Crop localCrop = cropDao.getCropById(cropId);
            if (localCrop != null) {
                mainHandler.post(() -> callback.onLocalData(localCrop));
            }

            // 2. Fetch from API
            ApiClient.getApiService().getCrop(cropId).enqueue(new Callback<Crop>() {
                @Override
                public void onResponse(Call<Crop> call, Response<Crop> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Crop remoteCrop = response.body();
                        executor.execute(() -> {
                            cropDao.insertCrop(remoteCrop);
                            mainHandler.post(() -> callback.onSuccess(remoteCrop));
                        });
                    } else {
                        mainHandler.post(() -> callback.onError("Failed to fetch crop: " + response.code()));
                    }
                }

                @Override
                public void onFailure(Call<Crop> call, Throwable t) {
                    mainHandler.post(() -> callback.onError("Network error: " + t.getMessage()));
                }
            });
        });
    }

    public void harvestCrop(Long cropId, final RepositoryCallback<Crop> callback) {
        // Harvest requires API call (server-side logic)
        ApiClient.getApiService().harvestCrop(cropId).enqueue(new Callback<Crop>() {
            @Override
            public void onResponse(Call<Crop> call, Response<Crop> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Crop harvestedCrop = response.body();
                    executor.execute(() -> {
                        cropDao.insertCrop(harvestedCrop);
                        mainHandler.post(() -> callback.onSuccess(harvestedCrop));
                    });
                } else {
                    mainHandler.post(() -> callback.onError("Failed to harvest crop: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Crop> call, Throwable t) {
                mainHandler.post(() -> callback.onError("Network error: " + t.getMessage()));
            }
        });
    }

    public void plantCrop(Long farmId, java.util.Map<String, Object> request, final RepositoryCallback<Crop> callback) {
        // Plant requires API call (server-side logic)
        ApiClient.getApiService().plantCrop(farmId, request).enqueue(new Callback<Crop>() {
            @Override
            public void onResponse(Call<Crop> call, Response<Crop> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Crop plantedCrop = response.body();
                    executor.execute(() -> {
                        cropDao.insertCrop(plantedCrop);
                        mainHandler.post(() -> callback.onSuccess(plantedCrop));
                    });
                } else {
                    mainHandler.post(() -> callback.onError("Failed to plant crop: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Crop> call, Throwable t) {
                mainHandler.post(() -> callback.onError("Network error: " + t.getMessage()));
            }
        });
    }

    public interface RepositoryCallback<T> {
        void onLocalData(T data); // Optional: Called when local data is available

        void onSuccess(T data);

        void onError(String message);
    }
}
