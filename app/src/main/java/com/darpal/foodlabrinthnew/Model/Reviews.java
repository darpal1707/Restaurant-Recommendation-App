package com.darpal.foodlabrinthnew.Model;

public class Reviews {

    String business_id;
    String date;
    String review_id;
    String text;
    String userid;
    String stars;

    public Reviews(String business_id, String date, String text, String userid, String stars) {
        this.business_id = business_id;
        this.date = date;
        this.text = text;
        this.userid = userid;
        this.stars = stars;
    }

    public String getStars() {
        return stars;
    }

    public void setStars(String stars) {
        this.stars = stars;
    }

    public String getBusiness_id() {
        return business_id;
    }

    public void setBusiness_id(String business_id) {
        this.business_id = business_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReview_id() {
        return review_id;
    }

    public void setReview_id(String review_id) {
        this.review_id = review_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
