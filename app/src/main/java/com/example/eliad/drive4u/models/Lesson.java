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
    public enum Hours {
        H_0700, H_0800, H_0900, H_1000, H_1100, H_1200, H_1300, H_1400, H_1500, H_1600, H_1700, H_1800, H_1900, H_2000
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
