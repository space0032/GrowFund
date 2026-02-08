package com.growfund.seedtowealth;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.growfund.seedtowealth.model.Crop;
import com.growfund.seedtowealth.network.ApiClient;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlantCropActivity extends AppCompatActivity {
    private static final String TAG = "PlantCropActivity";

    private Spinner cropTypeSpinner;
    private EditText areaInput, investmentInput;
    private TextView expectedYieldText;
    private Button plantButton;
    private ProgressBar loadingProgress;

    private Long farmId;

    private long currentSavings = 0;
    private TextView currentSavingsText;
    private com.growfund.seedtowealth.repository.FarmRepository farmRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_crop);

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
        cropTypeSpinner = findViewById(R.id.cropTypeSpinner);
        areaInput = findViewById(R.id.areaInput);
        investmentInput = findViewById(R.id.investmentInput);
        expectedYieldText = findViewById(R.id.expectedYieldText);
        plantButton = findViewById(R.id.plantButton);
        loadingProgress = findViewById(R.id.loadingProgress);
        currentSavingsText = findViewById(R.id.currentSavingsText);

        plantButton.setOnClickListener(v -> plantCrop());

        loadWeatherAndTrends();
    }

    private void fetchFarmSavings() {
        farmRepository.getFarm(
                new com.growfund.seedtowealth.repository.FarmRepository.RepositoryCallback<com.growfund.seedtowealth.model.Farm>() {
                    @Override
                    public void onLocalData(com.growfund.seedtowealth.model.Farm data) {
                        runOnUiThread(() -> {
                            currentSavings = data.getSavings();
                            currentSavingsText.setText("Current Savings: "
                                    + com.growfund.seedtowealth.utils.MoneyUtils.formatCurrency(currentSavings));
                        });
                    }

                    @Override
                    public void onSuccess(com.growfund.seedtowealth.model.Farm data) {
                        runOnUiThread(() -> {
                            currentSavings = data.getSavings();
                            currentSavingsText.setText("Current Savings: "
                                    + com.growfund.seedtowealth.utils.MoneyUtils.formatCurrency(currentSavings));
                        });
                    }

                    @Override
                    public void onError(String message) {
                        runOnUiThread(() -> {
                            currentSavingsText.setText("Current Savings: Error");
                            Log.e(TAG, "Failed to load savings: " + message);
                        });
                    }
                });
    }

    private void loadWeatherAndTrends() {
        // Load Trends
        setupCropTypeSpinner();

        // Load Weather
        TextView weatherText = findViewById(R.id.weatherImpactText);
        ApiClient.getApiService().getCurrentWeather().enqueue(new Callback<java.util.Map<String, Object>>() {
            @Override
            public void onResponse(Call<java.util.Map<String, Object>> call,
                    Response<java.util.Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    java.util.Map<String, Object> body = response.body();
                    String condition = (String) body.get("condition");
                    Double multiplier = (Double) body.get("growthMultiplier");

                    String message = "Current Weather: " + body.get("displayName");
                    if ("RAINY".equals(condition)) {
                        message += "\n(Growth Speed: +20% Faster!)";
                        weatherText.setTextColor(android.graphics.Color.BLUE);
                    } else if ("DROUGHT".equals(condition)) {
                        message += "\n(Growth Speed: -50% Slower!)";
                        weatherText.setTextColor(android.graphics.Color.RED);
                    } else {
                        message += "\n(Standard Growth Rate)";
                        weatherText.setTextColor(android.graphics.Color.parseColor("#FFA000")); // Orange
                    }
                    weatherText.setText(message);
                }
            }

            @Override
            public void onFailure(Call<java.util.Map<String, Object>> call, Throwable t) {
                weatherText.setText("Weather data unavailable");
            }
        });
    }

    private void setupCropTypeSpinner() {
        // Fetch trends first
        ApiClient.getApiService().getMarketTrends().enqueue(new Callback<Map<String, Double>>() {
            @Override
            public void onResponse(Call<Map<String, Double>> call, Response<Map<String, Double>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Double> trends = response.body();
                    updateSpinnerWithTrends(trends);
                } else {
                    setupDefaultSpinner();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Double>> call, Throwable t) {
                setupDefaultSpinner();
            }
        });
    }

    private void setupDefaultSpinner() {
        String[] cropTypes = { "WHEAT", "RICE", "COTTON", "SUGARCANE", "CORN" };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, cropTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cropTypeSpinner.setAdapter(adapter);
    }

    private void updateSpinnerWithTrends(Map<String, Double> trends) {
        // Create formatted strings: "WHEAT (₹42.50)"
        String[] cropTypes = new String[] { "WHEAT", "RICE", "COTTON", "SUGARCANE", "CORN" };
        String[] displayItems = new String[cropTypes.length];

        for (int i = 0; i < cropTypes.length; i++) {
            String crop = cropTypes[i];
            Double price = trends.get(crop);
            if (price != null) {
                displayItems[i] = crop + " (" + com.growfund.seedtowealth.utils.MoneyUtils.formatCurrency(price) + ")";
            } else {
                displayItems[i] = crop;
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, displayItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cropTypeSpinner.setAdapter(adapter);
    }

    private void plantCrop() {
        String selectedItem = cropTypeSpinner.getSelectedItem().toString();
        // Extract "WHEAT" from "WHEAT (₹42.50)"
        String cropType = selectedItem.split(" ")[0];

        String areaStr = areaInput.getText().toString();
        String investmentStr = investmentInput.getText().toString();

        if (areaStr.isEmpty() || investmentStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double area = Double.parseDouble(areaStr);
        long investment = Long.parseLong(investmentStr);

        if (investment > currentSavings) {
            Toast.makeText(this,
                    "Insufficient Funds! You only have "
                            + com.growfund.seedtowealth.utils.MoneyUtils.formatCurrency(currentSavings),
                    Toast.LENGTH_LONG).show();
            return;
        }

        Map<String, Object> request = new HashMap<>();
        request.put("cropType", cropType);
        request.put("areaPlanted", area);
        request.put("investmentAmount", investment);
        request.put("season", "KHARIF");

        loadingProgress.setVisibility(View.VISIBLE);
        plantButton.setEnabled(false);

        farmRepository.plantCrop(farmId, request,
                new com.growfund.seedtowealth.repository.FarmRepository.RepositoryCallback<Crop>() {
                    @Override
                    public void onLocalData(Crop data) {
                        // Not used for plant operation
                    }

                    @Override
                    public void onSuccess(Crop data) {
                        runOnUiThread(() -> {
                            loadingProgress.setVisibility(View.GONE);
                            plantButton.setEnabled(true);

                            Crop crop = data;
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

                            // Play Sound & Vibrate
                            com.growfund.seedtowealth.utils.SoundManager.playPlantSound(PlantCropActivity.this);

                            Toast.makeText(PlantCropActivity.this,
                                    "Crop planted successfully! You will be notified when ready.",
                                    Toast.LENGTH_LONG).show();
                            finish(); // Return to FarmActivity
                        });
                    }

                    @Override
                    public void onError(String message) {
                        runOnUiThread(() -> {
                            loadingProgress.setVisibility(View.GONE);
                            plantButton.setEnabled(true);
                            Toast.makeText(PlantCropActivity.this, "Error: " + message, Toast.LENGTH_SHORT)
                                    .show();
                        });
                    }
                });
    }
}
