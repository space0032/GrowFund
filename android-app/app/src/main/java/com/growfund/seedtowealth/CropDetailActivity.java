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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_detail);

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

        ApiClient.getApiService().getCrop(cropId).enqueue(new Callback<Crop>() {
            @Override
            public void onResponse(Call<Crop> call, Response<Crop> response) {
                loadingProgress.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    currentCrop = response.body();
                    updateUI();
                } else {
                    Toast.makeText(CropDetailActivity.this,
                            "Failed to load crop: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Crop> call, Throwable t) {
                loadingProgress.setVisibility(View.GONE);
                com.growfund.seedtowealth.utils.ErrorHandler.handleError(CropDetailActivity.this, t);
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
            harvestButton.setVisibility(View.VISIBLE);
            harvestResultsCard.setVisibility(View.GONE);
        } else {
            harvestButton.setVisibility(View.GONE);
        }
    }

    private void harvestCrop() {
        loadingProgress.setVisibility(View.VISIBLE);
        harvestButton.setEnabled(false);

        ApiClient.getApiService().harvestCrop(cropId).enqueue(new Callback<Crop>() {
            @Override
            public void onResponse(Call<Crop> call, Response<Crop> response) {
                loadingProgress.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    currentCrop = response.body();
                    updateUI();
                    Toast.makeText(CropDetailActivity.this, "Harvest Successful!", Toast.LENGTH_LONG).show();
                } else {
                    harvestButton.setEnabled(true);
                    Toast.makeText(CropDetailActivity.this, "Harvest Failed: " + response.code(), Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<Crop> call, Throwable t) {
                loadingProgress.setVisibility(View.GONE);
                harvestButton.setEnabled(true);
                Toast.makeText(CropDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
