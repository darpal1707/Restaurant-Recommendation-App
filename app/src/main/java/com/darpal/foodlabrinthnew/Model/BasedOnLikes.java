package com.darpal.foodlabrinthnew.Model;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;

public class BasedOnLikes {

    public String name;
    public String address;
    public String  review_count;


    public BasedOnLikes(String name, String address, String review_count) {
        this.name = name;
        this.address = address;
        this.review_count = review_count;

    }

    public BasedOnLikes() {
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
