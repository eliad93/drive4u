package com.example.eliad.drive4u.models;

import com.google.protobuf.Enum;


public class Lesson {
    public enum Status {
        T_CANCELED(0, "teacher canceled the lesson"),
        T_CONFIRMED(1, "teacher confirmed the lesson"),
        T_UPDATE(2, "teacher updated the lesson"),
        S_CANCELED(3, "student canceled the lesson"),
        S_CONFIRMED(4, "student confirmed the lesson"),
        S_REQUEST(5, "student request the lesson"),
        S_UPDATE(6, "student update the lesson");

        private int code;
        private String userMessage;
        private Status(int c, String userMessage){
            code = c;
            userMessage = userMessage;
        }

        public String getUserMessage(){
            return userMessage;
        }
    }
    protected String teacherUID = null;
    protected String studentUID = null;
    protected String date = null;
    protected String hour = null;
    protected String startingLocation = null;
    protected String endingLocation = null;
    protected Status conformationStatus = Status.S_REQUEST;

    private Lesson() {} // empty constructor for firebase

    public Lesson(String teacherUID, String studentUID, String date, String hour,
                     String startingLocation, String endingLocation,
                     Status conformationStatus){
        this.teacherUID = teacherUID;
        this.studentUID = studentUID;
        this.date = date;
        this.hour = hour;
        this.startingLocation= startingLocation;
        this.endingLocation= endingLocation;
        this.conformationStatus = conformationStatus;

    }
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

    public String getStartingLocation() {return startingLocation;}

    public void setStartingLocation(String startingLocation) {this.startingLocation = startingLocation;}

    public String getEndingLocation() {return endingLocation;}

    public void setEndingLocation(String endingLocation) {this.endingLocation = endingLocation;}

    public Status getConformationStatus() {return conformationStatus;}

    public void setConformationStatus(Status conformationStatus) {this.conformationStatus = conformationStatus;}
}
