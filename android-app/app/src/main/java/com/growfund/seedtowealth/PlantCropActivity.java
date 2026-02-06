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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_crop);

        farmId = getIntent().getLongExtra("farmId", -1);
        if (farmId == -1) {
            Toast.makeText(this, "Error: Farm ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupCropTypeSpinner();
    }

    private void initViews() {
        cropTypeSpinner = findViewById(R.id.cropTypeSpinner);
        areaInput = findViewById(R.id.areaInput);
        investmentInput = findViewById(R.id.investmentInput);
        expectedYieldText = findViewById(R.id.expectedYieldText);
        plantButton = findViewById(R.id.plantButton);
        loadingProgress = findViewById(R.id.loadingProgress);

        plantButton.setOnClickListener(v -> plantCrop());
    }

    private void setupCropTypeSpinner() {
        String[] cropTypes = { "WHEAT", "RICE", "COTTON", "SUGARCANE", "CORN" };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, cropTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cropTypeSpinner.setAdapter(adapter);
    }

    private void plantCrop() {
        String cropType = cropTypeSpinner.getSelectedItem().toString();
        String areaStr = areaInput.getText().toString();
        String investmentStr = investmentInput.getText().toString();

        if (areaStr.isEmpty() || investmentStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double area = Double.parseDouble(areaStr);
        long investment = Long.parseLong(investmentStr);

        Map<String, Object> request = new HashMap<>();
        request.put("cropType", cropType);
        request.put("areaPlanted", area);
        request.put("investmentAmount", investment);
        request.put("season", "KHARIF");

        loadingProgress.setVisibility(View.VISIBLE);
        plantButton.setEnabled(false);

        ApiClient.getApiService().plantCrop(farmId, request).enqueue(new Callback<Crop>() {
            @Override
            public void onResponse(Call<Crop> call, Response<Crop> response) {
                loadingProgress.setVisibility(View.GONE);
                plantButton.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(PlantCropActivity.this,
                            "Crop planted successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Return to FarmActivity
                } else {
                    Toast.makeText(PlantCropActivity.this,
                            "Failed to plant crop", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Crop> call, Throwable t) {
                loadingProgress.setVisibility(View.GONE);
                plantButton.setEnabled(true);
                Log.e(TAG, "Error planting crop", t);
                Toast.makeText(PlantCropActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
