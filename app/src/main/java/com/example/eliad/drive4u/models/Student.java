package com.example.eliad.drive4u.models;

import java.util.Map;

public class Student extends User {
    private int numberOfLessons = 0; // TODO: manage this number with more details
    private Boolean hasTeacher = false;
    private Teacher teacher = null;

    public Student(String mId, String mFirstName, String mLastName, String mPhoneNumber,
                   String mEmail, String mCity) {
        super(mId, mFirstName, mLastName, mPhoneNumber, mEmail, mCity);
    }

    public int getNumberOfLessons() {
        return numberOfLessons;
    }

    public void setNumberOfLessons(int numberOfLessons) {
        this.numberOfLessons = numberOfLessons;
    }

    public Boolean getHasTeacher() {
        return hasTeacher;
    }

    public void setHasTeacher(Boolean hasTeacher) {
        this.hasTeacher = hasTeacher;
    }

    public int getBalance() { return 0; /*TODO: manage the balance*/}
}
