package com.namrata.android.dbmsproject.Retrofit;

import com.namrata.android.dbmsproject.Comment;
import com.namrata.android.dbmsproject.Post;
import com.namrata.android.dbmsproject.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface INodeJS {

    @POST("register")
    @FormUrlEncoded
    Call<String> registerUser(@Field("email") String email,
                                    @Field("name") String name,
                                    @Field("password") String password,
                              @Field("profile_pic") String imageUrl);

    @POST("login")
    @FormUrlEncoded
    Call<User> loginUser(@Field("email") String email,
                         @Field("password") String password);

    @POST("like/{poster_id}/{post_timestamp}")
    @FormUrlEncoded
    Call<String> likePost(@Path("poster_id") String poster_id,
                          @Path("post_timestamp") String post_timestamp,
                          @Field("liker_id") String liker_id);

    @POST("share/{poster_id}/{post_timestamp}")
    @FormUrlEncoded
    Call<String> sharePost(@Path("poster_id") String poster_id,
                           @Path("post_timestamp") String post_timestamp,
                          @Field("sharer_id") String sharer_id,
                           @Field("share_timestamp") String share_timestamp);

    @DELETE("like/{poster_id}/{post_timestamp}/{liker_id}")
    Call<String> unlikePost(@Path("poster_id") String poster_id,
                    @Path("post_timestamp") String post_timestamp,
                    @Path("liker_id") String liker_id);

    @POST("post")
    @FormUrlEncoded
    Call<String> createPost(@Field("unique_id") String uniqueId,
                            @Field("post_timestamp") String timestamp,
                            @Field("post_image_url") String postImageUrl,
                            @Field("post_caption") String caption);

    @PATCH("post/{poster_id}/{post_timestamp}")
    @FormUrlEncoded
    Call<String> updatePost(@Path("poster_id") String posterId,
                            @Path("post_timestamp") String timestamp,
                            @Field("post_image_url") String postImageUrl,
                            @Field("post_caption") String caption);

    @DELETE("post/{poster_id}/{post_timestamp}")
    Call<String> deletePost(@Path("poster_id") String posterId,
                            @Path("post_timestamp") String timestamp);

    @POST("comment/{posterId}/{postTimestamp}")
    @FormUrlEncoded
    Call<String> addComment(@Path("posterId") String posterId,
                            @Path("postTimestamp") String postTimestamp,
                            @Field("commenter_id") String commenterId,
                            @Field("comment_text") String comment,
                            @Field("comment_timestamp") String commentTimestamp);

    @GET("/likes/{posterId}/{postTimestamp}")
    Call<List<User>> getAllLikers(@Path("posterId") String posterId,
                                  @Path("postTimestamp") String postTimestamp);

    @GET("/search/{keyword}")
    Call<List<User>> searchProfiles(@Path("keyword") String keyword);

    @GET("/shares/{posterId}/{postTimestamp}")
    Call<List<User>> getAllShares(@Path("posterId") String posterId,
                                  @Path("postTimestamp") String postTimestamp);

    @GET("/comments/{posterId}/{postTimestamp}")
    Call<List<Comment>> getAllComments(@Path("posterId") String posterId,
                                       @Path("postTimestamp") String postTimestamp);

    @GET("/posts/{Id}")
    Call<List<Post>> getAllPosts(@Path("Id") String Id);

    @GET("/posts/user/{Id}")
    Call<List<Post>> getMyPosts(@Path("Id") String Id);

    @GET("/posts/trending/{Id}")
    Call<List<Post>> getTrendingPosts(@Path("Id") String Id);

    @GET("/user/{userId}")
    Call<User> getUserData(@Path("Id") String Id);

    @GET("/profileImage/{Id}")
    Call<String> getProfileImage(@Path("Id") String Id);




}
