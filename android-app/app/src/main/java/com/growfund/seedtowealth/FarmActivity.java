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

    private com.growfund.seedtowealth.utils.SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm);

        sessionManager = new com.growfund.seedtowealth.utils.SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            // Redirect to Login if not logged in (implementation detail, for now just
            // continue or finish)
            // Intent intent = new Intent(this, LoginActivity.class);
            // startActivity(intent);
            // finish();
        }

        initViews();
        loadFarmData();

        // Notifications
        com.growfund.seedtowealth.utils.NotificationHelper.createNotificationChannel(this);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                androidx.core.app.ActivityCompat.requestPermissions(this,
                        new String[] { android.Manifest.permission.POST_NOTIFICATIONS }, 101);
            }
        }
    }

    private View emptyStateView;

    private void initViews() {
        farmNameText = findViewById(R.id.farmNameText);
        landSizeText = findViewById(R.id.landSizeText);
        savingsText = findViewById(R.id.savingsText);
        emergencyFundText = findViewById(R.id.emergencyFundText);
        cropsRecyclerView = findViewById(R.id.cropsRecyclerView);
        emptyStateView = findViewById(R.id.emptyStateView);
        loadingProgress = findViewById(R.id.loadingProgress);
        plantCropFab = findViewById(R.id.plantCropFab);
        TextView weatherText = findViewById(R.id.weatherText);

        findViewById(R.id.leaderboardButton).setOnClickListener(v -> {
            Intent intent = new Intent(FarmActivity.this, LeaderboardActivity.class);
            startActivity(intent);
        });

        loadWeather(weatherText);

        findViewById(R.id.investmentButton).setOnClickListener(v -> {
            Intent intent = new Intent(FarmActivity.this, InvestmentActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.achievementsButton).setOnClickListener(v -> {
            Intent intent = new Intent(FarmActivity.this, AchievementsActivity.class);
            startActivity(intent);
        });

        cropsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        cropAdapter = new CropAdapter(crop -> {
            Intent intent = new Intent(this, CropDetailActivity.class);
            intent.putExtra("cropId", crop.getId());
            startActivity(intent);
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

        // Initialize Daily Tip & Quiz
        TextView dailyTipText = findViewById(R.id.dailyTipText);
        findViewById(R.id.quizButton).setOnClickListener(v -> {
            if (currentFarm != null) {
                Intent intent = new Intent(FarmActivity.this, QuizActivity.class);
                startActivity(intent);
            }
        });
        loadDailyTip(dailyTipText);
    }

    private void loadDailyTip(TextView tipView) {
        String[] tips = {
                "Diversifying your crops can protect against price fluctuations.",
                "Soil testing helps you use the right amount of fertilizer.",
                "Drip irrigation saves water and improves yield.",
                "Always keep an emergency fund for unexpected weather events.",
                "Government schemes like KCC offer low-interest loans.",
                "Crop insurance is a safety net against crop failure.",
                "Rotating crops maintains soil fertility naturally.",
                "Selling directly to markets can get you better prices than middlemen."
        };
        int randomIndex = (int) (Math.random() * tips.length);
        tipView.setText(tips[randomIndex]);
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
                com.growfund.seedtowealth.utils.ErrorHandler.handleError(FarmActivity.this, t);
            }
        });
    }

    private void createFarm() {
        loadingProgress.setVisibility(View.VISIBLE);

        java.util.Map<String, String> request = new java.util.HashMap<>();
        request.put("farmName", "My Farm");
        // userId is now handled by backend via Auth token

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

    private void showExpandDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Expand Farm")
                .setMessage("Expand your farm by 1 acre for ₹50,000?")
                .setPositiveButton("Expand", (dialog, which) -> expandFarm())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void expandFarm() {
        loadingProgress.setVisibility(View.VISIBLE);
        ApiClient.getApiService().expandFarm().enqueue(new Callback<Farm>() {
            @Override
            public void onResponse(Call<Farm> call, Response<Farm> response) {
                loadingProgress.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    currentFarm = response.body();
                    updateFarmUI();
                    Toast.makeText(FarmActivity.this, "Farm Expanded!", Toast.LENGTH_SHORT).show();
                } else {
                    com.growfund.seedtowealth.utils.ErrorHandler.handleApiError(FarmActivity.this, response);
                }
            }

            @Override
            public void onFailure(Call<Farm> call, Throwable t) {
                loadingProgress.setVisibility(View.GONE);
                com.growfund.seedtowealth.utils.ErrorHandler.handleError(FarmActivity.this, t);
            }
        });
    }

    private void updateFarmUI() {
        farmNameText.setText(currentFarm.getFarmName());
        landSizeText.setText(String.format("%.1f acres (Tap to expand)", currentFarm.getLandSize()));
        savingsText.setText(String.format("₹%,d", currentFarm.getSavings()));
        emergencyFundText.setText(String.format("₹%,d", currentFarm.getEmergencyFund()));

        if (currentFarm.getSavings() >= 50000) {
            landSizeText.setOnClickListener(v -> showExpandDialog());
        } else {
            landSizeText.setOnClickListener(null);
        }
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

                    if (cropList.isEmpty()) {
                        cropsRecyclerView.setVisibility(View.GONE);
                        emptyStateView.setVisibility(View.VISIBLE);
                    } else {
                        cropsRecyclerView.setVisibility(View.VISIBLE);
                        emptyStateView.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Crop>> call, Throwable t) {
                Log.e(TAG, "Error loading crops", t);
                // Can also show empty state or error state here
            }
        });
    }

    private void loadWeather(TextView weatherText) {
        ApiClient.getApiService().getCurrentWeather().enqueue(new Callback<java.util.Map<String, Object>>() {
            @Override
            public void onResponse(Call<java.util.Map<String, Object>> call,
                    Response<java.util.Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    java.util.Map<String, Object> body = response.body();
                    String condition = (String) body.get("condition"); // SUNNY
                    String displayName = (String) body.get("displayName"); // Sunny

                    String emoji = "☀️";
                    if ("RAINY".equals(condition))
                        emoji = "🌧️";
                    else if ("DROUGHT".equals(condition))
                        emoji = "🏜️";

                    weatherText.setText(emoji + " " + displayName);
                } else {
                    weatherText.setText("Weather: Unknown");
                }
            }

            @Override
            public void onFailure(Call<java.util.Map<String, Object>> call, Throwable t) {
                weatherText.setText("Weather: --");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentFarm != null) {
            loadCrops(); // Refresh crops when returning from PlantCropActivity
        }
        // Refresh weather too? Maybe.
    }
}
