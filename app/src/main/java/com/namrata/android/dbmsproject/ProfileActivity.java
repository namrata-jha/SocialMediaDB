package com.namrata.android.dbmsproject;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.namrata.android.dbmsproject.Adapters.PostListAdapter;
import com.namrata.android.dbmsproject.Retrofit.INodeJS;
import com.namrata.android.dbmsproject.Retrofit.RetrofitClient;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    INodeJS myAPI;

    RecyclerView recyclerView;
    PostListAdapter adapter;
    SwipeRefreshLayout swipe;
    TextView name, email, noPostsLabel;
    ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        swipe = findViewById(R.id.swipe_my_post);

        name = findViewById(R.id.profile_name);
        email = findViewById(R.id.profile_email);
        profileImage = findViewById(R.id.profile_image);
        noPostsLabel = findViewById(R.id.no_posts_label);


        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateActivity(getIntent().getStringExtra("userId"));
                swipe.setRefreshing(false);
            }
        });

        myAPI = RetrofitClient.getInstance().create(INodeJS.class);

        String userId = getIntent().getStringExtra("userId");
        String userName = getIntent().getStringExtra("name");
        String userEmail = getIntent().getStringExtra("email");
        String url = getIntent().getStringExtra("url");

        name.setText(userName);
        email.setText(userEmail);
        Picasso.get().load(url).into(profileImage);


        populateActivity(userId);

    }

    private void populateActivity(String userId) {

        Call<List<Post>> call = myAPI.getMyPosts(userId);

        call.enqueue(new Callback<List<Post>>() {

            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                Log.i("MyLogs", "onResponse: " + call.toString() + "\n" + response.toString());
                if(response.body() == null)
                    noPostsLabel.setVisibility(View.VISIBLE);

                else if(response.body().size() == 0)
                    noPostsLabel.setVisibility(View.VISIBLE);
                else
                    generateDataList(response.body());
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    /*Method to generate List of data using RecyclerView with custom adapter*/
    private void generateDataList(List<Post> postList) {
        recyclerView = findViewById(R.id.profile_post_list);
        adapter = new PostListAdapter(postList, getApplicationContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}
