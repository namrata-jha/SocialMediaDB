package com.namrata.android.dbmsproject;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("unique_id")
    private
    String uniqueId;

    @SerializedName("name")
    private
    String name;

    @SerializedName("email")
    private
    String email;

    @SerializedName("profile_picture_url")
    private String profilePictureUrl;

    public User(String uniqueId, String name, String email, String profilePictureUrl) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.email = email;
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
}
