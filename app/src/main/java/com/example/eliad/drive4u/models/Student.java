package com.example.eliad.drive4u.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Student extends User {
    private Integer numberOfLessons = 0;
    private String teacherId = "";
    private Integer totalExpense = 0;

    public Student(String mId, String mFirstName, String mLastName, String mPhoneNumber,
                    String mCity, String mEmail) {
        super(mId, mFirstName, mLastName, mPhoneNumber, mCity, mEmail);
    }

    public Student(){

    }

    public Integer getNumberOfLessons() {
        return numberOfLessons;
    }

    public void setNumberOfLessons(Integer numberOfLessons) {
        this.numberOfLessons = numberOfLessons;
    }

    public Integer getBalance() { return 0; }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public Integer getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(Integer totalExpense) {
        this.totalExpense = totalExpense;
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
    }
}
