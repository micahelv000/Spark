package com.mordouchvolobuev.Spark.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayout.Tab;
import com.mordouchvolobuev.Spark.adapters.MyPagerAdapter;
import com.mordouchvolobuev.Spark.R;

public class LayoutFragments extends AppCompatActivity {

    private ViewPager viewPager;
    private ImageView homeButton, mapButton, profileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_fragments);
        int color = Color.parseColor("#0064D1");
        ColorStateList colorStateActive = ColorStateList.valueOf(color);
        ColorStateList colorStateInactive = ColorStateList.valueOf(Color.GRAY);
        // Retrieve user data from intent
        Intent intent = getIntent();
        String userDataString = intent.getStringExtra("user");

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Toast.makeText(LayoutFragments.this, "Back button is disabled in this screen.", Toast.LENGTH_SHORT)
                        .show();
            }
        };

        // Add the callback to the onBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, callback);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        homeButton = findViewById(R.id.B_Open_search);
        mapButton = findViewById(R.id.B_OpenMap);
        profileButton = findViewById(R.id.B_Profile);
        ImageView fabAddPark = findViewById(R.id.fabAddPark);

        // Create and set up the ViewPager adapter
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager(), userDataString);
        viewPager.setAdapter(adapter);

        // Set initial page to home fragment
        viewPager.setCurrentItem(1);

        // Link the TabLayout with the ViewPager
        tabLayout.setupWithViewPager(viewPager);

        fabAddPark.setOnClickListener(v -> {
            Intent intent1 = new Intent(LayoutFragments.this, AddParking.class);
            intent1.putExtras(getIntent());
            startActivity(intent1);
        });

        homeButton.setOnClickListener(v -> {
            viewPager.setCurrentItem(1); // Select the second tab
        });

        mapButton.setOnClickListener(v -> {
            viewPager.setCurrentItem(0); // Select the first tab
        });

        profileButton.setOnClickListener(v -> {
            viewPager.setCurrentItem(2); // Select the third tab
        });

        // Set up a listener to handle tab selection and change button colors
        // accordingly
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(Tab tab) {
                int position = tab.getPosition();
                switch (position) {
                    case 0:
                        homeButton.setImageTintList(colorStateInactive);
                        mapButton.setImageTintList(colorStateActive);
                        profileButton.setImageTintList(colorStateInactive);
                        break;
                    case 1:
                        homeButton.setImageTintList(colorStateActive);
                        mapButton.setImageTintList(colorStateInactive);
                        profileButton.setImageTintList(colorStateInactive);
                        break;
                    case 2:
                        homeButton.setImageTintList(colorStateInactive);
                        mapButton.setImageTintList(colorStateInactive);
                        profileButton.setImageTintList(colorStateActive);
                        break;
                }
            }

            @Override
            public void onTabUnselected(Tab tab) {
                // No action needed
            }

            @Override
            public void onTabReselected(Tab tab) {
                // No action needed
            }
        });

        // Set initial color
        homeButton.setImageTintList(colorStateActive);
        mapButton.setImageTintList(colorStateInactive);
        profileButton.setImageTintList(colorStateInactive);
    }

}