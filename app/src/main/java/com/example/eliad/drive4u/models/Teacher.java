package com.example.eliad.drive4u.models;

import java.util.Map;

public class Teacher extends User {
    private Integer numberOfStudents = 0;

    public Teacher(Map<String,Object> params) {
        super(params);
    }

    public Teacher(){
        super();
    }

    public Integer getNumberOfStudents() {
        return numberOfStudents;
    }

    public void setNumberOfStudents(Integer numberOfStudents) {
        this.numberOfStudents = numberOfStudents;
    }
}
