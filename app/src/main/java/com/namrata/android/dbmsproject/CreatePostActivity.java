package com.namrata.android.dbmsproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.namrata.android.dbmsproject.Retrofit.INodeJS;
import com.namrata.android.dbmsproject.Retrofit.RetrofitClient;
import com.squareup.picasso.Picasso;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePostActivity extends AppCompatActivity {

    INodeJS myAPI;

    TextView userName;
    EditText postCaption, postImageUrl;
    Button postButton;
    ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        myAPI = RetrofitClient.getInstance().create(INodeJS.class);


        userName = findViewById(R.id.post_user_name);
        SessionManager manager = new SessionManager(getApplicationContext());
        userName.setText(manager.getUserName());
        postCaption = findViewById(R.id.post_edittext);
        postImageUrl = findViewById(R.id.post_image_edittext);
        postButton = findViewById(R.id.post_button);
        profileImage = findViewById(R.id.create_post_profile_image);

        Call<String> call = myAPI.getProfileImage(manager.getUserId());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.code() == 200){

                    String url = response.body();
                    Picasso.get().load(url).into(profileImage);
                }
                else {
                    Toast.makeText(CreatePostActivity.this, response.errorBody().toString(), Toast.LENGTH_SHORT).show();
                    Log.i("MyLogs else: ", response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(CreatePostActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.i("MyLogs", t.getMessage());
            }
        });
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String caption = postCaption.getText().toString();
                String imageUrl = postImageUrl.getText().toString();

                createPost(caption, imageUrl);
            }
        });

    }

    private void createPost(String caption, String imageUrl){

        SessionManager sessionManager = new SessionManager(getApplicationContext());
        if(sessionManager.getUserId() == null)
            Toast.makeText(this, "Something's wrong", Toast.LENGTH_SHORT).show();
        Call<String> call = myAPI.createPost(sessionManager.getUserId(), String.valueOf(new Date().getTime()), imageUrl, caption);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.code() == 200){
                    Toast.makeText(CreatePostActivity.this, "Created post!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.i("MyLogs", "Response code: " + response.code());
                    Toast.makeText(CreatePostActivity.this, "Something unexpected occurred.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(CreatePostActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
