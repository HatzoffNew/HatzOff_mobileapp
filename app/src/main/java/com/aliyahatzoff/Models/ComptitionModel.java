package com.aliyahatzoff.Models;

public class ComptitionModel {
    String interest;
    int price;
    int image;

    public ComptitionModel(String interest, int price, int image) {
        this.interest = interest;
        this.price = price;
        this.image = image;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
