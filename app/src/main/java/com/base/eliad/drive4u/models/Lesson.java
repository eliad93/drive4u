package com.base.eliad.drive4u.models;

import android.os.Parcel;
import android.os.Parcelable;


public class Lesson implements Parcelable {

    public enum Status {
        T_CANCELED(0, "teacher canceled the lesson"),
        T_CONFIRMED(1, "teacher confirmed the lesson"),
        T_UPDATE(2, "teacher updated the lesson"),
        S_CANCELED(3, "student canceled the lesson"),
        S_CONFIRMED(4, "student confirmed the lesson"),
        S_REQUEST(5, "student request the lesson"),
        S_UPDATE(6, "student update the lesson"),
        T_OPTION(6, "teacher offer the lesson");

        private Integer code;
        private String userMessage;

        Status(Integer c, String userMessage){
            this.code = c;
            this.userMessage = userMessage;
        }

        public String getUserMessage(){
            return userMessage;
        }
    }
    protected String lessonID = null;

    public enum Grade{
        BAD(1, "bad lesson"),
        GOOD(2, "good lesson"),
        EXCELLENT(3, "excellent lesson");
        private Integer grade;
        private String description;
        Grade(Integer grade, String description){
            this.grade = grade;
            this.description = description;
        }
    }

    public class Summary implements Parcelable{

        protected Summary(Parcel in) {
            paragraph = in.readString();
            grade = Grade.valueOf(in.readString());
        }
        private Grade grade;
        private String paragraph;
        public String getParagraph() {
            return paragraph;
        }
        public void setParagraph(String paragraph) {
            this.paragraph = paragraph;
        }
        public Grade getGrade() {
            return grade;
        }
        public void setGrade(Grade grade) {
            this.grade = grade;
        }
        public final Creator<Summary> CREATOR = new Creator<Summary>() {
            @Override
            public Summary createFromParcel(Parcel in) {
                return new Summary(in);
            }
            @Override
            public Summary[] newArray(int size) {
                return new Summary[size];
            }
        };
        @Override
        public int describeContents() {
            return 0;
        }
        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(paragraph);
            dest.writeString(grade.name());
        }
    }

    protected String teacherUID = null;
    protected String studentUID = null;
    protected String date = null;
    protected String hour = null;
    protected String startingLocation = null;
    protected String endingLocation = null;
    protected Status conformationStatus = Status.S_REQUEST;
    protected Summary summary;

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

    public String getLessonID() {
        return lessonID;
    }

    public void setLessonID(String UID) {this.lessonID = UID;}

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

    public Summary getSummary() {
        return summary;
    }

    public void setSummary(Summary summary) {
        this.summary = summary;
    }

    public void summarize(String paragraph, int grade){
        summary.setParagraph(paragraph);
        summary.setGrade(Grade.values()[grade]);
    }

    protected Lesson(Parcel in) {
        teacherUID = in.readString();
        studentUID = in.readString();
        date = in.readString();
        hour = in.readString();
        startingLocation = in.readString();
        endingLocation = in.readString();
        conformationStatus = Status.valueOf(in.readString());
        summary = in.readParcelable(Summary.class.getClassLoader());
    }

    public static final Creator<Lesson> CREATOR = new Creator<Lesson>() {
        @Override
        public Lesson createFromParcel(Parcel in) {
            return new Lesson(in);
        }

        @Override
        public Lesson[] newArray(int size) {
            return new Lesson[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(teacherUID);
        dest.writeString(studentUID);
        dest.writeString(date);
        dest.writeString(hour);
        dest.writeString(startingLocation);
        dest.writeString(endingLocation);
        dest.writeString(conformationStatus.name());
        dest.writeParcelable(summary, flags);
    }
}
