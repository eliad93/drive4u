package com.base.eliad.drive4u.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.Nullable;


public abstract class User implements Parcelable{

    // a key that signs no image was set yet
    public static final String DEFAULT_IMAGE_KEY = "default_image_key";

    // user images are located in this firebase storage reference
    public static final String UPLOADS = "uploads";

    public static final String ONLINE = "online";
    public static final String OFFLINE = "offline";

    protected String id = null;
    protected String firstName = null;
    protected String lastName = null;
    protected String phoneNumber = null;
    protected String city = null;
    protected String email = null;
    protected int balance = 0;
    protected String imageUrl = null;
    protected String status = null;

    protected User(String mId, String mFirstName,String mLastName, String mPhoneNumber,
                   String mCity, String mEmail, String mImageUrl, String mStatus){
        id = mId;
        firstName = mFirstName;
        lastName = mLastName;
        phoneNumber = mPhoneNumber;
        city = mCity;
        email = mEmail;
        imageUrl = mImageUrl;
        status = mStatus;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public abstract String getClassRoom();

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    // Parcelling part
    protected User(Parcel in){
        this.id = in.readString();
        this.firstName = in.readString();
        this.lastName =  in.readString();
        this.phoneNumber = in.readString();
        this.city = in.readString();
        this.email =  in.readString();
        this.balance =  in.readInt();
        this.imageUrl = in.readString();
        this.status = in.readString();
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
        dest.writeString(imageUrl);
        dest.writeString(status);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (super.equals(obj)) {
            // this == obj
            return true;
        }

        if (!(obj instanceof User)) {
            // not a subclass of a user.
            return false;
        }
        User u = (User) obj;
        return this.getID().equals(u.getID());
    }

    public String getFullName() {
        if(firstName == null){
            return lastName != null ? lastName : "";
        }
        if(lastName == null){
            return firstName;
        }
        return getFirstName() + " " + getLastName();
    }
}
