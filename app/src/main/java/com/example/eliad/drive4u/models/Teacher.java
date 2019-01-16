package com.example.eliad.drive4u.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Teacher extends User {
    private ArrayList<String> students = new ArrayList<>(); // storing students id
    private Integer totalPayed = 0;
    private String carModel;
    private Integer price;
    private String gearType;
    private Integer lessonLength;
    public enum GearType {
        Manual(1, "manual"),
        AUTOMATIC (2, "automatic"),
        BOTH(3, "manual and automatic");

        private Integer code;
        private String userMessage;

        GearType(Integer c, String userMessage){
            this.code = c;
            this.userMessage = userMessage;
        }

        public String getUserMessage(){
            return userMessage;
        }
    }
    public Teacher(){
        super();
    }

    public Teacher(String mId, String mFirstName, String mLastName, String mPhoneNumber,
                   String mCity, String mEmail, String mCarModel, Integer mPrice, String mGearType, Integer mlessonLength, String imageUrl, String status){
        super(mId, mFirstName, mLastName, mPhoneNumber, mEmail, mCity, imageUrl, status);
        carModel = mCarModel;
        price = mPrice;
        lessonLength = mlessonLength;
        gearType = mGearType;
    }

    public void addStudent(String student){
        assert student != null;
        if(!students.contains(student)){
            students.add(student);
        }
    }

    public Boolean isConnected(String student){
        return students.contains(student);
    }

    public Integer numberOfStudents(){
        return students.size();
    }

    public ArrayList<String> getStudents() {
        return students;
    }

    public void setStudents(ArrayList<String> students) {
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

    public Integer getLessonLength() {
        return lessonLength;
    }

    public void setLessonLength(Integer lessonLength) {
        this.lessonLength = lessonLength;
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
        students = new ArrayList<>();
        students = in.readArrayList(String.class.getClassLoader());
        totalPayed = in.readInt();
        carModel = in.readString();
        price = in.readInt();
        lessonLength = in.readInt();
        gearType = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        super.writeToParcel(dest, flags);
        dest.writeList(students);
        dest.writeInt(totalPayed);
        dest.writeString(carModel);
        dest.writeInt(price);
        dest.writeInt(lessonLength);
        dest.writeString(gearType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String getClassRoom() {
        return id;
    }
}
