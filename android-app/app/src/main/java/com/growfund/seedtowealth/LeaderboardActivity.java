package com.growfund.seedtowealth;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.growfund.seedtowealth.adapter.LeaderboardAdapter;
import com.growfund.seedtowealth.model.LeaderboardEntry;
import com.growfund.seedtowealth.network.ApiClient;
import com.growfund.seedtowealth.utils.ErrorHandler;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeaderboardActivity extends AppCompatActivity {

    private RecyclerView leaderboardRecyclerView;
    private ProgressBar loadingProgress;
    private LeaderboardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        leaderboardRecyclerView = findViewById(R.id.leaderboardRecyclerView);
        loadingProgress = findViewById(R.id.loadingProgress);

        leaderboardRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LeaderboardAdapter();
        leaderboardRecyclerView.setAdapter(adapter);

        loadLeaderboard();
    }

    private void loadLeaderboard() {
        loadingProgress.setVisibility(View.VISIBLE);
        ApiClient.getApiService().getLeaderboard().enqueue(new Callback<List<LeaderboardEntry>>() {
            @Override
            public void onResponse(Call<List<LeaderboardEntry>> call, Response<List<LeaderboardEntry>> response) {
                loadingProgress.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setEntries(response.body());
                } else {
                    ErrorHandler.handleApiError(LeaderboardActivity.this, response);
                }
            }

            @Override
            public void onFailure(Call<List<LeaderboardEntry>> call, Throwable t) {
                loadingProgress.setVisibility(View.GONE);
                ErrorHandler.handleError(LeaderboardActivity.this, t);
            }
        });
    }
}
