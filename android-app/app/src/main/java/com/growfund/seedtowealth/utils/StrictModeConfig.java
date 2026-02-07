package com.growfund.seedtowealth.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.StrictMode;

/**
 * StrictMode configuration for detecting performance issues during development.
 * Helps identify:
 * - Disk reads/writes on main thread
 * - Network operations on main thread
 * - Memory leaks
 * - Slow code execution
 */
public class StrictModeConfig {

    /**
     * Enable StrictMode for debug builds only.
     * Call this from Application.onCreate() or MainActivity.onCreate()
     */
    public static void enableStrictMode(Context context) {
        // Check if app is debuggable
        boolean isDebuggable = (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;

        if (isDebuggable) {
            // Thread policy - detects disk/network operations on main thread
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .detectCustomSlowCalls()
                    .penaltyLog()
                    .penaltyFlashScreen() // Visual indicator
                    .build());

            // VM policy - detects memory leaks and resource issues
            StrictMode.VmPolicy.Builder vmPolicyBuilder = new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .detectActivityLeaks()
                    .penaltyLog();

            // Add additional checks for newer Android versions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vmPolicyBuilder.detectContentUriWithoutPermission();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                vmPolicyBuilder.detectNonSdkApiUsage();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vmPolicyBuilder.detectCredentialProtectedWhileLocked();
            }

            StrictMode.setVmPolicy(vmPolicyBuilder.build());
        }
    }

    /**
     * Disable StrictMode (useful for specific operations that trigger false
     * positives)
     */
    public static void disableStrictMode() {
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.LAX);
        StrictMode.setVmPolicy(StrictMode.VmPolicy.LAX);
    }
}
