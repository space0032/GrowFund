package com.growfund.seedtowealth.model;

public class User {
    private String firebaseUid;
    private String email;
    private String displayName;
    private String photoUrl;
    private Integer coins;

    // Getters
    public String getFirebaseUid() {
        return firebaseUid;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public Integer getCoins() {
        return coins;
    }
}
