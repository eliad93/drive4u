package com.example.eliad.drive4u.models;

import java.util.Map;

public class Student extends User {
    private int numberOfLessons = 0;
    private Boolean hasTeacher = false;

    public Student(String mId, String mName, String mPhoneNumber, String mEmail) {
        super(mId, mName, mPhoneNumber, mEmail);
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
}
