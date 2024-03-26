package com.example.projectandroid1;

public class Item {
    private String name;
    private int amount;
    private double price;

    private int image;

    public Item() {}
    public Item(String name, int amount, double price, int image) {
        this.name = name;
        this.amount = amount;
        this.price = price;
        this.image = image;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getImage() {
        return image;
    }
}
