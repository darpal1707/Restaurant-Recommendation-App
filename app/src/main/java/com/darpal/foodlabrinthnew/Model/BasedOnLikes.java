package com.darpal.foodlabrinthnew.Model;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;

public class BasedOnLikes {

    public String name;
    public String business_id;
    public String categories;
    public String address;
    public String review_count;
    public String city;
    public String state;


    public BasedOnLikes(String name, String address, String review_count, String city, String state, String cuisine) {
        this.name = name;
        this.address = address;
        this.review_count = review_count;
        this.city = city;
        this.state = state;
        this.categories = cuisine;
    }

    public String getBusiness_id() {
        return business_id;
    }

    public void setBusiness_id(String business_id) {
        this.business_id = business_id;
    }


    public BasedOnLikes() {
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
