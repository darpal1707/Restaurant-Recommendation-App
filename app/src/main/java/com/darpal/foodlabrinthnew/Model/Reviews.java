package com.darpal.foodlabrinthnew.Model;

public class Reviews {

    String busines_id;
    String date;
    String review_id;
    String text;
    String userid;

    public Reviews(String busines_id, String date, String text, String userid) {
        this.busines_id = busines_id;
        this.date = date;
        this.text = text;
        this.userid = userid;
    }

    public String getBusines_id() {
        return busines_id;
    }

    public void setBusines_id(String busines_id) {
        this.busines_id = busines_id;
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
