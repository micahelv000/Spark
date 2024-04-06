package com.example.projectandroid1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Parking extends AppCompatActivity implements OnMapReadyCallback {

    private ImageView Rep,empty,takeIt;
    private final double latitude = 37.7749; // Example latitude (San Francisco)
    private final double longitude = -122.4194; // Example longitude (San Francisco)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);

        MapView mMapView = findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        Rep = findViewById(R.id.B_Report);
        empty =findViewById(R.id.B_Update);
        takeIt = findViewById(R.id.B_TakeTheParking);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        // Add a marker at the specified location and move the camera
        LatLng location = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions().position(location).title("Your Parking Location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15)); // Zoom level: 1-20 (higher is closer)

        // Disable all gestures
        googleMap.getUiSettings().setAllGesturesEnabled(false);
    }


    public void B_Report(View view){
        Animation rotate = AnimationUtils.loadAnimation(Rep.getContext(), R.anim.hearbeat_anim);
        Rep.startAnimation(rotate);
        AlertDialog.Builder builder = new AlertDialog.Builder(Rep.getContext());
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to Report that the parking is taken?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            //update status

        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    public void B_Update(View view){
        Animation rotate = AnimationUtils.loadAnimation(empty.getContext(), R.anim.hearbeat_anim);
        takeIt.startAnimation(rotate);
        AlertDialog.Builder builder = new AlertDialog.Builder(empty.getContext());
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure the parking is still available?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            //update status

        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    public void B_TakeParking(View view){
        Animation rotate = AnimationUtils.loadAnimation(takeIt.getContext(), R.anim.hearbeat_anim);
        takeIt.startAnimation(rotate);
        AlertDialog.Builder builder = new AlertDialog.Builder(takeIt.getContext());
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you took the parking?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            //update status

        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();

    }
    public void B_OpenNavi(View view){
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
        // Create an Intent with the action VIEW and the Uri as data
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        // Check if there is an activity available to handle the Intent
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(this, "No navigation app available", Toast.LENGTH_SHORT).show();
        }


    }




}