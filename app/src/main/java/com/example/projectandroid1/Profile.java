package com.example.projectandroid1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class Profile extends AppCompatActivity {
    ImageView b_Profile;
    private RecyclerView recyclerView;
    private CustomAdapter adapter;
    private LinearLayoutManager layoutManager;
    private ArrayList<Item> dataSet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Change the color of the current page button
        b_Profile = findViewById(R.id.B_Profile);
        int color = Color.parseColor("#0064D1");
        ColorStateList colorStateList = ColorStateList.valueOf(color);
        b_Profile.setImageTintList(colorStateList);

        dataSet = new ArrayList<>();
        recyclerView = findViewById(R.id.resView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        FloatingActionButton fabEditProfile = findViewById(R.id.fabEditProfile);
        fabEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the edit profile action here
                Intent intent = new Intent(Profile.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // Add items to the dataSet
        for (int i = 0; i < myData.nameArray.length; i++) {
            dataSet.add(new Item(
                    myData.nameArray[i],
                    Integer.parseInt(myData.amount[i]),
                    Double.parseDouble(myData.price[i]),
                    myData.drawableArray[i]
            ));
        }
        adapter = new CustomAdapter(dataSet, this);
        recyclerView.setAdapter(adapter);
    }

    public void B_AddParking(View view) {
        Intent intent = new Intent(this, AddParking.class);
        startActivity(intent);
    }
    public void B_Logout(View view) {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }
    public void B_Profile(View view) {
        Intent intent = new Intent(this, Profile.class);
        startActivity(intent);
    }
    public void B_Home(View view) {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }
}