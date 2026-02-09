package com.growfund.seedtowealth;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.Slider;
import com.growfund.seedtowealth.model.Crop;
import com.growfund.seedtowealth.network.ApiClient;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlantCropActivity extends AppCompatActivity {
    private static final String TAG = "PlantCropActivity";

    private ChipGroup cropTypeChipGroup;
    private EditText areaInput;
    private Slider areaSlider;
    private TextView expectedYieldText;
    private TextView estimatedCostText;
    private Button plantButton;
    private ProgressBar loadingProgress;

    private Long farmId;

    private long currentSavings = 0;
    private TextView currentSavingsText;
    private TextView availableLandText;
    private com.growfund.seedtowealth.repository.FarmRepository farmRepository;

    private String selectedCropType = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_crop);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        farmRepository = new com.growfund.seedtowealth.repository.FarmRepository(getApplication());

        farmId = getIntent().getLongExtra("farmId", -1);
        if (farmId == -1) {
            Toast.makeText(this, "Error: Farm ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        fetchFarmSavings();
    }

    private void initViews() {
        cropTypeChipGroup = findViewById(R.id.cropTypeChipGroup);
        areaInput = findViewById(R.id.areaInput);
        areaSlider = findViewById(R.id.areaSlider);

        expectedYieldText = findViewById(R.id.expectedYieldText);
        estimatedCostText = findViewById(R.id.estimatedCostText);
        plantButton = findViewById(R.id.plantButton);
        loadingProgress = findViewById(R.id.loadingProgress);
        currentSavingsText = findViewById(R.id.currentSavingsText);
        availableLandText = findViewById(R.id.availableLandText);

        plantButton.setOnClickListener(v -> plantCrop());

        // Setup Text/Slider synchronization
        areaInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
                try {
                    String val = s.toString();
                    if (!val.isEmpty()) {
                        float area = Float.parseFloat(val);
                        // Update slider only if within range
                        if (area >= areaSlider.getValueFrom() && area <= areaSlider.getValueTo()) {
                            areaSlider.setValue(area);
                        }
                        updateCostEstimate();
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid text inputs during typing
                }
            }
        });

        areaSlider.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                // Round to 1 decimal place
                float roundedValue = (float) (Math.round(value * 2) / 2.0);
                areaInput.setText(String.valueOf(roundedValue));
                // updateCostEstimate handled by TextWatcher
            }
        });

        cropTypeChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != View.NO_ID) {
                Chip chip = group.findViewById(checkedId);
                // Tag contains the raw crop code e.g., "WHEAT"
                selectedCropType = (String) chip.getTag();
                updateCostEstimate();
            } else {
                selectedCropType = null;
                estimatedCostText.setText("Select a Crop");
            }
        });

        loadWeatherAndTrends();
    }

    private void updateCostEstimate() {
        if (selectedCropType == null) {
            return;
        }

        String areaStr = areaInput.getText().toString();
        if (areaStr.isEmpty()) {
            return;
        }

        try {
            double area = Double.parseDouble(areaStr);
            fetchCostForArea(area);
        } catch (NumberFormatException e) {
            estimatedCostText.setText("Invalid Area");
        }
    }

    private void fetchCostForArea(double area) {
        if (selectedCropType == null)
            return;

        ApiClient.getApiService().getPlantingCostEstimate(farmId, selectedCropType, area).enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Long cost = response.body();

                    estimatedCostText.setText(com.growfund.seedtowealth.utils.MoneyUtils.formatCurrency(cost));

                    // Visual feedback
                    int colorRes = (cost > currentSavings) ? R.color.error : R.color.text_primary;
                    estimatedCostText.setTextColor(getResources().getColor(colorRes, null));

                    plantButton.setEnabled(cost <= currentSavings);
                    if (cost > currentSavings) {
                        plantButton.setText("Insufficient Funds");
                    } else {
                        plantButton.setText("Confirm Planting");
                    }

                } else {
                    Log.e(TAG, "Estimate Failed");
                    estimatedCostText.setText("Error");
                }
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                estimatedCostText.setText("Net Error");
            }
        });
    }

    private void fetchFarmSavings() {
        farmRepository.getFarm(
                new com.growfund.seedtowealth.repository.FarmRepository.RepositoryCallback<com.growfund.seedtowealth.model.Farm>() {
                    @Override
                    public void onLocalData(com.growfund.seedtowealth.model.Farm data) {
                        runOnUiThread(() -> updateSavingsUI(data));
                    }

                    @Override
                    public void onSuccess(com.growfund.seedtowealth.model.Farm data) {
                        runOnUiThread(() -> updateSavingsUI(data));
                    }

                    @Override
                    public void onError(String message) {
                        runOnUiThread(() -> {
                            currentSavingsText.setText("Wallet: Error");
                        });
                    }
                });
    }

    private void updateSavingsUI(com.growfund.seedtowealth.model.Farm data) {
        currentSavings = data.getSavings();
        currentSavingsText.setText("Wallet: "
                + com.growfund.seedtowealth.utils.MoneyUtils.formatCurrency(currentSavings));

        // Get available land from backend
        Double availableLand = data.getAvailableLand();
        Double totalLand = data.getLandSize();

        if (availableLand != null && totalLand != null && totalLand > 0) {
            // Calculate percentage of available land
            double percentage = (availableLand / totalLand) * 100;

            // Display available land with color coding
            availableLandText.setText(String.format("Available: %.1f Acres", availableLand));

            // Color coding based on percentage
            if (percentage > 50) {
                // Green for plenty of land
                availableLandText.setTextColor(getResources().getColor(R.color.success, null));
            } else if (percentage < 20) {
                // Red for low land
                availableLandText.setTextColor(getResources().getColor(R.color.error, null));
            } else {
                // Amber/Warning for moderate land
                availableLandText.setTextColor(getResources().getColor(R.color.warning, null));
            }

            // Update Slider Max to Available Land (not total land)
            float maxPlantable = availableLand.floatValue();
            if (maxPlantable > 0) {
                areaSlider.setValueTo(maxPlantable);
                if (areaSlider.getValue() > maxPlantable) {
                    areaSlider.setValue(maxPlantable);
                }
            }
        } else {
            availableLandText.setText("Available: N/A");
            availableLandText.setTextColor(getResources().getColor(R.color.text_secondary, null));
        }
    }

    private void loadWeatherAndTrends() {
        // Load Trends
        setupCropChips();

        // Load Weather
        TextView weatherText = findViewById(R.id.weatherImpactText);
        ApiClient.getApiService().getCurrentWeather().enqueue(new Callback<java.util.Map<String, Object>>() {
            @Override
            public void onResponse(Call<java.util.Map<String, Object>> call,
                    Response<java.util.Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    java.util.Map<String, Object> body = response.body();
                    String condition = (String) body.get("condition");
                    String displayName = (String) body.get("displayName");

                    String message = "Weather: " + displayName;
                    int color = getResources().getColor(R.color.accent_dark, null);

                    if ("RAINY".equals(condition)) {
                        message += " (+20% Speed)";
                        color = getResources().getColor(R.color.info, null);
                    } else if ("DROUGHT".equals(condition)) {
                        message += " (-50% Speed)";
                        color = getResources().getColor(R.color.error, null);
                    }

                    weatherText.setText(message);
                    weatherText.setTextColor(color);
                }
            }

            @Override
            public void onFailure(Call<java.util.Map<String, Object>> call, Throwable t) {
                weatherText.setText("Weather data unavailable");
            }
        });
    }

    private void setupCropChips() {
        ApiClient.getApiService().getMarketTrends().enqueue(new Callback<Map<String, Double>>() {
            @Override
            public void onResponse(Call<Map<String, Double>> call, Response<Map<String, Double>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateChipsWithTrends(response.body());
                } else {
                    // Fallback defaults
                    updateChipsWithTrends(new HashMap<>());
                }
            }

            @Override
            public void onFailure(Call<Map<String, Double>> call, Throwable t) {
                updateChipsWithTrends(new HashMap<>());
            }
        });
    }

    private void updateChipsWithTrends(Map<String, Double> trends) {
        String[] cropTypes = new String[] { "WHEAT", "RICE", "COTTON", "SUGARCANE", "CORN" };

        cropTypeChipGroup.removeAllViews();

        for (String crop : cropTypes) {
            Chip chip = new Chip(this);
            chip.setCheckable(true);
            chip.setTag(crop); // Store raw code

            Double price = trends.get(crop);
            if (price != null) {
                chip.setText(crop + "\n" + com.growfund.seedtowealth.utils.MoneyUtils.formatCurrency(price));
            } else {
                chip.setText(crop);
            }

            // Style
            chip.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            cropTypeChipGroup.addView(chip);
        }

        // Select first by default
        if (cropTypeChipGroup.getChildCount() > 0) {
            ((Chip) cropTypeChipGroup.getChildAt(0)).setChecked(true);
        }
    }

    private void plantCrop() {
        if (selectedCropType == null) {
            Toast.makeText(this, "Please select a crop", Toast.LENGTH_SHORT).show();
            return;
        }

        String areaStr = areaInput.getText().toString();
        if (areaStr.isEmpty()) {
            Toast.makeText(this, "Please enter area", Toast.LENGTH_SHORT).show();
            return;
        }

        double area = Double.parseDouble(areaStr);

        Map<String, Object> request = new HashMap<>();
        request.put("cropType", selectedCropType);
        request.put("areaPlanted", area);
        request.put("season", "KHARIF"); // Dynamic season?

        loadingProgress.setVisibility(View.VISIBLE);
        plantButton.setEnabled(false);

        farmRepository.plantCrop(farmId, request,
                new com.growfund.seedtowealth.repository.FarmRepository.RepositoryCallback<Crop>() {
                    @Override
                    public void onLocalData(Crop data) {
                    }

                    @Override
                    public void onSuccess(Crop data) {
                        runOnUiThread(() -> {
                            loadingProgress.setVisibility(View.GONE);
                            plantButton.setEnabled(true);
                            scheduleNotification(data);

                            // Play Sound & Vibrate
                            com.growfund.seedtowealth.utils.SoundManager.playPlantSound(PlantCropActivity.this);

                            Toast.makeText(PlantCropActivity.this,
                                    "Crop planted successfully!",
                                    Toast.LENGTH_LONG).show();
                            finish();
                        });
                    }

                    @Override
                    public void onError(String message) {
                        runOnUiThread(() -> {
                            loadingProgress.setVisibility(View.GONE);
                            plantButton.setEnabled(true);
                            Toast.makeText(PlantCropActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
    }

    private void scheduleNotification(Crop crop) {
        long timeInMillis = crop.getTimeRemainingInMillis();

        if (timeInMillis > 0) {
            try {
                androidx.work.Data workData = new androidx.work.Data.Builder()
                        .putString(
                                com.growfund.seedtowealth.worker.HarvestReminderWorker.KEY_CROP_NAME,
                                crop.getCropType())
                        .build();

                androidx.work.OneTimeWorkRequest workRequest = new androidx.work.OneTimeWorkRequest.Builder(
                        com.growfund.seedtowealth.worker.HarvestReminderWorker.class)
                        .setInitialDelay(timeInMillis, java.util.concurrent.TimeUnit.MILLISECONDS)
                        .setInputData(workData)
                        .build();

                androidx.work.WorkManager.getInstance(PlantCropActivity.this)
                        .enqueue(workRequest);
            } catch (Exception e) {
                Log.e(TAG, "Error scheduling notification", e);
            }
        }
    }
}
