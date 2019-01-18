package com.example.eliad.drive4u.models;

public class StudentConnectionRequest {
    public enum Status{
        SENT(0, "connection request to teacher sent"),
        ACCEPTED(1, "connection request accepted by teacher"),
        DECLINED(2, "connection request declined by teacher");
        private Integer code;
        private String userMessage;
        Status(Integer c, String userMessage){
            this.code = c;
            this.userMessage = userMessage;
        }
    }
    private String studentId;
    private String teacherId;
    private Status status;
    public StudentConnectionRequest(String mStudentId, String mTeacherId){
        studentId = mStudentId;
        teacherId = mTeacherId;
        status = Status.SENT;
    }
    public String getStudentId() {
        return studentId;
    }
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    public String getTeacherId() {
        return teacherId;
    }
    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }
}
