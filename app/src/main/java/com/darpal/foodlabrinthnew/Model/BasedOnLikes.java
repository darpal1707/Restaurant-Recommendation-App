package com.darpal.foodlabrinthnew.Model;

public class BasedOnLikes {

    public int resImage;
    public String name;
    public String address;
    public String  review_count;

    public BasedOnLikes(int resImage, String resName, String resLocation, String resReviewCount) {
        this.resImage = resImage;
        this.name = resName;
        this.address = resLocation;
        this.review_count = resReviewCount;
    }

    public BasedOnLikes() {
    }

    public int getResImage() {
        return resImage;
    }

    public void setResImage(int resImage) {
        this.resImage = resImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getReview_count() {
        return review_count;
    }

    public void setReview_count(String review_count) {
        this.review_count = review_count;
    }
}
