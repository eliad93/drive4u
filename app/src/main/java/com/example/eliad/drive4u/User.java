package com.example.eliad.drive4u;

import android.content.Intent;

import java.util.Map;

public abstract class User {
    protected String uId;
    protected String name;
    protected String phoneNumber;
    protected String email;

    protected User(Map<String, Object> params){
        uId = (String) params.get("id");
        name = (String)params.get("name");
        phoneNumber = (String)params.get("phone");
        email = (String)params.get("email");
    }
}
