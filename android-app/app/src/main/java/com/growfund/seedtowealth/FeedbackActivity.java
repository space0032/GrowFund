package com.growfund.seedtowealth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class FeedbackActivity extends AppCompatActivity {

    private EditText etFeedback;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Feedback");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

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

        // Send via Firestore
        java.util.Map<String, Object> feedbackData = new java.util.HashMap<>();
        feedbackData.put("content", feedback);
        feedbackData.put("appVersion", "1.0.0");
        feedbackData.put("deviceModel", android.os.Build.MODEL);
        feedbackData.put("timestamp", com.google.firebase.Timestamp.now());

        // Add User ID if available
        com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance()
                .getCurrentUser();
        if (user != null) {
            feedbackData.put("userId", user.getUid());
        }

        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore
                .getInstance();
        db.collection("feedback")
                .add(feedbackData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(FeedbackActivity.this, "Feedback sent successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(FeedbackActivity.this, "Failed to send feedback: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
