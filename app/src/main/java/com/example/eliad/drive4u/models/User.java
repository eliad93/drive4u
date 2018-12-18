package com.example.eliad.drive4u.models;

import android.content.Intent;

import java.util.Map;

public abstract class User {
    protected String ID=null;
    protected String firstName=null;
    protected String lastName=null;
    protected String phoneNumber=null;
    protected String city=null;
    protected String email=null;
    protected int balance=0;

    protected User(String mId, String mFirstName,String mLastName, String mPhoneNumber,
                   String mCity, String mEmail){
        ID = mId;
        firstName = mFirstName;
        lastName = mLastName;
        phoneNumber = mPhoneNumber;
        city = mCity;
        email = mEmail;
    }

    public User(){}  // empty constructor for firebase

    // getters and setters for firebase

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public void changeBalanceBy(int diff) {
        balance += diff;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
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
