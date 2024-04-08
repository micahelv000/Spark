package com.example.projectandroid1;

import android.annotation.SuppressLint;
import android.location.Location;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.Objects;

public class Post {
    private final String address;
    private final String epoch;
    private String totalLikes;
    private final String image;
    private final String userID;
    private final String postID;
    private final Location location;
    private boolean likeStatus;
    private final boolean isFree;
    private final String carType;
    private final String[] parkingType;

    public Post(String address, String epoch, String totalLikes, String image, Location location, String userID,
            String postID, boolean likeStatus, String carType, boolean isFree, String[] parkingType) {
        this.address = address;
        this.epoch = epoch;
        this.totalLikes = totalLikes;
        this.image = image;
        this.location = location;
        this.userID = userID;
        this.postID = postID;
        this.likeStatus = likeStatus;
        this.isFree = isFree;
        this.carType = carType;
        this.parkingType = parkingType;
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public String toString() {
        return String.format("%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%.6f|%.6f",
                address, epoch, totalLikes, image, userID, postID, likeStatus, carType, isFree,
                Arrays.toString(parkingType), location.getLatitude(), location.getLongitude());
    }

    // fromString method to parse a string and create a Post object
    public static Post fromString(String postString) {
        String[] parts = postString.split("\\|");
        String address = parts[0];
        String epoch = parts[1];
        String likes = parts[2];
        String image = parts[3];
        String userID = parts[4];
        String postID = parts[5];
        boolean likeStatus = Boolean.parseBoolean(parts[6]);
        String carType = parts[7];
        boolean isFree = Boolean.parseBoolean(parts[8]);
        String[] parkingType;
        if (!Objects.equals(parts[9], "null")) {
            parkingType = Arrays.stream(parts[9].substring(1, parts[9].length() - 1).split(", "))
                    .map(s -> "null".equals(s) ? null : s)
                    .toArray(String[]::new);
        } else {
            parkingType = null;
        }
        double Lat = Double.parseDouble(parts[10]);
        double Long = Double.parseDouble(parts[11]);

        // Assuming the location string is in the format "latitude,longitude"
        // String location = parts[5];
        Location location = new Location("");
        location.setLatitude(Lat);
        location.setLongitude(Long);
        return new Post(address, epoch, likes, image, location, userID, postID, likeStatus, carType, isFree,
                parkingType);
    }

    public String getAddress() {
        return address;
    }

    public String getEpoch() {
        return epoch;
    }

    public String getTotalLikes() {
        return totalLikes;
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

    public void setFullName(TextView textView) {
        FireBaseHandler.getFullName(userID).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                textView.setText(String.format("\uD83D\uDC64 %s", task.getResult()));
            }
        });
    }

    public String getPostID() {
        return postID;
    }

    public boolean getLikeStatus() {
        return likeStatus;
    }

    public void setLikeStatus(boolean status) {
        this.likeStatus = status;
    }

    public void setTotalLikes(String s) {
        this.totalLikes = s;
    }

    public String getCarType() {
        return carType;
    }

    public String[] getParkingType() {
        return parkingType;
    }

    public boolean getIsFree() {
        return isFree;
    }
}
