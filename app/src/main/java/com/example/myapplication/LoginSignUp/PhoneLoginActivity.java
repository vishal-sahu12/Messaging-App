package com.example.myapplication.LoginSignUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.core.Tag;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {

    EditText phoneNumber,verificationCode;
    ProgressDialog progressDialog;
    TextView loginThroughEmail;
    Button sendOTP,VerifyOTP;
    FirebaseAuth auth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks callback;
    String verificationId;
    PhoneAuthProvider.ForceResendingToken token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);
        Initialize();
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

                // For Checking the 10 digit number

                if(userPhoneNumber.length()!=10){
                    phoneNumber.setError("Enter 10 Digit Number..");
                }

                else{
                    progressDialog.setTitle("Verifying Account");
                    progressDialog.setMessage("Please wait, while verifying  your account..");
                    progressDialog.show();
                    PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth).
                            setPhoneNumber("+91"+ userPhoneNumber).
                            setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(PhoneLoginActivity.this)
                            .setCallbacks(callback).build();
                    PhoneAuthProvider.verifyPhoneNumber(options);
                    auth.setLanguageCode("en");

                }
            }
        });

        callback= new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                progressDialog.cancel();
               verificationCode.setText(phoneAuthCredential.getSmsCode());
                //signInWithPhoneCredentials(phoneAuthCredential);



            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                progressDialog.dismiss();
                progressDialog.cancel();
                Toast.makeText(getApplicationContext(), "Invalid Phone Number,Please Enter Valid Phone Number", Toast.LENGTH_SHORT).show();
                verificationCode.setVisibility(View.INVISIBLE);
                VerifyOTP.setVisibility(View.INVISIBLE);
                phoneNumber.setVisibility(View.VISIBLE);
                sendOTP.setVisibility(View.VISIBLE);

                // Exceptional Handling

                if (e instanceof FirebaseAuthInvalidCredentialsException){
                    Toast.makeText(getApplicationContext(),  "Invalid Request" +e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
                if (e instanceof FirebaseTooManyRequestsException){
                    Toast.makeText(getApplicationContext(), "Your Sms Limit has been Expired", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken Token) {
                verificationId =s;
                token = Token;
                Toast.makeText(getApplicationContext(), "Code Sent", Toast.LENGTH_SHORT).show();
                verificationCode.setVisibility(View.VISIBLE);
                VerifyOTP.setVisibility(View.VISIBLE);
                phoneNumber.setVisibility(View.INVISIBLE);
                sendOTP.setVisibility(View.INVISIBLE);
                progressDialog.cancel();

                    }
        };

        VerifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                phoneNumber.setVisibility(View.INVISIBLE);
                sendOTP.setVisibility(View.INVISIBLE);
                String code = verificationCode.getText().toString();
                if (TextUtils.isEmpty(code)){
                    verificationCode.setError("Please Enter Verification Code");
                }
                else {
                    progressDialog.setTitle("Verification Code");
                    progressDialog.setMessage("Please Wait ,while verifying your code...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,code);
                    signInWithPhoneCredentials(credential);
                }
            }
        });
    }

    private void signInWithPhoneCredentials(PhoneAuthCredential phoneAuthCredential) {
        auth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "You are Successfully logged In", Toast.LENGTH_SHORT).show();
                    sendUserToMainActivity();
                }
                else {
                    String error = task.getException().toString();
                    Toast.makeText(PhoneLoginActivity.this, "Error : "+error, Toast.LENGTH_SHORT).show();

                }

                progressDialog.dismiss();
                progressDialog.cancel();
            }
        })  ;
    }

    private void sendUserToLogin() {
        Intent intent = new Intent(PhoneLoginActivity.this,LoginActivity.class);
        startActivity(intent);

    }
    private void sendUserToMainActivity() {
        Intent intent = new Intent(PhoneLoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void Initialize() {
        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        phoneNumber = findViewById(R.id.phone_number);
        verificationCode = findViewById(R.id.verification_code);
        sendOTP = findViewById(R.id.send_otp_btn);
        VerifyOTP = findViewById(R.id.verify_btn);
        loginThroughEmail = findViewById(R.id.login_through_email);
        verificationCode.setVisibility(View.INVISIBLE);
        VerifyOTP.setVisibility(View.INVISIBLE);
        phoneNumber.setVisibility(View.VISIBLE);
        sendOTP.setVisibility(View.VISIBLE);


    }

}