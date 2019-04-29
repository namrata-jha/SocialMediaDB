package com.namrata.android.dbmsproject;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.namrata.android.dbmsproject.Adapters.PostListAdapter;
import com.namrata.android.dbmsproject.Retrofit.INodeJS;
import com.namrata.android.dbmsproject.Retrofit.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostsViewActivity extends AppCompatActivity {

    INodeJS myAPI;

    RecyclerView recyclerView;
    PostListAdapter adapter;
    SwipeRefreshLayout swipe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts_view);

        Toolbar toolbar = findViewById(R.id.my_post_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null;
        if(getIntent().getStringExtra("type").equals("trending"))
            actionbar.setTitle("Trending posts");
        else
            actionbar.setTitle("My posts");

        swipe = findViewById(R.id.swipe_my_post);


        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateActivity();
                swipe.setRefreshing(false);
            }
        });

        myAPI = RetrofitClient.getInstance().create(INodeJS.class);

        populateActivity();

    }

    private void populateActivity(){

        SessionManager manager = new SessionManager(getApplicationContext());
        Call<List<Post>> call;
        if(getIntent().getStringExtra("type").equals("trending"))
            call = myAPI.getTrendingPosts(manager.getUserId());
        else
            call = myAPI.getMyPosts(manager.getUserId());

        call.enqueue(new Callback<List<Post>>() {

            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                Log.i("MyLogs", "onResponse: "+call.toString()+"\n"+response.toString());
                generateDataList(response.body());
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /*Method to generate List of data using RecyclerView with custom adapter*/
    private void generateDataList(List<Post> postList) {
        recyclerView = findViewById(R.id.my_post_list);
        adapter = new PostListAdapter(postList, getApplicationContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }


}
