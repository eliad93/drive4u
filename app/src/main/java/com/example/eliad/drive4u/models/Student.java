package com.example.eliad.drive4u.models;

import java.util.Map;

public class Student extends User {
    private int numberOfLessons = 0;
    private Boolean hasTeacher = false;

    public Student(Map<String, Object> params) {
        super(params);
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
