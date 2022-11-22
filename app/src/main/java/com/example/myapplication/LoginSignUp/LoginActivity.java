package com.example.myapplication.LoginSignUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {
    TextView needaccount, forgotPassword;
    Button phoneNumber, login;
    EditText email, password;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    DatabaseReference UserRef;

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
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allowUserToLogin();
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResetLink();
            }
        });
    }

    private void sendUserToPhoneNumberLoginActivity() {

        Intent intent = new Intent(LoginActivity.this, PhoneLoginActivity.class);
        startActivity(intent);
        finish();
    }


    private void sendUserToRegister() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }


    private void Initialize() {
        progressDialog = new ProgressDialog(this);
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        auth = FirebaseAuth.getInstance();
        forgotPassword = findViewById(R.id.forgotpswd_text);
        needaccount = findViewById(R.id.signup_text);
        phoneNumber = findViewById(R.id.phone_number_btn);
        email = findViewById(R.id.edit_email);
        password = findViewById(R.id.edit_pass);
        login = findViewById(R.id.login_btn);
    }


    private void sendResetLink() {
        if (email.getText().toString().matches("^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$") &&
                email.getText().toString().length()>8) {
            AlertDialog.Builder passwordReset = new AlertDialog.Builder(this);
            passwordReset.setTitle("Reset Password");
            passwordReset.setPositiveButton("YES",(dialog, which) -> {
                String resetEmail =email.getText().toString();
                auth.sendPasswordResetEmail(resetEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(LoginActivity.this, "Reset Link has been Sent to your EmailId ", Toast.LENGTH_SHORT).show();
                    }
                });
            });

            passwordReset.setNegativeButton("NO",(dialog, which) -> {});
            passwordReset.show();

        }
        else {
            email.setError("Please Enter Your EmailId..");
        }


    }

    private void allowUserToLogin() {

        String userEmail = email.getText().toString();
        String userPass = password.getText().toString();
        if (TextUtils.isEmpty(userEmail)) {
            email.setError("Please Enter your EMail id");
        }
        if (TextUtils.isEmpty(userPass)) {
            password.setError("Please Enter your Password");
        }
        if (TextUtils.isEmpty(userEmail) && TextUtils.isEmpty(userPass)) {
            email.setError("Please Enter your EMail id");
            password.setError("Please Enter your Password");
        }
        if (!TextUtils.isEmpty(userEmail) && !TextUtils.isEmpty(userPass)) {
            progressDialog.setTitle("Logging In");
            progressDialog.setMessage("Please wait, while Login in Your account..");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            auth.signInWithEmailAndPassword(userEmail, userPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        final String[] deviceToken = new String[1];
                        FirebaseMessaging.getInstance().getToken()
                                .addOnCompleteListener(new OnCompleteListener<String>() {
                                    @Override
                                    public void onComplete(@NonNull Task<String> task) {
                                        deviceToken[0] = task.getResult();
                                    }
                                });
                        firebaseUser=auth.getCurrentUser();
                        String currentUserId = firebaseUser.getUid();
                        UserRef.child(currentUserId).setValue("");
                        UserRef.child(currentUserId).child("device_token").setValue(deviceToken[0]);
                        sendUserToMainActivity();
                        Toast.makeText(LoginActivity.this, "Account Created SuccessFully", Toast.LENGTH_SHORT).show();

                    } else {
                        String error = task.getException().toString();
                        Toast.makeText(LoginActivity.this, "Error Occurred : "+error, Toast.LENGTH_SHORT).show();

                    }
                    progressDialog.cancel();
                    progressDialog.dismiss();
                }
            });
        }
    }

    private void sendUserToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}