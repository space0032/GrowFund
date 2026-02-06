package com.growfund.seedtowealth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.growfund.seedtowealth.adapter.CropAdapter;
import com.growfund.seedtowealth.model.Crop;
import com.growfund.seedtowealth.model.Farm;
import com.growfund.seedtowealth.network.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FarmActivity extends AppCompatActivity {
    private static final String TAG = "FarmActivity";

    private TextView farmNameText, landSizeText, savingsText, emergencyFundText;
    private RecyclerView cropsRecyclerView;
    private ProgressBar loadingProgress;
    private FloatingActionButton plantCropFab;

    private Farm currentFarm;
    private List<Crop> cropList = new ArrayList<>();
    private CropAdapter cropAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm);

        initViews();
        loadFarmData();
    }

    private void initViews() {
        farmNameText = findViewById(R.id.farmNameText);
        landSizeText = findViewById(R.id.landSizeText);
        savingsText = findViewById(R.id.savingsText);
        emergencyFundText = findViewById(R.id.emergencyFundText);
        cropsRecyclerView = findViewById(R.id.cropsRecyclerView);
        loadingProgress = findViewById(R.id.loadingProgress);
        plantCropFab = findViewById(R.id.plantCropFab);

        cropsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        cropAdapter = new CropAdapter(crop -> {
            // TODO: Handle crop click - navigate to crop details
            Toast.makeText(this, "Crop: " + crop.getCropType(), Toast.LENGTH_SHORT).show();
        });
        cropsRecyclerView.setAdapter(cropAdapter);

        plantCropFab.setOnClickListener(v -> {
            if (currentFarm != null) {
                Intent intent = new Intent(FarmActivity.this, PlantCropActivity.class);
                intent.putExtra("farmId", currentFarm.getId());
                startActivity(intent);
            } else {
                Toast.makeText(FarmActivity.this, "Please wait, loading farm data...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadFarmData() {
        loadingProgress.setVisibility(View.VISIBLE);

        ApiClient.getApiService().getMyFarm().enqueue(new Callback<Farm>() {
            @Override
            public void onResponse(Call<Farm> call, Response<Farm> response) {
                loadingProgress.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    currentFarm = response.body();
                    updateFarmUI();
                    loadCrops();
                } else if (response.code() == 404) {
                    // Farm doesn't exist, create one
                    Log.d(TAG, "No farm found, creating new farm");
                    createFarm();
                } else {
                    Toast.makeText(FarmActivity.this, "Failed to load farm: " + response.code(), Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<Farm> call, Throwable t) {
                loadingProgress.setVisibility(View.GONE);
                Log.e(TAG, "Error loading farm", t);
                Toast.makeText(FarmActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void createFarm() {
        loadingProgress.setVisibility(View.VISIBLE);

        java.util.Map<String, String> request = new java.util.HashMap<>();
        request.put("farmName", "My Farm");
        request.put("userId", "1"); // TODO: Get actual user ID

        ApiClient.getApiService().createFarm(request).enqueue(new Callback<Farm>() {
            @Override
            public void onResponse(Call<Farm> call, Response<Farm> response) {
                loadingProgress.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    currentFarm = response.body();
                    updateFarmUI();
                    Toast.makeText(FarmActivity.this, "Farm created successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FarmActivity.this, "Failed to create farm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Farm> call, Throwable t) {
                loadingProgress.setVisibility(View.GONE);
                Log.e(TAG, "Error creating farm", t);
                Toast.makeText(FarmActivity.this, "Error creating farm: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateFarmUI() {
        farmNameText.setText(currentFarm.getFarmName());
        landSizeText.setText(String.format("%.1f acres", currentFarm.getLandSize()));
        savingsText.setText(String.format("₹%,d", currentFarm.getSavings()));
        emergencyFundText.setText(String.format("₹%,d", currentFarm.getEmergencyFund()));
    }

    private void loadCrops() {
        if (currentFarm == null)
            return;

        ApiClient.getApiService().getCrops(currentFarm.getId()).enqueue(new Callback<List<Crop>>() {
            @Override
            public void onResponse(Call<List<Crop>> call, Response<List<Crop>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cropList = response.body();
                    cropAdapter.setCrops(cropList);
                    Log.d(TAG, "Loaded " + cropList.size() + " crops");
                }
            }

            @Override
            public void onFailure(Call<List<Crop>> call, Throwable t) {
                Log.e(TAG, "Error loading crops", t);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentFarm != null) {
            loadCrops(); // Refresh crops when returning from PlantCropActivity
        }
    }
}
