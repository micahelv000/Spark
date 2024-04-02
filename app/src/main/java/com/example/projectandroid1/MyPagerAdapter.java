package com.example.projectandroid1;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.google.android.gms.maps.MapFragment;

public class MyPagerAdapter extends FragmentPagerAdapter {
    private static final int NUM_PAGES = 3; // Number of pages

    public MyPagerAdapter(@NonNull FragmentManager fragmentManager) {
        super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        // Return the fragment for each position
        switch (position) {
            case 0:
                return new ParkingMapsFragment();
            case 1:
                return new HomeFragment();
            case 2:
                //return new ProfileFragment();
                return new ProfileFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }


}