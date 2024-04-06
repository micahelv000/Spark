package com.example.projectandroid1;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MyPagerAdapter extends FragmentStateAdapter {
    private static final int NUM_PAGES = 3;
    private final String userdata;

    public MyPagerAdapter(@NonNull FragmentActivity fragmentActivity, String userDataString) {
        super(fragmentActivity);
        userdata = userDataString;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return the fragment for each position
        Bundle bundle = new Bundle();
        bundle.putString("userData", userdata);

        switch (position) {
            // Pass user data to ProfileFragment
            case 0:
                ParkingMapsFragment parkingMapsFragment = new ParkingMapsFragment();
                parkingMapsFragment.setArguments(bundle);
                return parkingMapsFragment;
            case 1:
                HomeFragment homeFragment = new HomeFragment();
                homeFragment.setArguments(bundle);
                return homeFragment;
            case 2:
                ProfileFragment profileFragment = new ProfileFragment();
                profileFragment.setArguments(bundle);
                return profileFragment;
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}