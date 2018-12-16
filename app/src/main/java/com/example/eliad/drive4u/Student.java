package com.example.eliad.drive4u;

import java.util.Map;

class Student extends User {
    private int numberOfLessons = 0;
    private Boolean hasTeacher = false;

    Student(Map<String, Object> params) {
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
