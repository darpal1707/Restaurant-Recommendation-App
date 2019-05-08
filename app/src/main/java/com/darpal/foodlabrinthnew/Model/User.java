package com.darpal.foodlabrinthnew.Model;

public class User {

    public String password;
    String userid;
    public String email;
    public String fullname;
    String viewedCuisine;

    public User(String userid, String password, String email, String fullname) {
        this.password = password;
        this.userid = userid;
        this.email = email;
        this.fullname = fullname;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}