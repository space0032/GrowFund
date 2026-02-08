package com.growfund.seedtowealth.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.growfund.seedtowealth.database.AppDatabase;
import com.growfund.seedtowealth.database.EquipmentDao;
import com.growfund.seedtowealth.database.FarmEquipmentDao;
import com.growfund.seedtowealth.model.Equipment;
import com.growfund.seedtowealth.model.FarmEquipment;
import com.growfund.seedtowealth.network.ApiClient;
import com.growfund.seedtowealth.network.ApiService;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EquipmentRepository {
    private static final String TAG = "EquipmentRepository";

    private final EquipmentDao equipmentDao;
    private final FarmEquipmentDao farmEquipmentDao;
    private final ApiService apiService;

    public EquipmentRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        equipmentDao = db.equipmentDao();
        farmEquipmentDao = db.farmEquipmentDao();
        apiService = ApiClient.getApiService();
    }

    // Equipment Catalog
    public LiveData<List<Equipment>> getAllEquipment() {
        return equipmentDao.getAllEquipment();
    }

    public LiveData<List<Equipment>> getEquipmentByType(String type) {
        return equipmentDao.getEquipmentByType(type);
    }

    public void fetchEquipmentCatalog(RepositoryCallback<List<Equipment>> callback) {
        apiService.getAllEquipment().enqueue(new Callback<List<Equipment>>() {
            @Override
            public void onResponse(Call<List<Equipment>> call, Response<List<Equipment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Equipment> equipment = response.body();
                    // Cache in database
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        equipmentDao.insertAll(equipment);
                    });
                    callback.onSuccess(equipment);
                } else {
                    callback.onError("Failed to fetch equipment catalog");
                }
            }

            @Override
            public void onFailure(Call<List<Equipment>> call, Throwable t) {
                Log.e(TAG, "Error fetching equipment: " + t.getMessage());
                callback.onError(t.getMessage());
            }
        });
    }

    // Farm Equipment
    public LiveData<List<FarmEquipment>> getFarmEquipment(Long farmId) {
        return farmEquipmentDao.getFarmEquipment(farmId);
    }

    public LiveData<List<FarmEquipment>> getUsableFarmEquipment(Long farmId) {
        return farmEquipmentDao.getUsableFarmEquipment(farmId);
    }

    public void fetchFarmEquipment(Long farmId, RepositoryCallback<List<FarmEquipment>> callback) {
        apiService.getFarmEquipment(farmId).enqueue(new Callback<List<FarmEquipment>>() {
            @Override
            public void onResponse(Call<List<FarmEquipment>> call, Response<List<FarmEquipment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<FarmEquipment> farmEquipment = response.body();
                    // Cache in database
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        farmEquipmentDao.insertAll(farmEquipment);
                    });
                    callback.onSuccess(farmEquipment);
                } else {
                    callback.onError("Failed to fetch farm equipment");
                }
            }

            @Override
            public void onFailure(Call<List<FarmEquipment>> call, Throwable t) {
                Log.e(TAG, "Error fetching farm equipment: " + t.getMessage());
                callback.onError(t.getMessage());
            }
        });
    }

    // Purchase Equipment
    public void purchaseEquipment(Long farmId, Long equipmentId, RepositoryCallback<Map<String, Object>> callback) {
        apiService.purchaseEquipment(farmId, equipmentId).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                    // Refresh farm equipment
                    fetchFarmEquipment(farmId, new RepositoryCallback<List<FarmEquipment>>() {
                        @Override
                        public void onSuccess(List<FarmEquipment> result) {
                        }

                        @Override
                        public void onError(String error) {
                        }
                    });
                } else {
                    callback.onError("Purchase failed");
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.e(TAG, "Error purchasing equipment: " + t.getMessage());
                callback.onError(t.getMessage());
            }
        });
    }

    // Get Equipment Bonuses
    public void getEquipmentBonuses(Long farmId, RepositoryCallback<Map<String, Double>> callback) {
        apiService.getEquipmentBonuses(farmId).enqueue(new Callback<Map<String, Double>>() {
            @Override
            public void onResponse(Call<Map<String, Double>> call, Response<Map<String, Double>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to get bonuses");
                }
            }

            @Override
            public void onFailure(Call<Map<String, Double>> call, Throwable t) {
                Log.e(TAG, "Error getting bonuses: " + t.getMessage());
                callback.onError(t.getMessage());
            }
        });
    }

    public interface RepositoryCallback<T> {
        void onSuccess(T result);

        void onError(String error);
    }
}
