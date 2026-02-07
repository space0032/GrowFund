package com.growfund.seedtowealth.repository;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.growfund.seedtowealth.database.AppDatabase;
import com.growfund.seedtowealth.database.InvestmentDao;
import com.growfund.seedtowealth.model.Investment;
import com.growfund.seedtowealth.network.ApiClient;

import java.util.List;
import java.util.concurrent.ExecutorService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InvestmentRepository {
    private static final String TAG = "InvestmentRepository";

    private InvestmentDao investmentDao;
    private ExecutorService executor;
    private Handler mainHandler;

    public InvestmentRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        investmentDao = db.investmentDao();
        executor = AppDatabase.databaseWriteExecutor;
        mainHandler = new Handler(Looper.getMainLooper());
    }

    // ===== LiveData Methods (Reactive) =====

    /**
     * Get active investments as LiveData for reactive updates.
     */
    public LiveData<List<Investment>> getActiveInvestmentsLiveData() {
        MutableLiveData<List<Investment>> liveData = new MutableLiveData<>();

        executor.execute(() -> {
            // Load from local DB first
            List<Investment> localInvestments = investmentDao.getActiveInvestments();
            if (localInvestments != null && !localInvestments.isEmpty()) {
                liveData.postValue(localInvestments);
            }

            // Fetch from API and update
            ApiClient.getApiService().getMyActiveInvestments().enqueue(new Callback<List<Investment>>() {
                @Override
                public void onResponse(Call<List<Investment>> call, Response<List<Investment>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Investment> remoteInvestments = response.body();
                        executor.execute(() -> {
                            investmentDao.insertInvestments(remoteInvestments);
                            liveData.postValue(remoteInvestments);
                        });
                    }
                }

                @Override
                public void onFailure(Call<List<Investment>> call, Throwable t) {
                    Log.e(TAG, "Failed to fetch investments: " + t.getMessage());
                }
            });
        });

        return liveData;
    }

    // ===== Callback Methods (Legacy) =====

    public void getActiveInvestments(final RepositoryCallback<List<Investment>> callback) {
        // 1. Fetch from Local DB First
        executor.execute(() -> {
            List<Investment> localInvestments = investmentDao.getActiveInvestments();
            if (localInvestments != null && !localInvestments.isEmpty()) {
                mainHandler.post(() -> callback.onLocalData(localInvestments));
            }

            // 2. Fetch from API
            ApiClient.getApiService().getMyActiveInvestments().enqueue(new Callback<List<Investment>>() {
                @Override
                public void onResponse(Call<List<Investment>> call, Response<List<Investment>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Investment> remoteInvestments = response.body();
                        // 3. Update Local DB
                        executor.execute(() -> {
                            investmentDao.insertInvestments(remoteInvestments);
                            mainHandler.post(() -> callback.onSuccess(remoteInvestments));
                        });
                    } else {
                        mainHandler.post(() -> callback.onError("Failed to fetch investments: " + response.code()));
                    }
                }

                @Override
                public void onFailure(Call<List<Investment>> call, Throwable t) {
                    mainHandler.post(() -> callback.onError("Network error: " + t.getMessage()));
                }
            });
        });
    }

    public void createInvestment(Investment investment, final RepositoryCallback<Investment> callback) {
        // Create investment via API
        ApiClient.getApiService().createInvestment(investment).enqueue(new Callback<Investment>() {
            @Override
            public void onResponse(Call<Investment> call, Response<Investment> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Investment createdInvestment = response.body();
                    // Save to local DB
                    executor.execute(() -> {
                        investmentDao.insertInvestment(createdInvestment);
                        mainHandler.post(() -> callback.onSuccess(createdInvestment));
                    });
                } else {
                    mainHandler.post(() -> callback.onError("Failed to create investment: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Investment> call, Throwable t) {
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
