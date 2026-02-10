package com.growfund.seedtowealth;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

/**
 * Main Activity - Entry point for the Seed to Wealth app
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check for existing session
        com.growfund.seedtowealth.utils.SessionManager sessionManager = new com.growfund.seedtowealth.utils.SessionManager(
                this);
        if (sessionManager.isLoggedIn()
                && com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser() != null) {
            // User is already logged in, redirect to FarmActivity
            Intent intent = new Intent(MainActivity.this, FarmActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        Button btnGetStarted = findViewById(R.id.btn_get_started);
        btnGetStarted.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, OnboardingActivity.class);
            startActivity(intent);
        });
    }
}
