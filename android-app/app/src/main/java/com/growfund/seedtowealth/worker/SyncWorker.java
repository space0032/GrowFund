package com.growfund.seedtowealth.worker;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.growfund.seedtowealth.database.AppDatabase;
import com.growfund.seedtowealth.model.Crop;
import com.growfund.seedtowealth.model.Farm;
import com.growfund.seedtowealth.network.ApiClient;
import com.growfund.seedtowealth.utils.SessionManager;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;

public class SyncWorker extends Worker {

    private final AppDatabase database;
    private final SessionManager sessionManager;

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        database = AppDatabase.getInstance(context);
        sessionManager = new SessionManager(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        if (!sessionManager.isLoggedIn()) {
            // Stop retrying if user is not logged in
            return Result.success();
        }

        try {
            // 1. Fetch Farm
            Response<Farm> farmResponse = ApiClient.getApiService().getMyFarm().execute();
            if (farmResponse.isSuccessful() && farmResponse.body() != null) {
                Farm farm = farmResponse.body();
                database.farmDao().insertFarm(farm);

                // 2. Fetch Crops
                // We need the farm ID to fetch crops.
                Response<List<Crop>> cropsResponse = ApiClient.getApiService().getCrops(farm.getId()).execute();
                if (cropsResponse.isSuccessful() && cropsResponse.body() != null) {
                    List<Crop> crops = cropsResponse.body();
                    // Associate crops with farmId ensure consistency
                    for (Crop crop : crops) {
                        crop.setFarmId(farm.getId());
                    }
                    database.cropDao().updateCropsForFarm(farm.getId(), crops);
                }

                // 3. Fetch Investments
                Response<List<com.growfund.seedtowealth.model.Investment>> investmentsResponse = ApiClient
                        .getApiService().getMyActiveInvestments().execute();
                if (investmentsResponse.isSuccessful() && investmentsResponse.body() != null) {
                    List<com.growfund.seedtowealth.model.Investment> investments = investmentsResponse.body();
                    database.investmentDao().insertInvestments(investments);
                }
            } else {
                // Determine if we should retry based on error code
                if (farmResponse.code() >= 500) {
                    return Result.retry();
                }
                // 4xx errors usually mean client issue or not found, no point retrying
                // immediately
            }

            return Result.success();
        } catch (IOException e) {
            e.printStackTrace();
            return Result.retry();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure();
        }
    }
}
