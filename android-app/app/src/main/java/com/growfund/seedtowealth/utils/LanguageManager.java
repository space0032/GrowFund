package com.growfund.seedtowealth.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

/**
 * Manages app language/locale settings.
 * Supports: English, Hindi, Tamil, Telugu, Marathi, Bengali, Gujarati
 */
public class LanguageManager {

    private static final String PREF_NAME = "language_pref";
    private static final String KEY_LANGUAGE = "selected_language";
    private static final String DEFAULT_LANGUAGE = "en";

    // Supported languages
    public static final String LANG_ENGLISH = "en";
    public static final String LANG_HINDI = "hi";
    public static final String LANG_TAMIL = "ta";
    public static final String LANG_TELUGU = "te";
    public static final String LANG_MARATHI = "mr";
    public static final String LANG_BENGALI = "bn";
    public static final String LANG_GUJARATI = "gu";

    /**
     * Get current language code
     */
    public static String getLanguage(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_LANGUAGE, DEFAULT_LANGUAGE);
    }

    /**
     * Set language and save preference
     */
    public static void setLanguage(Context context, String languageCode) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply();
    }

    /**
     * Apply language to context
     * Call this in attachBaseContext() or onCreate()
     */
    public static Context applyLanguage(Context context) {
        String languageCode = getLanguage(context);
        return updateResources(context, languageCode);
    }

    /**
     * Update context resources with new locale
     */
    private static Context updateResources(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration configuration = new Configuration(resources.getConfiguration());
        configuration.setLocale(locale);

        return context.createConfigurationContext(configuration);
    }

    /**
     * Get display name for language code
     */
    public static String getLanguageName(String languageCode) {
        switch (languageCode) {
            case LANG_ENGLISH:
                return "English";
            case LANG_HINDI:
                return "हिन्दी (Hindi)";
            case LANG_TAMIL:
                return "தமிழ் (Tamil)";
            case LANG_TELUGU:
                return "తెలుగు (Telugu)";
            case LANG_MARATHI:
                return "मराठी (Marathi)";
            case LANG_BENGALI:
                return "বাংলা (Bengali)";
            case LANG_GUJARATI:
                return "ગુજરાતી (Gujarati)";
            default:
                return "English";
        }
    }

    /**
     * Get all supported languages
     */
    public static String[] getSupportedLanguages() {
        return new String[] {
                LANG_ENGLISH,
                LANG_HINDI,
                LANG_TAMIL,
                LANG_TELUGU,
                LANG_MARATHI,
                LANG_BENGALI,
                LANG_GUJARATI
        };
    }

    /**
     * Get display names for all supported languages
     */
    public static String[] getSupportedLanguageNames() {
        String[] codes = getSupportedLanguages();
        String[] names = new String[codes.length];
        for (int i = 0; i < codes.length; i++) {
            names[i] = getLanguageName(codes[i]);
        }
        return names;
    }
}
