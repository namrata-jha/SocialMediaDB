package com.namrata.android.dbmsproject;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.namrata.android.dbmsproject.Adapters.PostListAdapter;
import com.namrata.android.dbmsproject.Adapters.UserListAdapter;
import com.namrata.android.dbmsproject.Retrofit.INodeJS;
import com.namrata.android.dbmsproject.Retrofit.RetrofitClient;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    INodeJS myAPI;

    RecyclerView recyclerView;
    PostListAdapter adapter;

    FloatingActionButton fab;

    private DrawerLayout mDrawerLayout;

    SwipeRefreshLayout swipe;

    ImageView profilePicture;
    TextView profileName, email;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null;
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        swipe = findViewById(R.id.swipe_main);

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateActivity();
                swipe.setRefreshing(false);
            }
        });

        NavigationView navigationView = findViewById(R.id.nav_view);

        View navigationHeader = navigationView.getHeaderView(0);
        profilePicture = navigationHeader.findViewById(R.id.nav_image);
        profileName = navigationHeader.findViewById(R.id.nav_name);
        email = navigationHeader.findViewById(R.id.nav_email);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        switch (menuItem.getItemId()) {
                            case R.id.nav_logout:
                                mDrawerLayout.closeDrawer(GravityCompat.START);
                                SessionManager sessionManager = new SessionManager(getApplicationContext());
                                sessionManager.logoutUser();
                                Toast.makeText(getApplicationContext(), "Clicked logout!", Toast.LENGTH_SHORT).show();
                                finish();
                                return true;

                            case R.id.nav_my_feed:
                                Intent intent = new Intent(MainActivity.this, PostsViewActivity.class);
                                intent.putExtra("type", "my_posts");
                                startActivity(intent);
                                return true;

                            case R.id.nav_trending:
                                Intent trendingIntent = new Intent(MainActivity.this, PostsViewActivity.class);
                                trendingIntent.putExtra("type", "trending");
                                startActivity(trendingIntent);
                                return true;

                            case R.id.nav_find_friends:
                                Intent findIntent = new Intent(MainActivity.this, SearchActivity.class);
                                startActivity(findIntent);
                                return true;
                        }

                        return true;
                    }
                });

        // Init retrofit
        myAPI = RetrofitClient.getInstance().create(INodeJS.class);

        SessionManager manager = new SessionManager(getApplicationContext());

        profileName.setText(manager.getUserName());
        email.setText(manager.getEmail());
        Call<String> call = myAPI.getProfileImage(manager.getUserId());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.code() == 200){

                    String url = response.body();
                    Picasso.get().load(url).into(profilePicture);
                }
                else {
                    Toast.makeText(MainActivity.this, response.errorBody().toString(), Toast.LENGTH_SHORT).show();
                    Log.i("MyLogs else: ", response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.i("MyLogs", t.getMessage());
            }
        });

        fab = findViewById(R.id.create_post_button);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CreatePostActivity.class));
            }
        });

        populateActivity();

    }

    private void populateActivity(){

        SessionManager manager = new SessionManager(getApplicationContext());
        Call<List<Post>> call = myAPI.getAllPosts(manager.getUserId());
        call.enqueue(new Callback<List<Post>>() {

            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                Log.i("MyLogs", "onResponse: "+call.toString()+"\n"+response.toString());
                generateDataList(response.body());
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /*Method to generate List of data using RecyclerView with custom adapter*/
    private void generateDataList(List<Post> postList) {
        recyclerView = findViewById(R.id.post_list);
        adapter = new PostListAdapter(postList, MainActivity.this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
