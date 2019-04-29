package com.namrata.android.dbmsproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.namrata.android.dbmsproject.Adapters.UserListAdapter;
import com.namrata.android.dbmsproject.Retrofit.INodeJS;
import com.namrata.android.dbmsproject.Retrofit.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    EditText searchBox;
    ImageView searchButton;
    RecyclerView recyclerView;
    UserListAdapter adapter;

    INodeJS myAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchBox = findViewById(R.id.search_edittext);
        searchButton = findViewById(R.id.search_button);

        myAPI = RetrofitClient.getInstance().create(INodeJS.class);


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = searchBox.getText().toString().toLowerCase();

                populateActivity(keyword);
            }
        });
    }

    private void populateActivity(String keyword) {

        Call<List<User>> call = myAPI.searchProfiles(keyword);

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                generateDataList(response.body());
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(SearchActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    /*Method to generate List of data using RecyclerView with custom adapter*/
    private void generateDataList(List<User> searchList) {
        recyclerView = findViewById(R.id.found_profile_list);
        adapter = new UserListAdapter(searchList, SearchActivity.this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SearchActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}