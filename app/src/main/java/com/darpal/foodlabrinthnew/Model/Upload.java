package com.darpal.foodlabrinthnew.Model;

public class Upload {

    public Upload() {
        //empty constructor needed
    }

    String mImageUrl;
    String user_id;
    String business_id;

    public Upload(String mImageUrl, String user_id, String business_id) {
        this.mImageUrl = mImageUrl;
        this.user_id = user_id;
        this.business_id = business_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getBusiness_id() {
        return business_id;
    }

    public void setBusiness_id(String business_id) {
        this.business_id = business_id;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.mImageUrl = imageUrl;
    }
}
