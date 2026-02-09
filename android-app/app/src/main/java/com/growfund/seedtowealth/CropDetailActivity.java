package com.growfund.seedtowealth;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.growfund.seedtowealth.model.Crop;
import com.growfund.seedtowealth.network.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CropDetailActivity extends AppCompatActivity {
    private static final String TAG = "CropDetailActivity";

    private TextView cropTypeTitle, statusText, areaText, seasonText;
    private TextView investmentText, expectedYieldText;
    private TextView actualYieldText, sellingPriceText, profitText;
    private CardView harvestResultsCard;
    private Button harvestButton;
    private ProgressBar loadingProgress;

    private Long cropId;
    private Crop currentCrop;
    private com.growfund.seedtowealth.repository.FarmRepository farmRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_detail);

        farmRepository = new com.growfund.seedtowealth.repository.FarmRepository(getApplication());

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        cropId = getIntent().getLongExtra("cropId", -1);
        if (cropId == -1) {
            Toast.makeText(this, "Error: Crop ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        loadCropData();
    }

    private void initViews() {
        cropTypeTitle = findViewById(R.id.cropTypeTitle);
        statusText = findViewById(R.id.statusText);
        areaText = findViewById(R.id.areaText);
        seasonText = findViewById(R.id.seasonText);
        investmentText = findViewById(R.id.investmentText);
        expectedYieldText = findViewById(R.id.expectedYieldText);

        harvestResultsCard = findViewById(R.id.harvestResultsCard);
        actualYieldText = findViewById(R.id.actualYieldText);
        sellingPriceText = findViewById(R.id.sellingPriceText);
        profitText = findViewById(R.id.profitText);

        harvestButton = findViewById(R.id.harvestButton);
        loadingProgress = findViewById(R.id.loadingProgress);

        harvestButton.setOnClickListener(v -> harvestCrop());
    }

    private void loadCropData() {
        loadingProgress.setVisibility(View.VISIBLE);

        farmRepository.getCrop(cropId,
                new com.growfund.seedtowealth.repository.FarmRepository.RepositoryCallback<Crop>() {
                    @Override
                    public void onLocalData(Crop data) {
                        runOnUiThread(() -> {
                            currentCrop = data;
                            updateUI();
                            loadingProgress.setVisibility(View.GONE);
                        });
                    }

                    @Override
                    public void onSuccess(Crop data) {
                        runOnUiThread(() -> {
                            currentCrop = data;
                            updateUI();
                            loadingProgress.setVisibility(View.GONE);
                        });
                    }

                    @Override
                    public void onError(String message) {
                        runOnUiThread(() -> {
                            loadingProgress.setVisibility(View.GONE);
                            Toast.makeText(CropDetailActivity.this, "Error: " + message, Toast.LENGTH_SHORT)
                                    .show();
                        });
                    }
                });
    }

    private void updateUI() {
        if (currentCrop == null)
            return;

        cropTypeTitle.setText(currentCrop.getCropType());
        statusText.setText("Status: " + currentCrop.getStatus());
        areaText.setText(currentCrop.getAreaPlanted() + " Acres");
        seasonText.setText(currentCrop.getSeason());
        investmentText.setText("₹" + currentCrop.getInvestmentAmount());
        expectedYieldText.setText(currentCrop.getExpectedYield() + " kg (est)");

        if ("HARVESTED".equals(currentCrop.getStatus())) {
            harvestButton.setVisibility(View.GONE);
            harvestResultsCard.setVisibility(View.VISIBLE);

            actualYieldText.setText(currentCrop.getActualYield() + " kg");

            if (currentCrop.getSellingPricePerUnit() != null) {
                sellingPriceText.setText("₹" + currentCrop.getSellingPricePerUnit() + "/kg");
            } else {
                sellingPriceText.setText("N/A");
            }

            if (currentCrop.getProfit() != null) {
                if (currentCrop.getProfit() >= 0) {
                    profitText.setText("+₹" + currentCrop.getProfit());
                    profitText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                } else {
                    profitText.setText("-₹" + Math.abs(currentCrop.getProfit()));
                    profitText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                }
            }
        } else if ("PLANTED".equals(currentCrop.getStatus()) || "GROWING".equals(currentCrop.getStatus())) {
            harvestResultsCard.setVisibility(View.GONE);

            long timeRemaining = currentCrop.getTimeRemainingInMillis();
            if (timeRemaining > 0) {
                harvestButton.setEnabled(false);
                startTimer(timeRemaining);
            } else {
                harvestButton.setEnabled(true);
                harvestButton.setText("Harvest Crop");
            }
            harvestButton.setVisibility(View.VISIBLE);
        } else {
            harvestButton.setVisibility(View.GONE);
        }
    }

    private android.os.CountDownTimer countDownTimer;

    private void startTimer(long millis) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new android.os.CountDownTimer(millis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                long minutes = seconds / 60;
                long remainingSeconds = seconds % 60;
                harvestButton.setText(String.format("Ready in: %02d:%02d", minutes, remainingSeconds));
            }

            @Override
            public void onFinish() {
                harvestButton.setEnabled(true);
                harvestButton.setText("Harvest Crop");
                currentCrop.setStatus("GROWING"); // Or READY?
                // Ideally refresh data
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void harvestCrop() {
        loadingProgress.setVisibility(View.VISIBLE);
        harvestButton.setEnabled(false);

        farmRepository.harvestCrop(cropId,
                new com.growfund.seedtowealth.repository.FarmRepository.RepositoryCallback<Crop>() {
                    @Override
                    public void onLocalData(Crop data) {
                        // Not used for harvest operation
                    }

                    @Override
                    public void onSuccess(Crop data) {
                        runOnUiThread(() -> {
                            loadingProgress.setVisibility(View.GONE);
                            currentCrop = data;
                            updateUI();

                            // Play Sound & Vibrate
                            com.growfund.seedtowealth.utils.SoundManager.playHarvestSound(CropDetailActivity.this);

                            Toast.makeText(CropDetailActivity.this, "Harvest Successful!", Toast.LENGTH_LONG)
                                    .show();
                        });
                    }

                    @Override
                    public void onError(String message) {
                        runOnUiThread(() -> {
                            loadingProgress.setVisibility(View.GONE);
                            harvestButton.setEnabled(true);
                            Toast.makeText(CropDetailActivity.this, "Error: " + message, Toast.LENGTH_SHORT)
                                    .show();
                        });
                    }
                });
    }
}
