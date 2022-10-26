package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toolbar;

import com.example.myapplication.helper.TabAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {
    androidx.appcompat.widget.Toolbar toolbar;
    AppBarLayout appBarLayout;
    TabLayout tabLayout;
    ViewPager viewPager;
    TabAdapter tabAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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


}