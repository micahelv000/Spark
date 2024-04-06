package com.example.projectandroid1;

import android.location.Location;
import android.widget.TextView;

public class Post {
    private String address;
    private String epoch;
    private String likes;
    private String image;
    private String userID;
    private Location location;

    public Post() {}
    public Post(String address, String epoch, String likes, String image, Location location, String userID){
        this.address = address;
        this.epoch = epoch;
        this.likes = likes;
        this.image = image;
        this.location = location;
        this.userID = userID;
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

    public String getUserID(){ return userID; }

    public Location getLocation() {
        return location;
    }

    public void setFullName(TextView textView){
        FireBaseHandler.getFullName(userID).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                textView.setText(String.format("\uD83D\uDC64 %s", task.getResult()));
            }
        });
    }
}
