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

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class FarmActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
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
    private ExtendedFloatingActionButton plantCropFab;

    private com.growfund.seedtowealth.repository.FarmRepository farmRepository;
    private com.growfund.seedtowealth.repository.EventRepository eventRepository;
    private com.growfund.seedtowealth.utils.SessionManager sessionManager;
    private androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefreshLayout;
    private android.view.View emptyStateView;
    private Farm currentFarm;
    private List<Crop> cropList = new ArrayList<>();
    private CropAdapter cropAdapter;
    private EventAdapter eventAdapter;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm);

        // Enable StrictMode for development builds
        com.growfund.seedtowealth.utils.StrictModeConfig.enableStrictMode(this);

        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Initialize Repositories
        farmRepository = new com.growfund.seedtowealth.repository.FarmRepository(getApplication());
        eventRepository = new com.growfund.seedtowealth.repository.EventRepository(getApplication());

        sessionManager = new com.growfund.seedtowealth.utils.SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            // ... login check
        }

        // Update Nav Header with User Info
        updateNavHeader();

        // Use OnBackPressedDispatcher for back press handling
        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });

        initViews();
        loadFarmData();
        checkForAppUpdate();
    }

    private void updateNavHeader() {
        View headerView = navigationView.getHeaderView(0);
        TextView navName = headerView.findViewById(R.id.navHeaderName);
        TextView navEmail = headerView.findViewById(R.id.navHeaderEmail);

        // Try to get user info from FirebaseAuth or SessionManager
        com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance()
                .getCurrentUser();
        if (user != null) {
            navName.setText(user.getDisplayName() != null ? user.getDisplayName() : "Farmer");
            navEmail.setText(user.getEmail());
            // You could load profile image here using Glide/Picasso if you had it
        } else {
            navName.setText("Guest Farmer");
            navEmail.setText("");
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull android.view.MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Already on Home
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(FarmActivity.this, ProfileActivity.class);
            if (currentFarm != null) {
                intent.putExtra("farmId", currentFarm.getId());
                intent.putExtra("farmName", currentFarm.getFarmName());
            }
            startActivity(intent);
        } else if (id == R.id.nav_leaderboard) {
            startActivity(new Intent(FarmActivity.this, LeaderboardActivity.class));
        } else if (id == R.id.nav_achievements) {
            startActivity(new Intent(FarmActivity.this, AchievementsActivity.class));
        } else if (id == R.id.nav_investment) {
            startActivity(new Intent(FarmActivity.this, InvestmentActivity.class));
        } else if (id == R.id.nav_equipment_shop) {
            startActivity(new Intent(FarmActivity.this, EquipmentShopActivity.class));
        } else if (id == R.id.nav_analytics) {
            Intent intent = new Intent(FarmActivity.this, AnalyticsActivity.class);
            if (currentFarm != null) {
                intent.putExtra("farmId", currentFarm.getId());
            }
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out Seed to Wealth - The best farming simulation game!");
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        } else if (id == R.id.nav_send) {
            startActivity(new Intent(FarmActivity.this, FeedbackActivity.class));
        } else if (id == R.id.nav_language) {
            // Language settings
            Toast.makeText(this, "Language settings coming soon!", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_logout) {
            sessionManager.logoutUser();
            Intent intent = new Intent(FarmActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
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

        // Equipment Shop is accessible via Navigation Drawer

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

        findViewById(R.id.sortButton).setOnClickListener(v -> showSortPopup(v));
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
                            // Clear existing data first
                            cropList.clear();
                            cropAdapter.setCrops(cropList);

                            // Update with new data
                            if (data != null && !data.isEmpty()) {
                                cropList = data;
                                cropAdapter.setCrops(cropList);
                                cropsRecyclerView.setVisibility(View.VISIBLE);
                                emptyStateView.setVisibility(View.GONE);
                            } else {
                                cropsRecyclerView.setVisibility(View.GONE);
                                emptyStateView.setVisibility(View.VISIBLE);
                            }
                            cropAdapter.notifyDataSetChanged();
                        });
                    }

                    @Override
                    public void onSuccess(List<Crop> data) {
                        runOnUiThread(() -> {
                            // Clear existing data first
                            cropList.clear();
                            cropAdapter.setCrops(cropList);

                            // Update with new data
                            if (data != null && !data.isEmpty()) {
                                cropList = data;
                                cropAdapter.setCrops(cropList);
                                cropsRecyclerView.setVisibility(View.VISIBLE);
                                emptyStateView.setVisibility(View.GONE);
                            } else {
                                cropsRecyclerView.setVisibility(View.GONE);
                                emptyStateView.setVisibility(View.VISIBLE);
                            }
                            cropAdapter.notifyDataSetChanged();
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
        if (currentFarm == null || currentFarm.getExpansionCost() == null) {
            Toast.makeText(this, "Unable to calculate expansion cost", Toast.LENGTH_SHORT).show();
            return;
        }

        String costFormatted = com.growfund.seedtowealth.utils.MoneyUtils
                .formatCurrency(currentFarm.getExpansionCost());
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Expand Farm")
                .setMessage("Expand your farm by 1 acre for " + costFormatted + "?")
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
                    // Enhanced error handling with specific messages
                    int statusCode = response.code();
                    String customMessage = null;

                    if (statusCode == 400) {
                        customMessage = "Insufficient funds to expand farm!";
                    } else if (statusCode == 409) {
                        customMessage = "Farm already at maximum size!";
                    } else {
                        customMessage = "Farm expansion failed!";
                    }

                    com.growfund.seedtowealth.utils.ErrorHandler.handleApiError(
                            FarmActivity.this, statusCode, customMessage);
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
        savingsText.setText(com.growfund.seedtowealth.utils.MoneyUtils.formatCurrency(currentFarm.getSavings()));
        emergencyFundText
                .setText(com.growfund.seedtowealth.utils.MoneyUtils.formatCurrency(currentFarm.getEmergencyFund()));

        // Check if user has enough savings for expansion
        Long expansionCost = currentFarm.getExpansionCost();
        if (expansionCost != null && currentFarm.getSavings() >= expansionCost) {
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

    private void showSortPopup(View v) {
        androidx.appcompat.widget.PopupMenu popup = new androidx.appcompat.widget.PopupMenu(this, v);
        popup.getMenuInflater().inflate(R.menu.menu_farm, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_sort_name) {
                sortCrops(java.util.Comparator.comparing(Crop::getCropType));
                return true;
            } else if (id == R.id.action_sort_status) {
                sortCrops((c1, c2) -> {
                    int status1 = getStatusPriority(c1.getStatus());
                    int status2 = getStatusPriority(c2.getStatus());
                    return Integer.compare(status1, status2);
                });
                return true;
            } else if (id == R.id.action_sort_date) {
                sortCrops((c1, c2) -> {
                    if (c1.getPlantedDate() == null && c2.getPlantedDate() == null)
                        return 0;
                    if (c1.getPlantedDate() == null)
                        return 1;
                    if (c2.getPlantedDate() == null)
                        return -1;
                    return c2.getPlantedDate().compareTo(c1.getPlantedDate());
                });
                return true;
            }
            return false;
        });
        popup.show();
    }

    private int getStatusPriority(String status) {
        if ("READY".equals(status))
            return 1;
        if ("GROWING".equals(status))
            return 2;
        return 3;
    }

    private void sortCrops(java.util.Comparator<Crop> comparator) {
        if (cropList != null) {
            java.util.Collections.sort(cropList, comparator);
            cropAdapter.setCrops(cropList);
            // Scroll to top after sorting
            cropsRecyclerView.scrollToPosition(0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sessionManager.isLoggedIn()) {
            // Refresh all farm data (including savings)
            loadFarmData();
        }
    }

    private void checkForAppUpdate() {
        com.google.android.play.core.appupdate.AppUpdateManager appUpdateManager = com.google.android.play.core.appupdate.AppUpdateManagerFactory
                .create(this);

        int appUpdateType = com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;

        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo
                    .updateAvailability() == com.google.android.play.core.install.model.UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(appUpdateType)) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            appUpdateType,
                            this,
                            100);
                } catch (android.content.IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode != RESULT_OK) {
                Log.w(TAG, "Update flow failed! Result code: " + resultCode);
            }
        }
    }
}
