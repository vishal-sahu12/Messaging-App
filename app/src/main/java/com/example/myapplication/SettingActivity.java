package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageActivity;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageView;
import com.example.myapplication.LoginSignUp.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {
    EditText username,userstatus;
    Button update;
    CircleImageView profileImage;
    FirebaseAuth auth;
    String image;
    DatabaseReference RootRef;
    public static final int Gallery_code=1;
    StorageReference UserProfileImg,storageReference;
    String currentUser;
    ProgressDialog progressDialog;
    String timeUploaded,valid;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        auth=FirebaseAuth.getInstance();
        currentUser=auth.getCurrentUser().getUid();
        progressDialog=new ProgressDialog(this);
        RootRef= FirebaseDatabase.getInstance().getReference();
        UserProfileImg= FirebaseStorage.getInstance().getReference().child("Profile Images");
        storageReference=FirebaseStorage.getInstance().getReference().child("Profile Images/"+currentUser+".jpg");
        Initialize();
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateSettings();
                if(!TextUtils.isEmpty(username.getText().toString()) &&
                        !TextUtils.isEmpty(userstatus.getText().toString())) {
                    SendUserToMainActivity();
                }

            }
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(username.getText().toString()) &&
                        !TextUtils.isEmpty(userstatus.getText().toString())) {
                    UpdateSettings();
                    Intent intent=new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent,"Select Image from here"),
                            Gallery_code);
                }
                else
                {
                    username.setError("please enter user name");
                    userstatus.setError("please enter user status");
                }
            }
        });
        RetriveData();
    }

    private void RetriveData() {
        RootRef.child("Users").child(currentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    if (snapshot.hasChild("name")&& snapshot.hasChild("status")&& !snapshot.hasChild("image"))
                    {
                        username.setVisibility(View.VISIBLE);
                        String uname=snapshot.child("name").getValue().toString();
                        String ustatus=snapshot.child("status").getValue().toString();
                        username.setText(uname);
                        userstatus.setText(ustatus);
                        if (snapshot.hasChild("timeUploaded") && snapshot.hasChild("valid"))
                        {
                            timeUploaded=snapshot.child("timeUploaded").getValue().toString();
                            valid=snapshot.child("valid").getValue().toString();
                        }
                    }
                    else if (snapshot.hasChild("name")&& snapshot.hasChild("status")&& snapshot.hasChild("image"))
                    {
                        username.setVisibility(View.VISIBLE);
                        image=snapshot.child("image").getValue().toString();
                        String uname=snapshot.child("name").getValue().toString();
                        String ustatus=snapshot.child("status").getValue().toString();
                        GetImage();
                        username.setText(uname);
                        userstatus.setText(ustatus);
                        if (snapshot.hasChild("timeUploaded") && snapshot.hasChild("valid"))
                        {
                            timeUploaded=snapshot.child("timeUploaded").getValue().toString();
                            valid=snapshot.child("valid").getValue().toString();
                        }
                    }
                    else
                    {
                        username.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(), "Please Update your Profile", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    username.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "Please Update your Profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void UpdateSettings() {
        String name=username.getText().toString();
        String status=userstatus.getText().toString();
        if(TextUtils.isEmpty(name))
        {
            username.setError("please enter user name");

        }
        if(TextUtils.isEmpty(status))
        {
            userstatus.setError("please enter user status");

        }
        if(TextUtils.isEmpty(name) && TextUtils.isEmpty(status))
        {
            username.setError("please enter user name");
            userstatus.setError("please enter user status");

        }
        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(status)) {
            HashMap<String , String> profileMap = new HashMap<>();
            profileMap.put("uid",currentUser);
            profileMap.put("name",name);
            profileMap.put("status",status);
            if (image!=null )
            {
                profileMap.put("image",image);
            }
            if(!TextUtils.isEmpty(timeUploaded) && !TextUtils.isEmpty(valid))
            {
                profileMap.put("timeUploaded",timeUploaded);
                profileMap.put("valid",valid);

            }
            RootRef.child("Users").child(currentUser).setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(getApplicationContext(), "Data Updated", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        String message=task.getException().getLocalizedMessage();
                        Toast.makeText(getApplicationContext(), "Error : "+message, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }
    private void SendUserToMainActivity() {
        Intent intent =new Intent(SettingActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void Initialize() {
        username=findViewById(R.id.username);
        userstatus=findViewById(R.id.status);
        update=findViewById(R.id.update_btn);
        profileImage=findViewById(R.id.circle_profile);
        toolbar=findViewById(R.id.app_bar_setting);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Account Settings");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==Gallery_code && resultCode==RESULT_OK && data!=null) {
            profileImage.setImageURI(data.getData());
        }

        if (resultCode == RESULT_OK) {

            if (requestCode == Gallery_code)// And cropImage request code
            {
                progressDialog.setTitle("Set Profile Image");
                progressDialog.setMessage("Please wait, while we are updating Your account..");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                // Getting Image and convert them into URI form.
                Uri resultUri = data.getData();
                //Uri resultUri = Uri.parse("https://www.google.com/url?sa=i&url=https%3A%2F%2Fwallpaperaccess.com%2Fcool-profile-pictures&psig=AOvVaw2mSIunng7_EJsUbc5hvFgY&ust=1668604923435000&source=images&cd=vfe&ved=0CBAQjRxqFwoTCNDO0_CjsPsCFQAAAAAdAAAAABAE");
                if (null != resultUri) {
                    StorageReference filePath = UserProfileImg.child(currentUser + ".jpg");
                    filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.cancel();
                            GetImage();
                            RootRef.child("Users").child(currentUser).child("image").setValue(currentUser);
                            Toast.makeText(getApplicationContext(), "Profile Image Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }

        }
    }
    private void GetImage() {
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri).into(profileImage);
            }
        });
    }

}

  /*  EditText userName, status;
    CircleImageView userProfile;
    Button update;
    FirebaseAuth auth;
    FirebaseUser user;
    String image, timeUploaded, valid;
    DatabaseReference Rootref;
    public static final int Gallery_Code = 1;
    StorageReference UserProfileImage, storageReference;
    ProgressDialog progressDialog;
    Toolbar toolbar;
    String currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        auth = FirebaseAuth.getInstance();
        user= FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        currentUser=user.getUid();
        Rootref = FirebaseDatabase.getInstance().getReference();

        UserProfileImage = FirebaseStorage.getInstance().getReference().child("Profile Pictures");
        storageReference = FirebaseStorage.getInstance().getReference().child(" Profile Images/" + currentUser + ".jpg");
        progressDialog = new ProgressDialog(this);
        if(currentUser==null){
            Toast.makeText(this, "CurrentUser Is Null", Toast.LENGTH_SHORT).show();
        }
         Initialize();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(userName.getText().toString()) && !TextUtils.isEmpty(status.getText().toString())) {

                    sendUserToMainActivity();

                }
            }
        });

        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(userName.getText().toString()) && !TextUtils.isEmpty(status.getText().toString())) {
                    UpdateSettings();
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Image From Here"), Gallery_Code);


                } else {

                    userName.setError("Please Enter your userName");
                    status.setError("Please Enter your Current Status");

                }
            }
        });

        RetriveData();

    }


    // 1 function();
    @SuppressLint("RestrictedApi")
    private void Initialize() {
        userName = findViewById(R.id.username);
        status = findViewById(R.id.status);
        update = findViewById(R.id.update_btn);
        toolbar = findViewById(R.id.app_bar_setting);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Account Setting");
        userProfile = findViewById(R.id.circle_profile);

    }

    // 2 function();
    private void sendUserToMainActivity() {
        Intent intent = new Intent(SettingActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == Gallery_Code)// And cropImage request code
            {
                progressDialog.setTitle("Set Profile Image");
                progressDialog.setMessage("Please wait, while we are updating Your account..");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                // Getting Image and convert them into URI form.
                Uri resultUri = data.getData();
                //Uri resultUri = Uri.parse("https://www.google.com/url?sa=i&url=https%3A%2F%2Fwallpaperaccess.com%2Fcool-profile-pictures&psig=AOvVaw2mSIunng7_EJsUbc5hvFgY&ust=1668604923435000&source=images&cd=vfe&ved=0CBAQjRxqFwoTCNDO0_CjsPsCFQAAAAAdAAAAABAE");
                if (null != resultUri) {
                    StorageReference filePath = UserProfileImage.child(currentUser + ".jpg");
                    filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.cancel();
                            GetImage();
                            Rootref.child("Users").child(currentUser).child("image").setValue(currentUser);
                            Toast.makeText(getApplicationContext(), "Profile Image Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }

        }
    }
    private void GetImage() {
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri).into(userProfile);
            }
        });
    }

    private void RetriveData() {
        Rootref.child("Users").child(currentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.hasChild("name") && snapshot.hasChild("status") && !snapshot.hasChild("image")) {
                        userName.setVisibility(View.GONE);
                        String uname = snapshot.child("name").getValue().toString();
                        String ustatuss = snapshot.child("status").getValue().toString();
                        userName.setText(uname);
                        status.setText(ustatuss);
                        if (snapshot.hasChild("timeUploaded") && snapshot.hasChild("valid")) {
                            timeUploaded = snapshot.child("timeUploaded").getValue().toString();
                            valid = snapshot.child("valid").getValue().toString();
                        }
                    } else if (snapshot.hasChild("name") && snapshot.hasChild("status") && snapshot.hasChild("image")) {
                        userName.setVisibility(View.GONE);
                        image = snapshot.child("image").getValue().toString();
                        String uname = snapshot.child("name").getValue().toString();
                        String ustatus = snapshot.child("status").getValue().toString();
                        GetImage();
                        userName.setText(uname);
                        status.setText(ustatus);
                        if (snapshot.hasChild("timeUploaded") && snapshot.hasChild("valid")) {
                            timeUploaded = snapshot.child("timeUploaded").getValue().toString();
                            valid = snapshot.child("valid").getValue().toString();
                        }
                    } else {
                        userName.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(), "Please Update Your Profile", Toast.LENGTH_SHORT).show();


                    }
                } else {
                    userName.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "Please Update Your Profile", Toast.LENGTH_SHORT).show();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void UpdateSettings() {
        String name=userName.getText().toString();
        String ustatus=status.getText().toString();
        if(TextUtils.isEmpty(name))
        {
            userName.setError("please enter user name");

        }
        if(TextUtils.isEmpty(ustatus))
        {
            status.setError("please enter user status");

        }
        if(TextUtils.isEmpty(name) && TextUtils.isEmpty(ustatus))
        {
            userName.setError("please enter user name");
            status.setError("please enter user status");

        }
        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(ustatus)) {
            HashMap<String , String> profileMap = new HashMap<>();
            profileMap.put("uid",currentUser);
            profileMap.put("name",name);
            profileMap.put("status",ustatus);
            if (image!=null )
            {
                profileMap.put("image",image);
            }
            if(!TextUtils.isEmpty(timeUploaded) && !TextUtils.isEmpty(valid))
            {
                profileMap.put("timeUploaded",timeUploaded);
                profileMap.put("valid",valid);

            }
            Rootref.child("Users").child(currentUser).setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(getApplicationContext(), "Data Updated", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        String message=task.getException().getLocalizedMessage();
                        Toast.makeText(getApplicationContext(), "Error : "+message, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }
}*/