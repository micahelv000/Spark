package com.example.projectandroid1;

import android.annotation.SuppressLint;
import android.location.Location;
import android.net.Uri;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class Post {
    private final String address;
    private final String epoch;
    private final String likes;
    private final String image;
    private final String userID;
    private final Location location;

    public Post(String address, String epoch, String likes, String image, Location location, String userID){
        this.address = address;
        this.epoch = epoch;
        this.likes = likes;
        this.image = image;
        this.location = location;
        this.userID = userID;
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public String toString() {
        return String.format("%s|%s|%s|%s|%s|%.6f|%.6f",
                address, epoch, likes, image, userID, location.getLatitude(),location.getLongitude());
    }

    // fromString method to parse a string and create a Post object
    public static Post fromString(String postString) {
        String[] parts = postString.split("\\|");
        String address = parts[0];
        String epoch = parts[1];
        String likes = parts[2];
        String image = parts[3];
        String userID = parts[4];
        double Lat = Double.parseDouble(parts[5]);
        double Long = Double.parseDouble(parts[6]);

        // Assuming the location string is in the format "latitude,longitude"
        //String location = parts[5];
        Location location = new Location("");
        location.setLatitude(Lat);
        location.setLongitude(Long);
        return new Post(address, epoch, likes, image, location, userID);
    }




    public String getAddress() {
        return address;
    }

    public String getEpoch() {
        return epoch;
    }

    public String getLikes() {
        return likes;
    }

    public String getImage() {
        return image;
    }

    public Location getLocation() {
        return location;
    }
    public String getuserID() {
        return userID;
    }

    public void setFullName(TextView textView){
        FireBaseHandler.getFullName(userID).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                textView.setText(String.format("\uD83D\uDC64 %s", task.getResult()));
            }
        });
    }
}
