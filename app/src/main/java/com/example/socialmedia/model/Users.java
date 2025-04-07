package com.example.socialmedia.model;

public class Users {
    private String email, uid, name, profile, password,bio;
    public Users(){}

    public Users(String email, String uid, String name, String profile, String password,String bio) {
        this.email = email;
        this.uid = uid;
        this.name = name;
        this.profile = profile;
        this.password = password;
        this.bio=bio;

    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}