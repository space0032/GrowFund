package com.growfund.seedtowealth.worker;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.growfund.seedtowealth.utils.NotificationHelper;

public class HarvestReminderWorker extends Worker {

    public static final String KEY_CROP_NAME = "crop_name";

    public HarvestReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String cropName = getInputData().getString(KEY_CROP_NAME);
        if (cropName == null) {
            cropName = "Crop";
        }

        // Show Notification
        NotificationHelper.showNotification(
                getApplicationContext(),
                "Harvest Ready!",
                "Your " + cropName + " is ready to harvest. Collect your earnings now!",
                (int) System.currentTimeMillis() // Unique ID
        );

        return Result.success();
    }
}
