package com.growfund.seedtowealth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.growfund.seedtowealth.model.User;
import com.growfund.seedtowealth.utils.SessionManager;
import com.growfund.seedtowealth.utils.SoundManager;
import com.growfund.seedtowealth.utils.LanguageManager;

import android.widget.EditText;
import android.app.AlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.growfund.seedtowealth.model.Farm;
import com.growfund.seedtowealth.network.ApiClient;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(android.content.Context newBase) {
        super.attachBaseContext(LanguageManager.applyLanguage(newBase));
    }

    private ImageView profileImage;
    private TextView userNameText, userEmailText, currentFarmNameText;
    private SwitchMaterial soundSwitch, vibrationSwitch;
    private Button logoutButton;
    private TextView languageText;
    private SessionManager sessionManager;
    private Long farmId;
    private String farmName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Get Farm Data from Intent
        if (getIntent().hasExtra("farmId")) {
            farmId = getIntent().getLongExtra("farmId", -1);
            farmName = getIntent().getStringExtra("farmName");
        }

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        sessionManager = new SessionManager(this);

        initViews();
        loadUserData();
        setupSettings();
        setupLanguageSelector();
        setupFarmSettings();
        setupLogout();
    }

    private void initViews() {
        profileImage = findViewById(R.id.profileImage);
        userNameText = findViewById(R.id.userNameText);
        userEmailText = findViewById(R.id.userEmailText);
        currentFarmNameText = findViewById(R.id.currentFarmNameText);
        soundSwitch = findViewById(R.id.soundSwitch);
        vibrationSwitch = findViewById(R.id.vibrationSwitch);
        logoutButton = findViewById(R.id.logoutButton);
        languageText = findViewById(R.id.languageText);
    }

    private void setupLanguageSelector() {
        // Display current language
        String currentLang = LanguageManager.getLanguage(this);
        languageText.setText(LanguageManager.getLanguageName(currentLang));

        // Language selector click listener
        findViewById(R.id.languageContainer).setOnClickListener(v -> showLanguageDialog());
    }

    private void showLanguageDialog() {
        String[] languages = LanguageManager.getSupportedLanguageNames();
        String[] languageCodes = LanguageManager.getSupportedLanguages();
        String currentLang = LanguageManager.getLanguage(this);

        // Find current language index
        int currentIndex = 0;
        for (int i = 0; i < languageCodes.length; i++) {
            if (languageCodes[i].equals(currentLang)) {
                currentIndex = i;
                break;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.select_language));
        builder.setSingleChoiceItems(languages, currentIndex, (dialog, which) -> {
            String selectedLang = languageCodes[which];
            if (!selectedLang.equals(currentLang)) {
                LanguageManager.setLanguage(this, selectedLang);

                // Recreate activity to apply language
                dialog.dismiss();
                recreate();

                Toast.makeText(this, getString(R.string.success), Toast.LENGTH_SHORT).show();
            } else {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void setupFarmSettings() {
        if (farmId != -1 && farmName != null) {
            currentFarmNameText.setText(farmName);
            findViewById(R.id.editFarmContainer).setOnClickListener(v -> showEditFarmDialog());
        } else {
            currentFarmNameText.setText("No Farm Details");
            findViewById(R.id.editFarmContainer).setEnabled(false);
        }
    }

    private void showEditFarmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rename Farm");

        final EditText input = new EditText(this);
        input.setHint("Enter new farm name");
        input.setText(farmName);
        input.setSelection(farmName.length());

        // Add padding
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        input.setPadding(padding, padding, padding, padding);

        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty()) {
                updateFarmName(newName);
            } else {
                Toast.makeText(this, "Farm name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void updateFarmName(String newName) {
        java.util.Map<String, String> request = new java.util.HashMap<>();
        request.put("farmName", newName);

        ApiClient.getApiService().updateFarmName(farmId, request).enqueue(new Callback<Farm>() {
            @Override
            public void onResponse(Call<Farm> call, Response<Farm> response) {
                if (response.isSuccessful()) {
                    farmName = newName;
                    currentFarmNameText.setText(newName);
                    Toast.makeText(ProfileActivity.this, "Farm Renamed Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to rename farm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Farm> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserData() {
        User user = sessionManager.getUserDetails();
        if (user != null) {
            userNameText.setText(user.getName() != null ? user.getName() : "GrowFund Farmer");
            userEmailText.setText(user.getEmail() != null ? user.getEmail() : "No Email");

            if (user.getPhotoUrl() != null && !user.getPhotoUrl().isEmpty()) {
                Glide.with(this)
                        .load(user.getPhotoUrl())
                        .placeholder(R.drawable.bg_circle_placeholder)
                        .circleCrop()
                        .into(profileImage);
            }
        }
    }

    private void setupSettings() {
        // Load saved preferences
        soundSwitch.setChecked(SoundManager.isSoundEnabled(this));
        vibrationSwitch.setChecked(SoundManager.isVibrationEnabled(this));

        // Listeners
        soundSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SoundManager.setSoundEnabled(this, isChecked);
            if (isChecked) {
                SoundManager.playSuccessSound(this);
            }
        });

        vibrationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SoundManager.setVibrationEnabled(this, isChecked);
            if (isChecked) {
                SoundManager.playSuccessSound(this); // This will trigger vibration if checked
            }
        });
    }

    private void setupLogout() {
        logoutButton.setOnClickListener(v -> {
            sessionManager.logoutUser();

            // Redirect to Login
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

            Toast.makeText(this, "Logged Out Successfully", Toast.LENGTH_SHORT).show();
        });
    }
}
