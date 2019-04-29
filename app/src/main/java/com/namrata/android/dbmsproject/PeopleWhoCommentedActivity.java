package com.namrata.android.dbmsproject;

import android.se.omapi.Session;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.namrata.android.dbmsproject.Adapters.CommentListAdapter;
import com.namrata.android.dbmsproject.Adapters.UserListAdapter;
import com.namrata.android.dbmsproject.Retrofit.INodeJS;
import com.namrata.android.dbmsproject.Retrofit.RetrofitClient;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PeopleWhoCommentedActivity extends AppCompatActivity {

    SwipeRefreshLayout swipe;
    INodeJS myAPI;
    RecyclerView recyclerView;
    CommentListAdapter adapter;
    FloatingActionButton fab;
    EditText commentInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_who_commented);

        Toolbar toolbar = findViewById(R.id.likes_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null;
        actionbar.setTitle("People who commented on this post");

        commentInput = findViewById(R.id.comment_input);

        myAPI = RetrofitClient.getInstance().create(INodeJS.class);

        swipe = findViewById(R.id.swipe_likes);

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateActivity();
                swipe.setRefreshing(false);
            }
        });

        fab = findViewById(R.id.comment_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String posterId = getIntent().getStringExtra("poster_id")
                        , postTimestamp = getIntent().getStringExtra("post_timestamp");
                SessionManager manager = new SessionManager(v.getContext());
                String comment = commentInput.getText().toString();
                Call<String> call = myAPI.addComment(posterId, postTimestamp, manager.getUserId()
                        , comment, String.valueOf(new Date().getTime()));

                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.code() == 200){
                            Toast.makeText(v.getContext(), "Added comment!", Toast.LENGTH_SHORT).show();
                            commentInput.setText("");
                        } else {
                            Log.i("MyLogs", "Response code: " + response.code());
                            Toast.makeText(v.getContext(), "Something unexpected occurred.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Toast.makeText(v.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        populateActivity();


    }

    private void populateActivity(){

        String posterId = getIntent().getStringExtra("poster_id"), postTimestamp = getIntent().getStringExtra("post_timestamp");
        SessionManager manager = new SessionManager(getApplicationContext());
        Call<List<Comment>> call = myAPI.getAllComments(posterId, postTimestamp);

        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                generateDataList(response.body());

            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                Toast.makeText(PeopleWhoCommentedActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    /*Method to generate List of data using RecyclerView with custom adapter*/
    private void generateDataList(List<Comment> likesList) {
        recyclerView = findViewById(R.id.likes_list);
        adapter = new CommentListAdapter(likesList, getApplicationContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}
