package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.myapplication.java.Fragcovid;
import com.example.myapplication.java.Fragdust;
import com.example.myapplication.java.Fragweahter;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return Fragweahter.newInstatce();
            case 1:
                return Fragdust.newInstatce();
            case 2:
                return Fragcovid.newInstatce();
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
                return "날씨";
            case 1:
                return "미세먼지";
            case 2:
                return "코로나";
            default:
                return null;
        }
    }
}
