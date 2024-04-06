package com.example.projectandroid1;

import android.graphics.Bitmap;
import android.net.Uri;

public class Post {
    private String address;
    private String epoch;
    private String likes;

    private String image;

    public Post() {}
    public Post(String address, String epoch, String likes, String image) {
        this.address = address;
        this.epoch = epoch;
        this.likes = likes;
        this.image = image;
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
}
