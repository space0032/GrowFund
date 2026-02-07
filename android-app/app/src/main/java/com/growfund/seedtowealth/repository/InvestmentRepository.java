package com.growfund.seedtowealth.repository;

import android.app.Application;
import android.util.Log;

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

    public InvestmentRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        investmentDao = db.investmentDao();
        executor = AppDatabase.databaseWriteExecutor;
    }

    public void getActiveInvestments(final RepositoryCallback<List<Investment>> callback) {
        // 1. Fetch from Local DB First
        executor.execute(() -> {
            List<Investment> localInvestments = investmentDao.getActiveInvestments();
            if (localInvestments != null && !localInvestments.isEmpty()) {
                callback.onLocalData(localInvestments);
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
                            callback.onSuccess(remoteInvestments);
                        });
                    } else {
                        callback.onError("Failed to fetch investments: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<List<Investment>> call, Throwable t) {
                    callback.onError("Network error: " + t.getMessage());
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
                        callback.onSuccess(createdInvestment);
                    });
                } else {
                    callback.onError("Failed to create investment: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Investment> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public interface RepositoryCallback<T> {
        void onLocalData(T data); // Optional: Called when local data is available

        void onSuccess(T data);

        void onError(String message);
    }
}
