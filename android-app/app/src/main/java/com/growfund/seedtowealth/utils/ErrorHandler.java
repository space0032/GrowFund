package com.growfund.seedtowealth.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.widget.Toast;

import retrofit2.Response;

public class ErrorHandler {

    public static void handleError(Context context, Throwable t) {
        if (!isNetworkAvailable(context)) {
            Toast.makeText(context, "No internet connection. Please check your network.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "An error occurred: " + t.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public static void handleApiError(Context context, Response<?> response) {
        int code = response.code();
        String message;

        switch (code) {
            case 401:
                message = "Session expired. Please login again.";
                // Ideally redirect to login
                break;
            case 403:
                message = "You don't have permission to perform this action.";
                break;
            case 404:
                message = "Resource not found.";
                break;
            case 500:
                message = "Server error. Please try again later.";
                break;
            default:
                message = "Request failed (" + code + ")";
        }

        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkCapabilities capabilities = connectivityManager
                    .getNetworkCapabilities(connectivityManager.getActiveNetwork());
            return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
        }
        return false;
    }
}
