package com.example.android.vozmail;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.vozmail.api.service.AuthClient;
import com.example.android.vozmail.api.service.RetrofitBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthenticateActivity extends AppCompatActivity {

    String UserName, UserEmail;
    TextView name, email;
    String authCode;
    private String client_id;
    private String client_secret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticate);
        Intent intent = getIntent();
        UserName = intent.getStringExtra("Name");
        UserEmail = intent.getStringExtra("Email");
        authCode = intent.getStringExtra("AuthCode");

        name = findViewById(R.id.username);
        email = findViewById(R.id.email);

        name.setText(UserName);
        email.setText(UserEmail);
        client_id = getResources().getString(R.string.server_client_id);
        client_secret = getString(R.string.server_client_secret);


    }
}