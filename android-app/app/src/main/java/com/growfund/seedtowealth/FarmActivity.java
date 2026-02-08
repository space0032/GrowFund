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
import com.growfund.seedtowealth.adapter.EventAdapter;
import com.growfund.seedtowealth.model.Crop;
import com.growfund.seedtowealth.model.Farm;
import com.growfund.seedtowealth.model.RandomEvent;
import com.growfund.seedtowealth.network.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import java.util.concurrent.TimeUnit;
import com.growfund.seedtowealth.worker.SyncWorker;

public class FarmActivity extends AppCompatActivity {
    private static final String TAG = "FarmActivity";

    @Override
    protected void attachBaseContext(android.content.Context newBase) {
        super.attachBaseContext(com.growfund.seedtowealth.utils.LanguageManager.applyLanguage(newBase));
    }

    private TextView farmNameText, landSizeText, savingsText, emergencyFundText;
    private RecyclerView cropsRecyclerView;
    private RecyclerView eventsRecyclerView;
    private View eventsSection;
    private ProgressBar loadingProgress;
    private FloatingActionButton plantCropFab;

    private com.growfund.seedtowealth.repository.FarmRepository farmRepository;
    private com.growfund.seedtowealth.repository.EventRepository eventRepository;
    private com.growfund.seedtowealth.utils.SessionManager sessionManager;
    private androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefreshLayout;
    private android.view.View emptyStateView;
    private Farm currentFarm;
    private List<Crop> cropList = new ArrayList<>();
    private CropAdapter cropAdapter;
    private EventAdapter eventAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm);

        // Enable StrictMode for development builds
        com.growfund.seedtowealth.utils.StrictModeConfig.enableStrictMode(this);

        // Initialize Repositories
        farmRepository = new com.growfund.seedtowealth.repository.FarmRepository(getApplication());
        eventRepository = new com.growfund.seedtowealth.repository.EventRepository(getApplication());

        sessionManager = new com.growfund.seedtowealth.utils.SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            // ... login check
        }

        // ... (rest of onCreate)
        initViews();
        loadFarmData();

        // ...
    }

    private void initViews() {
        farmNameText = findViewById(R.id.farmNameText);
        landSizeText = findViewById(R.id.landSizeText);
        savingsText = findViewById(R.id.savingsText);
        emergencyFundText = findViewById(R.id.emergencyFundText);
        cropsRecyclerView = findViewById(R.id.cropsRecyclerView);
        eventsRecyclerView = findViewById(R.id.eventsRecyclerView);
        eventsSection = findViewById(R.id.eventsSection);
        emptyStateView = findViewById(R.id.emptyStateView);
        loadingProgress = findViewById(R.id.loadingProgress);
        plantCropFab = findViewById(R.id.plantCropFab);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        TextView weatherText = findViewById(R.id.weatherText);

        swipeRefreshLayout.setOnRefreshListener(this::loadFarmData);

        // Schedule Background Sync
        PeriodicWorkRequest syncRequest = new PeriodicWorkRequest.Builder(SyncWorker.class, 15, TimeUnit.MINUTES)
                .setConstraints(new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build())
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "FarmDataSync",
                ExistingPeriodicWorkPolicy.KEEP,
                syncRequest);

        findViewById(R.id.profileButton).setOnClickListener(v -> {
            Intent intent = new Intent(FarmActivity.this, ProfileActivity.class);
            if (currentFarm != null) {
                intent.putExtra("farmId", currentFarm.getId());
                intent.putExtra("farmName", currentFarm.getFarmName());
            }
            startActivity(intent);
        });

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

        // Equipment Shop button
        View equipmentButton = findViewById(R.id.investmentButton); // Reusing investment button for now
        if (equipmentButton != null) {
            equipmentButton.setOnClickListener(v -> {
                Intent intent = new Intent(FarmActivity.this, EquipmentShopActivity.class);
                startActivity(intent);
            });
        }

        // Setup RecyclerView for crops
        cropsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cropAdapter = new CropAdapter(crop -> {
            Intent intent = new Intent(this, CropDetailActivity.class);
            intent.putExtra("cropId", crop.getId());
            startActivity(intent);
        });
        cropsRecyclerView.setAdapter(cropAdapter);

        // Setup RecyclerView for events
        eventAdapter = new EventAdapter();
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        eventsRecyclerView.setAdapter(eventAdapter);

        // Load active events
        loadActiveEvents();

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
        if (!swipeRefreshLayout.isRefreshing()) {
            loadingProgress.setVisibility(View.VISIBLE);
        }

        farmRepository.getFarm(new com.growfund.seedtowealth.repository.FarmRepository.RepositoryCallback<Farm>() {
            @Override
            public void onLocalData(Farm data) {
                // Show local data immediately
                if (currentFarm == null) {
                    currentFarm = data;
                    runOnUiThread(() -> {
                        updateFarmUI();
                        loadCrops(); // Load crops for this farm from local DB/Network
                    });
                }
            }

            @Override
            public void onSuccess(Farm data) {
                loadingProgress.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                currentFarm = data;
                runOnUiThread(() -> {
                    updateFarmUI();
                    loadCrops();
                });
            }

            @Override
            public void onError(String message) {
                loadingProgress.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                if (currentFarm == null) {
                    // Only show error if we have no data at all
                    runOnUiThread(
                            () -> Toast.makeText(FarmActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show());
                } else {
                    Log.w(TAG, "Background sync failed: " + message);
                }
            }
        });
    }

    private void loadCrops() {
        if (currentFarm == null)
            return;

        farmRepository.getCrops(currentFarm.getId(),
                new com.growfund.seedtowealth.repository.FarmRepository.RepositoryCallback<List<Crop>>() {
                    @Override
                    public void onLocalData(List<Crop> data) {
                        runOnUiThread(() -> {
                            cropList = data;
                            cropAdapter.setCrops(cropList);
                            if (cropList.isEmpty()) {
                                cropsRecyclerView.setVisibility(View.GONE);
                                emptyStateView.setVisibility(View.VISIBLE);
                            } else {
                                cropsRecyclerView.setVisibility(View.VISIBLE);
                                emptyStateView.setVisibility(View.GONE);
                            }
                        });
                    }

                    @Override
                    public void onSuccess(List<Crop> data) {
                        runOnUiThread(() -> {
                            cropList = data;
                            cropAdapter.setCrops(cropList);
                            if (cropList.isEmpty()) {
                                cropsRecyclerView.setVisibility(View.GONE);
                                emptyStateView.setVisibility(View.VISIBLE);
                            } else {
                                cropsRecyclerView.setVisibility(View.VISIBLE);
                                emptyStateView.setVisibility(View.GONE);
                            }
                        });
                    }

                    @Override
                    public void onError(String message) {
                        Log.e(TAG, "Error loading crops: " + message);
                    }
                });
    }

    private void createFarm() {
        // Logic placeholder or use ApiClient if crucial
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
        if (currentFarm == null)
            return;
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

    private void loadActiveEvents() {
        eventRepository.fetchActiveEventsFromServer(
                new com.growfund.seedtowealth.repository.EventRepository.RepositoryCallback<List<RandomEvent>>() {
                    @Override
                    public void onSuccess(List<RandomEvent> result) {
                        if (result != null && !result.isEmpty()) {
                            eventAdapter.setEvents(result);
                            eventsSection.setVisibility(View.VISIBLE);
                        } else {
                            eventsSection.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Failed to load events: " + error);
                        // Try loading from local cache
                        eventRepository.getActiveEvents(
                                new com.growfund.seedtowealth.repository.EventRepository.RepositoryCallback<List<RandomEvent>>() {
                                    @Override
                                    public void onSuccess(List<RandomEvent> result) {
                                        if (result != null && !result.isEmpty()) {
                                            eventAdapter.setEvents(result);
                                            eventsSection.setVisibility(View.VISIBLE);
                                        } else {
                                            eventsSection.setVisibility(View.GONE);
                                        }
                                    }

                                    @Override
                                    public void onError(String error) {
                                        eventsSection.setVisibility(View.GONE);
                                    }
                                });
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sessionManager.isLoggedIn()) {
            // Refresh all farm data (including savings)
            loadFarmData();
        }
    }
}
