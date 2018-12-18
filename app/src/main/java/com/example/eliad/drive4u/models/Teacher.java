package com.example.eliad.drive4u.models;

import java.util.HashMap;
import java.util.Map;

public class Teacher extends User {
    private HashMap<String, Student> students = null; // key is uId from firebase auth
    private Integer price=100;
    private Integer totalPayed = 0;
    private String carModel = null;
    private String gearType = null;


    public Teacher(String mId, String mFirstName, String mLastName, String mPhoneNumber,
                   String mEmail, String mCity, String mGearType) {
        super(mId, mFirstName, mLastName, mPhoneNumber, mEmail, mCity);
        gearType = mGearType;
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

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getTotalPayed() {
        return totalPayed;
    }

    public void setTotalPayed(Integer totalPayed) {
        this.totalPayed = totalPayed;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getGearType() {
        return gearType;
    }

    public void setGearType(String gearType) {
        this.gearType = gearType;
    }
}
