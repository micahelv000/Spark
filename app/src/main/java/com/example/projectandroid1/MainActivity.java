package com.example.projectandroid1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        ImageView logo = findViewById(R.id.logo);
        Animation rotate = AnimationUtils.loadAnimation(this, R.anim.fadein);
        logo.startAnimation(rotate);

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
