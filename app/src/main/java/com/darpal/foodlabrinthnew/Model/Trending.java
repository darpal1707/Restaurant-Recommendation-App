package com.darpal.foodlabrinthnew.Model;

import java.util.ArrayList;

public class Trending {
    public int resImage;
    public String resName;
    public String resCuisine;
    public String resLocation;
    public String  resReviewCount;

    public Trending() {
    }


    public Trending(int resImage, String resName, String resCuisine, String resLocation, String  resReviewCount) {
        this.resImage = resImage;
        this.resName = resName;
        this.resCuisine = resCuisine;
        this.resLocation = resLocation;
        this.resReviewCount = resReviewCount;
    }


    public int getResImage() {
        return resImage;
    }

    public void setResImage(int resImage) {
        this.resImage = resImage;
    }

    public String getResName() {
        return resName;
    }

    public void setResName(String resName) {
        this.resName = resName;
    }

    public String getResCuisine() {
        return resCuisine;
    }

    public void setResCuisine(String resCuisine) {
        this.resCuisine = resCuisine;
    }

    public String getResLocation() {
        return resLocation;
    }

    public void setResLocation(String resLocation) {
        this.resLocation = resLocation;
    }

    public String  getResReviewCount() {
        return resReviewCount;
    }

    public void setResReviewCount(String  resReviewCount) {
        this.resReviewCount = resReviewCount;
    }
}
