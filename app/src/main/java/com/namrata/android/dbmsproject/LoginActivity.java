package com.namrata.android.dbmsproject;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.namrata.android.dbmsproject.Retrofit.INodeJS;
import com.namrata.android.dbmsproject.Retrofit.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {


    private static final int REGISTER_RESULT_CODE = 101;
    INodeJS myAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    EditText emailEditText, passwordEditText;
    Button loginButton;

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.login_email);
        passwordEditText = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);

        // Init retrofit
        Retrofit retrofit = RetrofitClient.getInstance();
        myAPI = retrofit.create(INodeJS.class);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if(!email.trim().isEmpty() && !password.trim().isEmpty())
                    loginUser(email, password);
                else
                    Toast.makeText(LoginActivity.this, "Empty strings not accepted!", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void loginUser(String email, String password){

        Call<User> call = myAPI.loginUser(email, password);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                if(response.code() == 400)
                    Toast.makeText(LoginActivity.this, "Wrong password", Toast.LENGTH_SHORT).show();

                else if(response.code() == 404)
                    Toast.makeText(LoginActivity.this, "User not found", Toast.LENGTH_SHORT).show();

                else {
                    User myUser = response.body();
                        Log.i("MyLogs", myUser.getName());
                        Toast.makeText(LoginActivity.this, "Login Successful!!!", Toast.LENGTH_SHORT).show();
                        SessionManager manager = new SessionManager(getApplicationContext());
                        manager.createLoginSession(myUser.getUniqueId(), myUser.getName(), myUser.getEmail());
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();

                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.i("MyLogs", "FAILURE: "+t.toString());
            }
        });


    }

    public void goToRegistration(View view) {
        startActivityForResult(new Intent(LoginActivity.this, RegisterActivity.class), REGISTER_RESULT_CODE);
    }

}
