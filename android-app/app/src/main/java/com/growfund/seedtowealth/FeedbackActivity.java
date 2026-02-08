package com.growfund.seedtowealth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FeedbackActivity extends AppCompatActivity {

    private EditText etFeedback;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Feedback");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        etFeedback = findViewById(R.id.et_feedback);
        btnSubmit = findViewById(R.id.btn_submit_feedback);

        btnSubmit.setOnClickListener(v -> submitFeedback());
    }

    private void submitFeedback() {
        String feedback = etFeedback.getText().toString().trim();
        if (feedback.isEmpty()) {
            etFeedback.setError("Please enter your feedback");
            return;
        }

        // Log to Firebase Analytics
        com.google.firebase.analytics.FirebaseAnalytics mFirebaseAnalytics = com.google.firebase.analytics.FirebaseAnalytics
                .getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putInt("feedback_length", feedback.length());
        mFirebaseAnalytics.logEvent("feedback_submitted", bundle);

        // Send via API
        java.util.Map<String, String> feedbackData = new java.util.HashMap<>();
        feedbackData.put("content", feedback);
        feedbackData.put("appVersion", "1.0.0"); // TODO: Get dynamically
        feedbackData.put("deviceModel", android.os.Build.MODEL);

        com.growfund.seedtowealth.network.ApiClient.getApiService()
                .submitFeedback(feedbackData)
                .enqueue(new retrofit2.Callback<Void>() {
                    @Override
                    public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(FeedbackActivity.this, "Feedback sent successfully!", Toast.LENGTH_SHORT)
                                    .show();
                            finish();
                        } else {
                            Toast.makeText(FeedbackActivity.this, "Failed to send feedback (" + response.code() + ")",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                        Toast.makeText(FeedbackActivity.this, "Network error. Please try again.", Toast.LENGTH_SHORT)
                                .show();
                        t.printStackTrace();
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
