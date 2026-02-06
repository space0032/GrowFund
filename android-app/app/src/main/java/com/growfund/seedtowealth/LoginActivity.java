package com.growfund.seedtowealth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

    private static final String TAG = "LoginActivity";

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private Button btnGoogleSignIn;
    private ActivityResultLauncher<Intent> mSignInLauncher;

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

        mSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent data = result.getData();
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        firebaseAuthWithGoogle(account.getIdToken());
                    } catch (ApiException e) {
                        Log.w(TAG, "Google sign in failed", e);
                        Toast.makeText(this, "Google Sign In Failed", Toast.LENGTH_SHORT).show();
                    }
                });

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
        mSignInLauncher.launch(signInIntent);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        showLoading(true);
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Log.d(TAG, "Firebase Auth Success: " + user.getUid());
                        syncUserWithBackend(user);
                    } else {
                        Log.w(TAG, "Firebase Auth Failed", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                        showLoading(false);
                    }
                });
    }

    private void syncUserWithBackend(FirebaseUser firebaseUser) {
        com.growfund.seedtowealth.model.User user = new com.growfund.seedtowealth.model.User(
                firebaseUser.getEmail(),
                firebaseUser.getDisplayName(),
                firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : null,
                firebaseUser.getUid());

        com.growfund.seedtowealth.network.ApiClient.getApiService().syncUser(user)
                .enqueue(new retrofit2.Callback<com.growfund.seedtowealth.model.User>() {
                    @Override
                    public void onResponse(retrofit2.Call<com.growfund.seedtowealth.model.User> call,
                            retrofit2.Response<com.growfund.seedtowealth.model.User> response) {
                        showLoading(false);
                        if (response.isSuccessful()) {
                            Log.d(TAG, "User Sync Success: " + response.body());
                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, InvestmentActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.e(TAG, "User Sync Failed: " + response.code());
                            Toast.makeText(LoginActivity.this, "Login Failed (Backend Sync)", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<com.growfund.seedtowealth.model.User> call, Throwable t) {
                        showLoading(false);
                        Log.e(TAG, "User Sync Error", t);
                        Toast.makeText(LoginActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnGoogleSignIn.setEnabled(!isLoading);
    }
}
