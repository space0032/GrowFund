package com.growfund.seedtowealth;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Onboarding Activity - Handles user onboarding flow
 */
public class OnboardingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        findViewById(R.id.btn_continue).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, LoginActivity.class);
            startActivity(intent);
        });
    }
}
