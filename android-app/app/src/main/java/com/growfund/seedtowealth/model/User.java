package com.growfund.seedtowealth.model;

public class User {
    private String email;
    private String name;
    private String photoUrl;
    @com.google.gson.annotations.SerializedName("firebaseUid")
    private String uid;

    // Add other fields as returned by backend if needed (e.g. balance, level)

    public User(String email, String name, String photoUrl, String uid) {
        this.email = email;
        this.name = name;
        this.photoUrl = photoUrl;
        this.uid = uid;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
