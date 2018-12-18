package com.example.eliad.drive4u.models;

import java.util.Map;

public class Student extends User {
    private int numberOfLessons = 0; // TODO: manage this number with more details
    private String teacherId = null;
    private Integer totalExpence = 0;

    public Student(String mId, String mFirstName, String mLastName, String mPhoneNumber,
                    String mCity, String mEmail) {
        super(mId, mFirstName, mLastName, mPhoneNumber, mCity, mEmail);
    }

    public int getNumberOfLessons() {
        return numberOfLessons;
    }

    public void setNumberOfLessons(int numberOfLessons) {
        this.numberOfLessons = numberOfLessons;
    }

    public int getBalance() { return 0; /*TODO: manage the balance*/}

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public Integer getTotalExpence() {
        return totalExpence;
    }

    public void setTotalExpence(Integer totalExpence) {
        this.totalExpence = totalExpence;
    }
}
