package com.growfund.seedtowealth;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.growfund.seedtowealth.adapter.RecommendationAdapter;
import com.growfund.seedtowealth.model.AnalyticsData;
import com.growfund.seedtowealth.model.Farm;
import com.growfund.seedtowealth.model.Recommendation;
import com.growfund.seedtowealth.network.ApiClient;
import com.growfund.seedtowealth.repository.FarmRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnalyticsActivity extends AppCompatActivity {

    private PieChart portfolioPieChart;
    private TextView totalValueText;
    private RecyclerView recommendationsRecyclerView;
    private ProgressBar loadingProgress;
    private RecommendationAdapter recommendationAdapter;
    private FarmRepository farmRepository;
    private Long farmId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Financial Analytics");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        initViews();

        farmRepository = new FarmRepository(getApplication());

        // Get Farm ID from Intent or Repository
        // Since FarmActivity passes it usually, but if not we can try to fetch current
        // farm
        if (getIntent().hasExtra("farmId")) {
            farmId = getIntent().getLongExtra("farmId", -1);
            loadAnalytics();
        } else {
            fetchFarmIdAndLoad();
        }
    }

    private void initViews() {
        portfolioPieChart = findViewById(R.id.portfolioPieChart);
        totalValueText = findViewById(R.id.totalValueText);
        recommendationsRecyclerView = findViewById(R.id.recommendationsRecyclerView);
        loadingProgress = findViewById(R.id.loadingProgress);

        recommendationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        recommendationAdapter = new RecommendationAdapter();
        recommendationsRecyclerView.setAdapter(recommendationAdapter);
    }

    private void fetchFarmIdAndLoad() {
        loadingProgress.setVisibility(View.VISIBLE);
        farmRepository.getFarm(new FarmRepository.RepositoryCallback<Farm>() {
            @Override
            public void onLocalData(Farm data) {
                if (data != null) {
                    farmId = data.getId();
                    loadAnalytics();
                }
            }

            @Override
            public void onSuccess(Farm data) {
                if (data != null) {
                    farmId = data.getId();
                    loadAnalytics();
                }
            }

            @Override
            public void onError(String message) {
                loadingProgress.setVisibility(View.GONE);
                Toast.makeText(AnalyticsActivity.this, "Failed to load farm data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAnalytics() {
        if (farmId == null || farmId == -1)
            return;

        loadingProgress.setVisibility(View.VISIBLE);

        // Load Dashboard Data
        ApiClient.getApiService().getFarmAnalytics(farmId).enqueue(new Callback<AnalyticsData>() {
            @Override
            public void onResponse(Call<AnalyticsData> call, Response<AnalyticsData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    setupPieChart(response.body());
                } else {
                    Toast.makeText(AnalyticsActivity.this, "Failed to load analytics", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AnalyticsData> call, Throwable t) {
                Toast.makeText(AnalyticsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Load Recommendations
        ApiClient.getApiService().getRecommendations(farmId).enqueue(new Callback<List<Recommendation>>() {
            @Override
            public void onResponse(Call<List<Recommendation>> call, Response<List<Recommendation>> response) {
                loadingProgress.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    recommendationAdapter.setRecommendations(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Recommendation>> call, Throwable t) {
                loadingProgress.setVisibility(View.GONE);
            }
        });
    }

    private void setupPieChart(AnalyticsData data) {
        List<PieEntry> entries = new ArrayList<>();

        if (data.getSavings() > 0)
            entries.add(new PieEntry(data.getSavings(), "Savings"));
        if (data.getInvestmentValue() > 0)
            entries.add(new PieEntry(data.getInvestmentValue(), "Investments"));
        if (data.getCropValue() > 0)
            entries.add(new PieEntry(data.getCropValue(), "Crops"));
        if (data.getEquipmentValue() > 0)
            entries.add(new PieEntry(data.getEquipmentValue(), "Equipment"));

        if (entries.isEmpty()) {
            entries.add(new PieEntry(1f, "No Assets"));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        PieData pieData = new PieData(dataSet);
        portfolioPieChart.setData(pieData);
        portfolioPieChart.getDescription().setEnabled(false);
        portfolioPieChart.setCenterText("Portfolio");
        portfolioPieChart.animateY(1000);
        portfolioPieChart.invalidate();

        totalValueText.setText("Total Value: "
                + com.growfund.seedtowealth.utils.MoneyUtils.formatCurrency(data.getTotalPortfolioValue()));
    }
}
