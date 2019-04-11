package com.darpal.foodlabrinthnew.Model;

import java.util.ArrayList;

public class Trending {

    public static String business_id;
    public static String name;
    public static String address;
    public static String review_count;
    public static String city;
    public static String state;
    public static String latitude;
    public static String longitude;

    public Trending() {
    }


    public Trending(String name, String address, String review_count, String city, String state, String latitude, String longitude, String business_id) {
        this.name = name;
        this.address = address;
        this.review_count = review_count;
        this.city = city;
        this.state = state;
        this.latitude = latitude;
        this.longitude = longitude;
        this.business_id = business_id;
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

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getBusiness_id() {
        return business_id;
    }

    public void setBusiness_id(String business_id) {
        this.business_id = business_id;
    }
}
