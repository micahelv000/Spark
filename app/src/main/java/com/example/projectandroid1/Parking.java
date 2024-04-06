package com.example.projectandroid1;

import static com.example.projectandroid1.Post.fromString;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

public class Parking extends AppCompatActivity implements OnMapReadyCallback {

    private ImageView Rep,empty,takeIt,profileIMG,ParkingIMG;
    private TextView Username ,info;
    private double latitude; // Example latitude (San Francisco)
    private double longitude ; // Example longitude (San Francisco)
    private String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);

        MapView mMapView = findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        Rep = findViewById(R.id.B_Report);
        empty = findViewById(R.id.B_Update);
        takeIt = findViewById(R.id.B_TakeTheParking);
        Username = findViewById(R.id.Username);
        profileIMG = findViewById(R.id.profileIMG);
        ParkingIMG = findViewById(R.id.imageView3);
        info = findViewById(R.id.textView2);

        Intent intent = getIntent();
        String ParkingInfoString = intent.getStringExtra("Parking");
        assert ParkingInfoString != null;
        Post ParkingInfo = fromString(ParkingInfoString);
        String userid = ParkingInfo.getuserID();


        FireBaseHandler.getUserName(userid).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Username.setText(String.format("@%s", task.getResult()));
            }
        });
        FireBaseHandler.getProfilePic(userid).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String img = task.getResult();

                Picasso.get().load(img).error(R.drawable.default_profile).placeholder(R.drawable.progress_animation).into(profileIMG);

            }
        });

        //parking img
        if(ParkingInfo.getImage()!=null) {
            Picasso.get().load(ParkingInfo.getImage()).error(R.drawable.default_profile).placeholder(R.drawable.progress_animation).into(ParkingIMG);
        }
        // Profile img

        latitude = ParkingInfo.getLocation().getLatitude();
        longitude = ParkingInfo.getLocation().getLongitude();


        String Data = "\n The address is:\n"+ParkingInfo.getAddress()+"\nUploaded at:\n "+ParkingInfo.getEpoch()+"\nThe parking has:\n "+ParkingInfo.getTotalLikes()+" Likes";

        info.setText(Data);



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