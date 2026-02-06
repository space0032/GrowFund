package com.growfund.seedtowealth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "LoginActivity";

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private Button btnGoogleSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        btnGoogleSignIn = findViewById(R.id.btn_google_sign_in);

        // Configure Google Sign In
        // WARNING: requestIdToken needs a Web Client ID from Firebase Console
        // For now using default_web_client_id which is usually auto-generated in
        // strings.xml by google-services
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btnGoogleSignIn.setOnClickListener(v -> signIn());

        // Guest Login (Testing)
        Button btnGuest = findViewById(R.id.btn_guest);
        btnGuest.setOnClickListener(v -> {
            Toast.makeText(this, "Guest Mode: Backend features will be limited.", Toast.LENGTH_LONG).show();
            // Navigate to InvestmentActivity (or Home)
            Intent intent = new Intent(LoginActivity.this, InvestmentActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(this, "Google Sign In Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        showLoading(true);
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Log.d(TAG, "Firebase Auth Success: " + user.getUid());

                        // Sync with Backend
                        user.getIdToken(false).addOnSuccessListener(result -> {
                            String token = result.getToken();
                            syncUserWithBackend(token);
                        });
                    } else {
                        Log.w(TAG, "Firebase Auth Failed", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                        showLoading(false);
                    }
                });
    }

    private void syncUserWithBackend(String token) {
        com.growfund.seedtowealth.network.ApiService apiService = com.growfund.seedtowealth.network.NetworkClient
                .getRetrofitClient().create(com.growfund.seedtowealth.network.ApiService.class);

        retrofit2.Call<com.growfund.seedtowealth.model.User> call = apiService.syncUser("Bearer " + token);
        call.enqueue(new retrofit2.Callback<com.growfund.seedtowealth.model.User>() {
            @Override
            public void onResponse(retrofit2.Call<com.growfund.seedtowealth.model.User> call,
                    retrofit2.Response<com.growfund.seedtowealth.model.User> response) {
                showLoading(false);
                if (response.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Synced with Server! Coins: " + response.body().getCoins(),
                            Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LoginActivity.this, InvestmentActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Server Sync Failed: " + response.code(), Toast.LENGTH_LONG)
                            .show();
                    Log.e(TAG, "Sync Failed: " + response.message());
                    // Allow to proceed anyway for now? Or block?
                    // Let's block to ensure data consistency, or maybe allow guest-like access
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.growfund.seedtowealth.model.User> call, Throwable t) {
                showLoading(false);
                Toast.makeText(LoginActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Network Error", t);
            }
        });
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnGoogleSignIn.setEnabled(!isLoading);
    }
}
