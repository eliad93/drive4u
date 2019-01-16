package com.example.eliad.drive4u.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Student extends User {
    private Integer numberOfLessons = 0;
    private String teacherId;
    private Integer totalExpense = 0;
    private String gearType;
    public enum GearType {
        Manual(1, "manual"),
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
    public Student(String mId, String mFirstName, String mLastName, String mPhoneNumber,
                    String mCity, String mEmail, String teacherId,Integer totalExpense,Integer numberOfLessons, String mGearType, String imageUrl ) {
        super(mId, mFirstName, mLastName, mPhoneNumber, mCity, mEmail, imageUrl);
        this.teacherId = teacherId;
        this.totalExpense =totalExpense;
        this.numberOfLessons = numberOfLessons;
        this.gearType = mGearType;
    }

    public Student(){

    }

    public Integer getNumberOfLessons() {
        return numberOfLessons;
    }

    public void setNumberOfLessons(Integer numberOfLessons) {
        this.numberOfLessons = numberOfLessons;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public Integer getBalance() { return 0; }

    public Integer getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(Integer totalExpense) {
        this.totalExpense = totalExpense;
    }

    public String getGearType() {
        return gearType;
    }

    public void setGearType(String gearType) {
        this.gearType = gearType;
    }


    public boolean hasTeacher(){
        if(teacherId != null){
            return (!teacherId.isEmpty());
        }
        return false;
    }

    public static final Parcelable.Creator<Student> CREATOR = new Parcelable.Creator<Student>() {
        public Student createFromParcel(Parcel in) {
            return new Student(in);
        }

        public Student[] newArray(int size) {
            return new Student[size];
        }
    };

    // Parcelling part
    protected Student(Parcel in){
        super(in);
        numberOfLessons = in.readInt();
        teacherId = in.readString();
        totalExpense = in.readInt();
        gearType = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(numberOfLessons);
        dest.writeString(teacherId);
        dest.writeInt(totalExpense);
        dest.writeString(gearType);
    }

    @Override
    public String getClassRoom() {
        return teacherId;
    }
}
