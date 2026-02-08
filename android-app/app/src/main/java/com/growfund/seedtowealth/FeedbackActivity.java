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

        // Send via Email Intent
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "growfund.feedback@example.com" });
        intent.putExtra(Intent.EXTRA_SUBJECT, "GrowFund Feedback");
        intent.putExtra(Intent.EXTRA_TEXT, feedback);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show();
            // Fallback: Could log to analytics or API
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
