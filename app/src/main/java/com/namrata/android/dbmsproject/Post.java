package com.namrata.android.dbmsproject;

import com.google.gson.annotations.SerializedName;


public class Post {

    @SerializedName("poster_id")
    private String posterId;

    @SerializedName("poster_name")
    private String posterName;

    @SerializedName("profile_picture_url")
    private String profilePictureUrl;

    @SerializedName("post_image_url")
    private String postImageUrl;

    @SerializedName("post_caption")
    private String postCaption;

    @SerializedName("post_timestamp")
    private String timestamp;

    @SerializedName("likes_count")
    private long likesCount;

    @SerializedName("comments_count")
    private long commentsCount;

    @SerializedName("share_count")
    private long shareCount;

    @SerializedName("like_bool")
    private int userLikesPost;

//    likes, comments, shares;
//    private boolean postPublic;


    public Post(String posterId, String posterName, String profilePictureUrl, String postImageUrl, String postCaption, String timestamp, long likesCount, int userLikesPost) {
        this.posterId = posterId;
        this.posterName = posterName;
        this.profilePictureUrl = profilePictureUrl;
        this.postImageUrl = postImageUrl;
        this.postCaption = postCaption;
        this.timestamp = timestamp;
        this.likesCount = likesCount;
        this.userLikesPost = userLikesPost;
    }

    public String getPosterName() {
        return posterName;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public String getPostImageUrl() {
        return postImageUrl;
    }

    public String getPostCaption() {
        return postCaption;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getPosterId() {
        return posterId;
    }

    public long getLikesCount() {
        return likesCount;
    }

    public int getUserLikesPost() {
        return userLikesPost;
    }

    public long getCommentsCount() {
        return commentsCount;
    }

    public long getShareCount() {
        return shareCount;
    }
}
