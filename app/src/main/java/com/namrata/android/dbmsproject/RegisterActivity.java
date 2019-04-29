package com.namrata.android.dbmsproject;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.namrata.android.dbmsproject.Retrofit.INodeJS;
import com.namrata.android.dbmsproject.Retrofit.RetrofitClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity {

    INodeJS myAPI;

    EditText emailEditText, passwordEditText, nameEditText, profilePicText;
    Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        nameEditText = findViewById(R.id.name);
        registerButton = findViewById(R.id.register_button);

        // Init retrofit
        Retrofit retrofit = RetrofitClient.getInstance();
        myAPI = retrofit.create(INodeJS.class);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String profilePicture = profilePicText.getText().toString();
                if(profilePicture.isEmpty())
                    profilePicture = null;

                if(!email.trim().isEmpty() && !password.trim().isEmpty() && !name.trim().isEmpty())
                    registerUser(name, email, password, profilePicture);
                else
                    Toast.makeText(RegisterActivity.this, "Empty strings not accepted!", Toast.LENGTH_SHORT).show();


            }
        });

    }

    private void registerUser(String name, final String email, final String password, String profilePic){

        Call<String> call = myAPI.registerUser(email, name, password, profilePic);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.code() == 409)
                    Toast.makeText(RegisterActivity.this, "User already exists, please try again.", Toast.LENGTH_SHORT).show();

                else if(response.code() == 200) {
                    Toast.makeText(RegisterActivity.this, "Successfully registered, please log in.", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK, new Intent());
                    finish();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });


    }
}
