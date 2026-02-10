package com.growfund.seedtowealth;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.growfund.seedtowealth.adapter.EventAdapter;
import com.growfund.seedtowealth.model.RandomEvent;
import com.growfund.seedtowealth.repository.EventRepository;

import java.util.List;

public class EventHistoryActivity extends AppCompatActivity {

    private RecyclerView eventsRecyclerView;
    private EventAdapter eventAdapter;
    private ProgressBar loadingProgress;
    private TextView emptyStateText;
    private EventRepository eventRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_history);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Event History");
        }

        // Initialize views
        eventsRecyclerView = findViewById(R.id.eventsRecyclerView);
        loadingProgress = findViewById(R.id.loadingProgress);
        emptyStateText = findViewById(R.id.emptyStateText);

        // Initialize repository
        eventRepository = new EventRepository(getApplication());

        // Setup RecyclerView
        eventAdapter = new EventAdapter();
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventsRecyclerView.setAdapter(eventAdapter);

        // Load event history
        loadEventHistory();
    }

    private void loadEventHistory() {
        loadingProgress.setVisibility(View.VISIBLE);
        eventsRecyclerView.setVisibility(View.GONE);
        emptyStateText.setVisibility(View.GONE);

        eventRepository.getEventHistory(new EventRepository.RepositoryCallback<List<RandomEvent>>() {
            @Override
            public void onSuccess(List<RandomEvent> result) {
                loadingProgress.setVisibility(View.GONE);

                if (result != null && !result.isEmpty()) {
                    eventAdapter.setEvents(result);
                    eventsRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    emptyStateText.setVisibility(View.VISIBLE);
                    emptyStateText.setText("No event history available");
                }
            }

            @Override
            public void onError(String error) {
                loadingProgress.setVisibility(View.GONE);
                emptyStateText.setVisibility(View.VISIBLE);

                // Differentiate between network errors and other errors
                if (error.contains("Network") || error.contains("network") ||
                        error.contains("connection") || error.contains("timeout")) {
                    // Transient network error - offer retry
                    emptyStateText.setText("Network error. Tap to retry.");
                    emptyStateText.setClickable(true);
                    emptyStateText.setOnClickListener(v -> loadEventHistory());
                } else {
                    // Permanent error
                    emptyStateText.setText("Failed to load event history");
                    emptyStateText.setClickable(false);
                    Toast.makeText(EventHistoryActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
