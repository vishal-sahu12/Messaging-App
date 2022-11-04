package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toolbar;

import com.example.myapplication.LoginSignUp.LoginActivity;
import com.example.myapplication.LoginSignUp.RegisterActivity;
import com.example.myapplication.helper.TabAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    androidx.appcompat.widget.Toolbar toolbar;
    AppBarLayout appBarLayout;
    TabLayout tabLayout;
    ViewPager viewPager;
    TabAdapter tabAdapter;
    FirebaseAuth auth;
    DatabaseReference Rootref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Rootref = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.main_id_toolbar);
        appBarLayout = findViewById(R.id.appbarlayout);
        viewPager = findViewById(R.id.viewpage);
        tabLayout = findViewById(R.id.tablayout);
        tabAdapter = new TabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabAdapter);
        setSupportActionBar(toolbar);
        tabLayout.setupWithViewPager(viewPager);
        getSupportActionBar().setTitle("WhatsApp");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.option_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         super.onOptionsItemSelected(item);
         if (item.getItemId()==R.id.logout_menu);
        {
            auth.signOut();
            sendUserToLogin();
        }
        if (item.getItemId()==R.id.setting_menu);
        {
            sendUserToSetting();
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentuser = auth.getCurrentUser();
        if (currentuser==null){
            auth.signOut();
            sendUserToLogin();
        }
    }
    private void sendUserToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    private void sendUserToSetting() {
        Intent intent = new Intent(MainActivity.this, SettingActivity.class);
        startActivity(intent);

    }
}