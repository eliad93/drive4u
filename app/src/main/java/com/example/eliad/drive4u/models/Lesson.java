package com.example.eliad.drive4u.models;

import com.google.protobuf.Enum;


public class Lesson {
    enum Status {
        T_CANCELED,T_CONFIRMED,T_REQUEST,T_UPDATE,S_CANCELED,S_CONFIRMED,S_REQUEST,S_UPDATE
    }
    protected String teacherUID = null;
    protected String studentUID = null;
    protected String date = null;
    protected String hour = null;
    protected boolean isPayed = false;
    protected String startingLocation = null;
    protected String endingLocation = null;
    protected Integer lessonNumber = 0;
    protected Status conformationStatus = Status.S_REQUEST;

    private Lesson() {} // empty constructor for firebase

    // getters and setters for firebase
    public String getTeacherUID() {
        return teacherUID;
    }

    public void setTeacherUID(String UID) {this.teacherUID = UID;}

    public String getStudentUID() {
        return studentUID;
    }

    public void setStudentUID(String UID) {
        this.studentUID = UID;
    }

    public String getDate() {return date;}

    public void setDate(String date) {this.date = date;}

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {this.hour = hour;}

    public boolean getIsPayed() {
        return isPayed;
    }

    public void setIsPayed(boolean isPayed) {this.isPayed = isPayed;}

    public String getStartingLocation() {
        return startingLocation;
    }

    public void setStartingLocation(String startingLocation) {this.startingLocation = startingLocation;}

    public String getEndingLocation() {return endingLocation;}

    public void setEndingLocation(String endingLocation) {this.endingLocation = endingLocation;}

    public Integer getLessonNumber() {
        return lessonNumber;
    }

    public void setLessonNumber(Integer lessonNumber) { this.lessonNumber = lessonNumber; }

    public Status getConformationStatus() {return conformationStatus;}

    public void setConformationStatus(Status conformationStatus) {this.conformationStatus = conformationStatus;}
}
