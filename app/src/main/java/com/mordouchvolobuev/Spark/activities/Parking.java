package com.mordouchvolobuev.Spark.activities;

import static com.mordouchvolobuev.Spark.models.Post.fromString;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mordouchvolobuev.Spark.firebase.FireBaseHandler;
import com.mordouchvolobuev.Spark.models.Post;
import com.mordouchvolobuev.Spark.R;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class Parking extends AppCompatActivity implements OnMapReadyCallback {

    private ImageView likeButton;
    private ImageView profileImage;
    private TextView username, info;
    private double latitude; // Example latitude (San Francisco)
    private double longitude; // Example longitude (San Francisco)
    private Post parkingInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);

        MapView mMapView = findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        likeButton = findViewById(R.id.B_Report);
        username = findViewById(R.id.Username);
        profileImage = findViewById(R.id.profileIMG);
        ImageView parkingIMG = findViewById(R.id.imageView3);
        info = findViewById(R.id.textView2);

        Intent intent = getIntent();
        String ParkingInfoString = intent.getStringExtra("Parking");
        assert ParkingInfoString != null;
        parkingInfo = fromString(ParkingInfoString);
        String userid = parkingInfo.getUserID();

        FireBaseHandler.getUserName(userid).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                username.setText(String.format("@%s", task.getResult()));
            }
        });
        FireBaseHandler.getProfilePic(userid).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String img = task.getResult();
                if (!Objects.equals(img, "")) {
                    Picasso.get().load(img).error(R.drawable.default_profile).placeholder(R.drawable.progress_animation)
                            .into(profileImage);
                }
            }
        });

        if (parkingInfo.getLikeStatus()) {
            likeButton.setColorFilter(Color.RED);
        } else {

            likeButton.setColorFilter(Color.BLACK);
        }

        // parking img
        if (parkingInfo.getImage() != null) {
            Picasso.get().load(parkingInfo.getImage()).error(R.drawable.default_parking)
                    .placeholder(R.drawable.progress_animation).into(parkingIMG);
        }

        latitude = parkingInfo.getLocation().getLatitude();
        longitude = parkingInfo.getLocation().getLongitude();

        updateInfo();

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

    public void B_Like(View view) {
        Animation rotate = AnimationUtils.loadAnimation(likeButton.getContext(), R.anim.hearbeat_anim);
        likeButton.startAnimation(rotate);

        String post_id = parkingInfo.getPostID();
        if (parkingInfo.getLikeStatus()) {
            likeButton.setColorFilter(Color.BLACK);
            FireBaseHandler fireBaseHandler = new FireBaseHandler();
            fireBaseHandler.unlikePost(post_id);
            parkingInfo.setTotalLikes(String.valueOf(Integer.parseInt(parkingInfo.getTotalLikes()) - 1));
            parkingInfo.setLikeStatus(false);
        } else {
            likeButton.setColorFilter(Color.RED);
            FireBaseHandler fireBaseHandler = new FireBaseHandler();
            fireBaseHandler.likePost(post_id);
            parkingInfo.setTotalLikes(String.valueOf(Integer.parseInt(parkingInfo.getTotalLikes()) + 1));
            parkingInfo.setLikeStatus(true);
        }

        updateInfo();

    }

    private void updateInfo() {
        String price_text;
        if (parkingInfo.getIsFree()) {
            price_text = "The parking is Free";
        } else {
            price_text = "The parking is Paid";
        }

        String[] parkingTypeArray = parkingInfo.getParkingType();
        String parkingTypes = "";

        if (parkingTypeArray != null) {
            parkingTypes = Arrays.stream(parkingTypeArray)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(", "));
        }

        String data = "\uD83D\uDCCD " + parkingInfo.getAddress() +
                "\n⏰ " + parkingInfo.getEpoch() +
                "\n♥ " + parkingInfo.getTotalLikes() + " Likes" +
                "\n\uD83D\uDD11 " + parkingTypes +
                "\n\uD83D\uDE98 Fit For: " + parkingInfo.getCarType() +
                "\n\uD83D\uDCB0 " + price_text;

        info.setText(data);
    }

    public void B_OpenNavi(View view) {
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