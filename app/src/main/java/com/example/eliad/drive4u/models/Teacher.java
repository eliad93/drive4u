package com.example.eliad.drive4u.models;

import java.util.HashMap;
import java.util.Map;

public class Teacher extends User {
    private HashMap<String, Student> students=null; // key is uId from firebase auth

    public Teacher(Map<String,Object> params) {
        super(params);
    }

    public Teacher(){
        super();
    }

    public void addStudent(String uId, Student student){
        assert student != null;
        if(!students.containsKey(uId)){
            students.put(uId, student);
        }
    }

    public Integer numberOfStudents(){
        return students.size();
    }

    public HashMap<String, Student> getStudents() {
        return students;
    }

    public void setStudents(HashMap<String, Student> students) {
        this.students = students;
    }
}
