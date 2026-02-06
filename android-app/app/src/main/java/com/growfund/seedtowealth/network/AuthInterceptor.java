package com.growfund.seedtowealth.network;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request.Builder builder = originalRequest.newBuilder();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            try {
                // Determine if we should force refresh the token.
                // For critical operations or if we suspect it's expired, we might want true.
                // For general interception, false is usually fine as the SDK handles
                // caching/refreshing.
                // However, this is a synchronous interceptor, so we must block.
                GetTokenResult result = Tasks.await(user.getIdToken(false));
                String token = result.getToken();
                if (token != null) {
                    builder.addHeader("Authorization", "Bearer " + token);
                }
            } catch (ExecutionException | InterruptedException e) {
                // Log error or handle failure.
                // If token fetch fails, we might still want to proceed (backend will 401)
                // or throw an IOException to fail the request.
                // Let's proceed without token so backend returns 401 instead of app crashing or
                // hanging.
                e.printStackTrace();
            }
        }

        return chain.proceed(builder.build());
    }
}
