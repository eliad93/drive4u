package com.example.eliad.drive4u;

import java.util.Map;

class Student extends User {
    private int numberOfLessons=0;
    Boolean hasTeacher=false;

    Student(Map<String, Object> params) {
        super(params);
    }
}
