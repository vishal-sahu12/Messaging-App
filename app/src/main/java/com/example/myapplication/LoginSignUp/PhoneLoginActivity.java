package com.example.myapplication.LoginSignUp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myapplication.R;

public class PhoneLoginActivity extends AppCompatActivity {

    EditText phoneNumber,verificationCode;
    ProgressDialog progressDialog;
    TextView loginThroughEmail;
    Button sendOTP,VerifyOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);
        Initalize();
        loginThroughEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToLogin();
            }
        });
        sendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userPhoneNumber = phoneNumber.getText().toString();
                if (TextUtils.isEmpty(userPhoneNumber)){
                    phoneNumber.setError("Enter Your Phone Number");
                }
                if(userPhoneNumber.length()!=10){
                    phoneNumber.setError("Enter 10 Digit Number Only");
                }
                else{
                    progressDialog.setTitle("Verifying Account");
                    progressDialog.setMessage("Please wait, while verifying  your account..");
                    progressDialog.show();

                }
            }
        });
    }
    private void sendUserToLogin() {
        Intent intent = new Intent(PhoneLoginActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void Initalize() {
        progressDialog = new ProgressDialog(this);
        phoneNumber = findViewById(R.id.phone_number);
        verificationCode = findViewById(R.id.verification_code);
        sendOTP = findViewById(R.id.send_otp_btn);
        VerifyOTP = findViewById(R.id.verify_btn);
        loginThroughEmail = findViewById(R.id.login_through_email);

    }
}