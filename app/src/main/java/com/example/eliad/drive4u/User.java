package com.example.eliad.drive4u;

import android.content.Intent;

import java.util.Map;

public abstract class User {
    protected String uId=null;
    protected String name=null;
    protected String phoneNumber=null;
    protected String email=null;

    protected User(Map<String, Object> params){
        uId = (String) params.get("id");
        name = (String)params.get("name");
        phoneNumber = (String)params.get("phone");
        email = (String)params.get("email");
    }

    public User(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
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
