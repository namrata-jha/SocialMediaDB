package com.namrata.android.dbmsproject;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.namrata.android.dbmsproject.Adapters.PostListAdapter;
import com.namrata.android.dbmsproject.Adapters.UserListAdapter;
import com.namrata.android.dbmsproject.Retrofit.INodeJS;
import com.namrata.android.dbmsproject.Retrofit.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PeopleWhoLikedActivity extends AppCompatActivity {

    SwipeRefreshLayout swipe;
    INodeJS myAPI;
    RecyclerView recyclerView;
    UserListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_who_liked);


        Toolbar toolbar = findViewById(R.id.likes_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null;
        if(getIntent().getStringExtra("action").equals("likes"))
            actionbar.setTitle("People who liked this post");
        else
            actionbar.setTitle("People who shared this post");


        swipe = findViewById(R.id.swipe_likes);

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateActivity(getIntent().getStringExtra("action"));
                swipe.setRefreshing(false);
            }
        });

        myAPI = RetrofitClient.getInstance().create(INodeJS.class);

        populateActivity(getIntent().getStringExtra("action"));
    }

    private void populateActivity(String action){

        String posterId = getIntent().getStringExtra("poster_id"), postTimestamp = getIntent().getStringExtra("post_timestamp");

        Call<List<User>> call;

        if(action.equals("likes"))
            call = myAPI.getAllLikers(posterId, postTimestamp);
        else
            call = myAPI.getAllShares(posterId, postTimestamp);

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                generateDataList(response.body());
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(PeopleWhoLikedActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    /*Method to generate List of data using RecyclerView with custom adapter*/
    private void generateDataList(List<User> likesList) {
        recyclerView = findViewById(R.id.likes_list);
        adapter = new UserListAdapter(likesList, PeopleWhoLikedActivity.this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(PeopleWhoLikedActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}
