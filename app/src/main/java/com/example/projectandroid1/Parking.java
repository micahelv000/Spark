package com.example.projectandroid1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Parking extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);
    }
    public void B_AddParking(View view) {
        Intent intent = new Intent(this, AddParking.class);
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