package com.example.eliad.drive4u.models;

import android.content.Intent;

import java.util.Map;

public abstract class User {
    protected String ID=null;
    protected String name=null;
    protected String phoneNumber=null;
    protected String email=null;

    protected User(String mId, String mName, String mPhoneNumber, String mEmail){
        ID = mId;
        name = mName;
        phoneNumber = mPhoneNumber;
        email = mEmail;
    }

    public User(){}  // empty constructor for firebase

    // getters and setters for firebase
    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
