package com.growfund.seedtowealth.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

public class SoundManager {
    private static final String TAG = "SoundManager";
    private static final int VOLUME = 100;
    private static ToneGenerator toneGenerator;

    static {
        try {
            toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, VOLUME);
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize ToneGenerator", e);
        }
    }

    public static void playPlantSound(Context context) {
        playSound(ToneGenerator.TONE_PROP_BEEP);
        vibrate(context, 100);
    }

    public static void playHarvestSound(Context context) {
        playSound(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD); // A pleasant chime-like sound
        vibrate(context, 200);
    }

    public static void playSuccessSound(Context context) {
        playSound(ToneGenerator.TONE_SUP_CONFIRM);
        vibrate(context, 100);
    }

    public static void playFailureSound(Context context) {
        playSound(ToneGenerator.TONE_SUP_ERROR);
        vibrate(context, 300);
    }

    private static void playSound(int toneType) {
        if (toneGenerator != null) {
            try {
                toneGenerator.startTone(toneType, 200); // Play for 200ms
            } catch (Exception e) {
                Log.e(TAG, "Error playing sound", e);
            }
        }
    }

    private static void vibrate(Context context, long duration) {
        try {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(duration);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error vibrating", e);
        }
    }
}
