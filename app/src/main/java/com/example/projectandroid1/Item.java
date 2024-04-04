package com.example.projectandroid1;

public class Item {
    private String name;
    private String epoch;
    private String likes;

    private int image;

    public Item() {}
    public Item(String address, String epoch, String likes, int image) {
        this.name = address;
        this.epoch = epoch;
        this.likes = likes;
        this.image = image;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEpoch() {
        return epoch;
    }

    public String getLikes() {
        return likes;
    }

    public int getImage() {
        return image;
    }
}
