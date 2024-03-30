package com.example.projectandroid1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Loading_Map extends AppCompatActivity {
    private ImageView logo;
    private Animation rotate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_map);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }


        logo = findViewById(R.id.logo);

        //MediaPlayer mediaPlayer = MediaPlayer.create(this, R.drawable.logo);
        //mediaPlayer.start();

        rotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
        logo.startAnimation(rotate);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Loading_Map.this, ParkingMaps.class));
                finish();
            }
        }, 1000);
    }
}