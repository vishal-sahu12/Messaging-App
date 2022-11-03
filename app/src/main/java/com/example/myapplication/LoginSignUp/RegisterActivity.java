package com.example.myapplication.LoginSignUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class RegisterActivity extends AppCompatActivity {
    EditText editEmail,editPass;
    Button signUp;
    TextView alreadyAccount;
    FirebaseAuth auth;
    String email,pass;
    ProgressDialog progressDialog;
    DatabaseReference Rootref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Initialize();
        Rootref = FirebaseDatabase.getInstance().getReference();
        alreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToLogin();

            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creteNewAccount();

            }
        });

    }

    private void creteNewAccount() {
        email = editEmail.getText().toString();
        pass = editPass.getText().toString();
        if (TextUtils.isEmpty(email)){
            editEmail.setError("Please Enter your EMail id");
        }
        if (TextUtils.isEmpty(pass)){
            editPass.setError("Please Enter your Password");
        }
        if (TextUtils.isEmpty(email) && TextUtils.isEmpty(pass)){
            editEmail.setError("Please Enter your EMail id");
            editPass.setError("Please Enter your Password");
        }
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)){
        progressDialog.setTitle("Create Account");
        progressDialog.setMessage("Please wait, while creating an account..");
        progressDialog.show();
        auth.createUserWithEmailAndPassword(email,pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            final String[] deviceToken = new String[1];
                            FirebaseMessaging.getInstance().getToken()
                                    .addOnCompleteListener(new OnCompleteListener<String>() {
                                        @Override
                                        public void onComplete(@NonNull Task<String> task) {
                                            deviceToken[0] =task.getResult();
                                        }
                                    });
                            String currentUserId=auth.getCurrentUser().getUid();
                            Rootref.child("Users").child(currentUserId).setValue("");
                            Rootref.child("Users").child(currentUserId).child("device_token").setValue(deviceToken[0]);
                            sendUserToMainActivity();
                            Toast.makeText(RegisterActivity.this, "Account Created SuccessFully",Toast.LENGTH_SHORT).show();

                        }
                        else{
                            Toast.makeText(RegisterActivity.this, "Error Occurred while creating an Account", Toast.LENGTH_SHORT).show();

                        }
                        progressDialog.cancel();
                        progressDialog.dismiss();
                    }
                });

        }
    }

    private void sendUserToLogin() {
        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
    private void sendUserToMainActivity() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void Initialize() {
        editEmail = findViewById(R.id.signup_email);
        editPass = findViewById(R.id.signup_pass);
        signUp = findViewById(R.id.signup_btn);
        alreadyAccount = findViewById(R.id.already_account);
        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
    }
}