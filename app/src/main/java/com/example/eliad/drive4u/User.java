package com.example.eliad.drive4u;

public abstract class User {
    private int uId;
    private UserInfo userInfo;

    /**
     *
     * @return the user name
     */
    public String name(){
        return userInfo.name();
    }
    public UserInfo getInfo(){
        return userInfo;
    }
}
