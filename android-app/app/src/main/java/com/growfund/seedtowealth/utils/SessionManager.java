package com.growfund.seedtowealth.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.growfund.seedtowealth.model.User;

public class SessionManager {
    private static final String PREF_NAME = "GrowFundSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_FIREBASE_UID = "firebaseUid";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_NAME = "name";
    private static final String KEY_PHOTO_URL = "photoUrl";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(String firebaseUid, String email, String name, String photoUrl) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_FIREBASE_UID, firebaseUid);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_PHOTO_URL, photoUrl);
        editor.apply();
    }

    public void saveUser(User user) {
        if (user != null) {
            editor.putString(KEY_FIREBASE_UID, user.getUid());
            editor.putString(KEY_EMAIL, user.getEmail());
            editor.putString(KEY_NAME, user.getName());
            editor.putString(KEY_PHOTO_URL, user.getPhotoUrl());
            editor.apply();
        }
    }

    public User getUserDetails() {
        if (!isLoggedIn()) {
            return null;
        }
        String uid = pref.getString(KEY_FIREBASE_UID, null);
        String email = pref.getString(KEY_EMAIL, null);
        String name = pref.getString(KEY_NAME, null);
        String photoUrl = pref.getString(KEY_PHOTO_URL, null);

        return new User(email, name, photoUrl, uid);
    }

    public String getFirebaseUid() {
        return pref.getString(KEY_FIREBASE_UID, null);
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();
    }
}
