package com.example.projectandroid1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;

public class LayoutFragments extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    ImageView Bhome, BMap, Bprofile;
    ImageView fabAddPark;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_fragments);
        int color = Color.parseColor("#0064D1");
        ColorStateList colorStateActive = ColorStateList.valueOf(color);
        ColorStateList colorStateInactive = ColorStateList.valueOf(Color.GRAY);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        Bhome = findViewById(R.id.B_Open_search);
        BMap = findViewById(R.id.B_OpenMap);
        Bprofile = findViewById(R.id.B_Profile);
        fabAddPark = findViewById(R.id.fabAddPark);
        // Create and set up the ViewPager adapter
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        // Set initial page to home fragment
        viewPager.setCurrentItem(1);

        // Link the TabLayout with the ViewPager
        tabLayout.setupWithViewPager(viewPager);

        fabAddPark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LayoutFragments.this, AddParking.class);
                startActivity(intent);
            }
        });
        // Set up click listeners for the buttons to mimic tab selection
        Bhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1); // Select the second tab
            }
        });


        BMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0); // Select the first tab
            }
        });

        Bprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(2); // Select the third tab
            }
        });


        // Set up a listener to handle tab selection and change button colors accordingly
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch (position) {
                    case 0:
                        Bhome.setImageTintList(colorStateInactive);
                        BMap.setImageTintList(colorStateActive);
                        Bprofile.setImageTintList(colorStateInactive);
                        break;
                    case 1:
                        Bhome.setImageTintList(colorStateActive);
                        BMap.setImageTintList(colorStateInactive);
                        Bprofile.setImageTintList(colorStateInactive);
                        break;
                    case 2:
                        Bhome.setImageTintList(colorStateInactive);
                        BMap.setImageTintList(colorStateInactive);
                        Bprofile.setImageTintList(colorStateActive);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // No action needed
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // No action needed
            }
        });

        // Set initial color
        Bhome.setImageTintList(colorStateActive);
        BMap.setImageTintList(colorStateInactive);
        Bprofile.setImageTintList(colorStateInactive);
    }
}