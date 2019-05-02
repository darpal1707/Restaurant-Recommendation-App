package com.darpal.foodlabrinthnew.Model;

public class Upload {

    public Upload() {
        //empty constructor needed
    }

    private String mImageUrl;

    public Upload(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.mImageUrl = imageUrl;
    }
}
