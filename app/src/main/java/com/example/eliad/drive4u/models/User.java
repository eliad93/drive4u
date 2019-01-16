package com.example.eliad.drive4u.models;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class User implements Parcelable{
    protected String id = null;
    protected String firstName = null;
    protected String lastName = null;
    protected String phoneNumber = null;
    protected String city = null;
    protected String email = null;
    protected Integer balance = 0;

    protected User(String mId, String mFirstName,String mLastName, String mPhoneNumber,
                   String mCity, String mEmail){
        id = mId;
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

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public void changeBalanceBy(Integer diff) {
        balance += diff;
    }

    public String getID() {
        return id;
    }

    public void setID(String ID) {
        this.id = ID;
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

    public abstract String getClassRoom();

    // Parcelling part
    protected User(Parcel in){
        this.id = in.readString();
        this.firstName = in.readString();
        this.lastName =  in.readString();
        this.phoneNumber = in.readString();
        this.city = in.readString();
        this.email =  in.readString();
        this.balance =  in.readInt();
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(phoneNumber);
        dest.writeString(city);
        dest.writeString(email);
        dest.writeInt(balance);
    }


}
