package com.example.eliad.drive4u.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;

public class Teacher extends User {
    private HashMap<String, Student> students = null; // key is uId from firebase auth
    private Integer totalPayed = 0;
    private String carModel = null;
    private Integer price=100;
    private String gearType = null;


    public Teacher(String mId, String mFirstName, String mLastName, String mPhoneNumber,
                   String mEmail, String mCity, String mGearType) {
        super(mId, mFirstName, mLastName, mPhoneNumber, mEmail, mCity);
        gearType = mGearType;
    }

    public Teacher(){
        super();
    }

    public Teacher(String mId, String mFirstName, String mLastName, String mPhoneNumber,
                   String mCity, String mEmail, String mCarModel, Integer mPrice, String mGearType){
        super(mId, mFirstName, mLastName, mPhoneNumber, mEmail, mCity);
        carModel = mCarModel;
        price = mPrice;
        gearType = mGearType;
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

    public static final Parcelable.Creator<Teacher> CREATOR = new Parcelable.Creator<Teacher>() {
        public Teacher createFromParcel(Parcel in) {
            return new Teacher(in);
        }

        public Teacher[] newArray(int size) {
            return new Teacher[size];
        }
    };

    // Parcelling part
    @SuppressWarnings("unchecked")
    protected Teacher(Parcel in){
        super(in);
        students = new HashMap<>();
        int size = in.readInt();
        for(int i = 0; i < size; i++){
            String key = in.readString();
            Student value = in.readParcelable(Student.class.getClassLoader());
            students.put(key,value);
        }
        totalPayed = in.readInt();
        carModel = in.readString();
        price = in.readInt();
        gearType = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        super.writeToParcel(dest, flags);
        dest.writeInt(students.size());
        for(Map.Entry<String,Student> entry : students.entrySet()){
            dest.writeString(entry.getKey());
            dest.writeParcelable(entry.getValue(), flags);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
