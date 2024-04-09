package com.example.projectandroid1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        ImageView logo = findViewById(R.id.logo);
        TextView line = findViewById(R.id.Line);
        String[] lines = {
                "Drivers spend an average of 17 hours a year searching for parking spots\n",
                "On average, an individual in a big city spends 18-20 minutes searching for a safe parking space\n",
                "The average driver will spend approximately 2,000+ hours of their life trying to find a parking spot\n",
                "Approximately 95% of a car's time is spent parked, highlighting the importance of the parking industry\n",
                "It is recommended that you drive no faster than 5 mph to 10 mph (8 - 16 kmh) in parking lots\n",
                "Searching for Parking Costs Americans $73 Billion a Year\n",
                "Search smarter not harder!\n"

        };

        for (int i = 0; i < lines.length; i++) {
            lines[i] = "\"" + lines[i].trim() + "\""; // Add quotation marks and trim whitespace
        }

        Random rd = new Random();
        int random = rd.nextInt(lines.length);
        line.setText(lines[random]);
        Animation rotate = AnimationUtils.loadAnimation(this, R.anim.fadein);
        logo.startAnimation(rotate);
        line.startAnimation(rotate);
        FireBaseHandler fireBaseHandler = new FireBaseHandler();
        if (FireBaseHandler.getCurrentUser() != null) {
            FirebaseUser user = FireBaseHandler.getCurrentUser();
            Intent intent = new Intent(this, LayoutFragments.class);
            fireBaseHandler.getUserData(user).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    JSONObject userJson = task.getResult();
                    intent.putExtra("user", userJson.toString());
                    startActivity(intent);
                }
            });
        } else {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                startActivity(new Intent(MainActivity.this, Login.class));
                finish();
            }, 3000);
        }

    }

}
