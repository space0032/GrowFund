package com.growfund.seedtowealth;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.growfund.seedtowealth.adapter.AchievementAdapter;
import com.growfund.seedtowealth.model.Achievement;
import com.growfund.seedtowealth.network.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AchievementsActivity extends AppCompatActivity {
    private static final String TAG = "AchievementsActivity";

    private RecyclerView achievementsRecyclerView;
    private ProgressBar loadingProgress;
    private AchievementAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        achievementsRecyclerView = findViewById(R.id.achievementsRecyclerView);
        loadingProgress = findViewById(R.id.loadingProgress);

        achievementsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AchievementAdapter();
        achievementsRecyclerView.setAdapter(adapter);

        loadAchievements();
    }

    private void loadAchievements() {
        loadingProgress.setVisibility(View.VISIBLE);
        ApiClient.getApiService().getAchievements().enqueue(new Callback<List<Achievement>>() {
            @Override
            public void onResponse(Call<List<Achievement>> call, Response<List<Achievement>> response) {
                loadingProgress.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<Achievement> achievements = response.body();
                    adapter.setAchievements(achievements);
                    if (achievements.isEmpty()) {
                        Toast.makeText(AchievementsActivity.this, "No achievements yet. Keep farming!",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(AchievementsActivity.this, "Failed to load achievements", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Achievement>> call, Throwable t) {
                loadingProgress.setVisibility(View.GONE);
                Toast.makeText(AchievementsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
