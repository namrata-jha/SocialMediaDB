package com.namrata.android.dbmsproject;

import com.google.gson.annotations.SerializedName;

public class Comment {

    @SerializedName("name")
    private String commenterName;

    @SerializedName("profile_picture_url")
    private String profilePictureUrl;

    @SerializedName("comment_text")
    private String commentText;

    @SerializedName("comment_timestamp")
    private String commentTimestamp;

    public Comment(String name, String profilePictureUrl, String commentText, String commentTimestamp) {
        this.commenterName = name;
        this.profilePictureUrl = profilePictureUrl;
        this.commentText = commentText;
        this.commentTimestamp = commentTimestamp;
    }

    public String getCommenterName() {
        return commenterName;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public String getCommentText() {
        return commentText;
    }

    public String getCommentTimestamp() {
        return commentTimestamp;
    }
}
