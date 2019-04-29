package com.namrata.android.dbmsproject.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.namrata.android.dbmsproject.EditPostActivity;
import com.namrata.android.dbmsproject.PeopleWhoCommentedActivity;
import com.namrata.android.dbmsproject.PeopleWhoLikedActivity;
import com.namrata.android.dbmsproject.Post;
import com.namrata.android.dbmsproject.R;
import com.namrata.android.dbmsproject.Retrofit.INodeJS;
import com.namrata.android.dbmsproject.Retrofit.RetrofitClient;
import com.namrata.android.dbmsproject.SessionManager;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.PostViewHolder> {

    private List<Post> dataList;
    private Context context;

    public PostListAdapter(List<Post> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }


    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.post_layout, viewGroup, false);
        return new PostListAdapter.PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder myViewHolder, int i) {
        myViewHolder.name.setText(dataList.get(i).getPosterName());
        myViewHolder.caption.setText(dataList.get(i).getPostCaption());
        myViewHolder.time.setText(getDate(dataList.get(i).getTimestamp()));

        if (dataList.get(i).getPostImageUrl() != null) {
            Log.i("MyLogs", dataList.get(i).getPostImageUrl());
            if (!dataList.get(i).getPostImageUrl().isEmpty())
                Picasso.get().load(dataList.get(i).getPostImageUrl()).into(myViewHolder.postImage);
            else
                myViewHolder.postImage.setVisibility(View.GONE);
        }

        else
            myViewHolder.postImage.setVisibility(View.GONE);


        if (dataList.get(i).getProfilePictureUrl() != null)
            if (!dataList.get(i).getProfilePictureUrl().isEmpty())
                Picasso.get().load(dataList.get(i).getProfilePictureUrl()).into(myViewHolder.profileImage);
            else
                myViewHolder.profileImage.setVisibility(View.GONE);
        else
            myViewHolder.profileImage.setVisibility(View.GONE);

        myViewHolder.likesCount.setText(String.valueOf(dataList.get(i).getLikesCount()));
        myViewHolder.commentsCount.setText(String.valueOf(dataList.get(i).getCommentsCount()));
        myViewHolder.sharesCount.setText(String.valueOf(dataList.get(i).getShareCount()));

        if(dataList.get(i).getUserLikesPost() == 0)
            myViewHolder.heartIcon.setImageResource(R.drawable.ic_heart);
        else
            myViewHolder.heartIcon.setImageResource(R.drawable.ic_heart_fill);

        if(!dataList.get(i).getPosterId().equals(new SessionManager(context).getUserId())){
            myViewHolder.deletePostIcon.setVisibility(View.GONE);
            myViewHolder.editPostIcon.setVisibility(View.GONE);
        }



    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


    public class PostViewHolder extends RecyclerView.ViewHolder {

        TextView name, caption, time, likesCount, commentsCount, sharesCount;
        ImageView profileImage, postImage, heartIcon, commentIcon, shareIcon, editPostIcon, deletePostIcon;

        public PostViewHolder(@NonNull final View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            caption = itemView.findViewById(R.id.post_text);
            time = itemView.findViewById(R.id.time);
            profileImage = itemView.findViewById(R.id.create_post_profile_image);
            postImage = itemView.findViewById(R.id.post_image);
            likesCount = itemView.findViewById(R.id.heart_count);
            heartIcon = itemView.findViewById(R.id.heart);
            commentsCount = itemView.findViewById(R.id.comment_count);
            sharesCount = itemView.findViewById(R.id.share_count);
            commentIcon = itemView.findViewById(R.id.comment);
            shareIcon = itemView.findViewById(R.id.share);
            editPostIcon = itemView.findViewById(R.id.update_post);
            deletePostIcon = itemView.findViewById(R.id.delete_post);

            editPostIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), EditPostActivity.class);
                    intent.putExtra("post_timestamp", dataList.get(getAdapterPosition()).getTimestamp());
                    intent.putExtra("caption", dataList.get(getAdapterPosition()).getPostCaption());
                    intent.putExtra("url", dataList.get(getAdapterPosition()).getPostImageUrl());
                    itemView.getContext().startActivity(intent);
                }
            });

            deletePostIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deletePost(itemView.getContext(), dataList.get(getAdapterPosition()).getTimestamp());
                }
            });

            heartIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(dataList.get(getAdapterPosition()).getUserLikesPost() == 0)
                        likePost(itemView.getContext(), dataList.get(getAdapterPosition()).getPosterId()
                                , dataList.get(getAdapterPosition()).getTimestamp());

                    else
                        unlikePost(itemView.getContext(), dataList.get(getAdapterPosition()).getPosterId()
                                , dataList.get(getAdapterPosition()).getTimestamp());
                }
            });

            shareIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sharePost(itemView.getContext(), dataList.get(getAdapterPosition()).getPosterId()
                            , dataList.get(getAdapterPosition()).getTimestamp());
                }
            });

            commentIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), PeopleWhoCommentedActivity.class);
                    intent.putExtra("poster_id", dataList.get(getAdapterPosition()).getPosterId());
                    intent.putExtra("post_timestamp", dataList.get(getAdapterPosition()).getTimestamp());
                    itemView.getContext().startActivity(intent);
                }
            });

            likesCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), PeopleWhoLikedActivity.class);
                    intent.putExtra("action", "likes");
                    intent.putExtra("poster_id", dataList.get(getAdapterPosition()).getPosterId());
                    intent.putExtra("post_timestamp", dataList.get(getAdapterPosition()).getTimestamp());
                    itemView.getContext().startActivity(intent);
                }
            });

            sharesCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), PeopleWhoLikedActivity.class);
                    intent.putExtra("action", "shares");
                    intent.putExtra("poster_id", dataList.get(getAdapterPosition()).getPosterId());
                    intent.putExtra("post_timestamp", dataList.get(getAdapterPosition()).getTimestamp());
                    itemView.getContext().startActivity(intent);
                }
            });

        }
    }

    private String getDate(String timestamp) {
        long time = Long.parseLong(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm a\nd MMM yyyy", Locale.getDefault());
        return sdf.format(new Date(time));
    }


    private void unlikePost(final Context itemContext, String posterId, String postTimestamp){
        SessionManager sessionManager = new SessionManager(itemContext);
        INodeJS myAPI = RetrofitClient.getInstance().create(INodeJS.class);
        if(sessionManager.getUserId() == null)
            Toast.makeText(itemContext, "Something's wrong", Toast.LENGTH_SHORT).show();
        Call<String> call = myAPI.unlikePost(posterId, postTimestamp, sessionManager.getUserId());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.code() == 200){
                    Toast.makeText(itemContext, "Unliked post!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i("MyLogs", "Response code: " + response.code());
                    Toast.makeText(itemContext, "Something unexpected occurred.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(itemContext, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void likePost(final Context itemContext, String posterId, String postTimestamp){
        SessionManager sessionManager = new SessionManager(itemContext);
        INodeJS myAPI = RetrofitClient.getInstance().create(INodeJS.class);
        if(sessionManager.getUserId() == null)
            Toast.makeText(itemContext, "Something's wrong", Toast.LENGTH_SHORT).show();
        Call<String> call = myAPI.likePost(posterId, postTimestamp, sessionManager.getUserId());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.code() == 200){
                    Toast.makeText(itemContext, "Liked post!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i("MyLogs", "Response code: " + response.code());
                    Toast.makeText(itemContext, "Something unexpected occurred.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(itemContext, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deletePost(final Context itemContext, String postTimestamp){
        SessionManager sessionManager = new SessionManager(itemContext);
        INodeJS myAPI = RetrofitClient.getInstance().create(INodeJS.class);
        if(sessionManager.getUserId() == null)
            Toast.makeText(itemContext, "Something's wrong", Toast.LENGTH_SHORT).show();
        Call<String> call = myAPI.deletePost(sessionManager.getUserId(), postTimestamp);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.code() == 200){
                    Toast.makeText(itemContext, "Deleted post!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i("MyLogs", "Response code: " + response.code());
                    Toast.makeText(itemContext, "Something unexpected occurred.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(itemContext, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void sharePost(final Context itemContext, String posterId, String postTimestamp){
        SessionManager sessionManager = new SessionManager(itemContext);
        INodeJS myAPI = RetrofitClient.getInstance().create(INodeJS.class);
        if(sessionManager.getUserId() == null)
            Toast.makeText(itemContext, "Something's wrong", Toast.LENGTH_SHORT).show();
        Call<String> call = myAPI.sharePost(posterId, postTimestamp, sessionManager.getUserId(), String.valueOf(new Date().getTime()));

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.code() == 200){
                    Toast.makeText(itemContext, "Shared post!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i("MyLogs", "Response code: " + response.code());
                    Toast.makeText(itemContext, "Something unexpected occurred.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(itemContext, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
