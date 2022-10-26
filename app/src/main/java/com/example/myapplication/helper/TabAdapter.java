package com.example.myapplication.helper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.myapplication.Fragment.ChatFragment;
import com.example.myapplication.Fragment.RequestFragment;
import com.example.myapplication.Fragment.StatusFragment;

public class TabAdapter extends FragmentPagerAdapter {
    public TabAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }




    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                ChatFragment chatFragment = new ChatFragment();
                return chatFragment;
            case 1:
                RequestFragment requestFragment = new RequestFragment();
                return requestFragment;
            case 2:
                StatusFragment statusFragment = new StatusFragment();
                return statusFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position){
            case 0:
                return "Chats";
            case 1:
                return "Request";
            case 2:
                return "Status";
            default:
                return null;
        }
    }
}
