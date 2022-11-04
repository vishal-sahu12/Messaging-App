package com.example.myapplication.LoginSignUp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.myapplication.R;

public class LoginActivity extends AppCompatActivity {
    TextView needaccount;
    Button phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Initialize();
        needaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToRegister();
            }
        });

        phoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToPhoneNumberLoginActivity();
            }
        });
    }
    private void sendUserToRegister() {
        Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
        finish();
    }
    private void sendUserToPhoneNumberLoginActivity() {
        Intent intent = new Intent(LoginActivity.this,PhoneLoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void Initialize() {
        needaccount = findViewById(R.id.signup_text);
        phoneNumber = findViewById(R.id.phone_number_btn);

    }
}