package com.example.eliad.drive4u.models;
import com.example.eliad.drive4u.helpers.ConditionsHelper.Order;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Student extends User {
    private Integer numberOfLessons = 0;
    private String teacherId;
    private Integer totalExpense = 0;
    private String gearType;
    private String request = ConnectionRequestStatus.NOT_YET.getUserMessage();

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public enum ConnectionRequestStatus{
        NOT_YET(0, "connection request to teacher not yet sent"),
        SENT(1, "connection request to teacher sent"),
        ACCEPTED(2, "connection request accepted by teacher"),
        DECLINED(3, "connection request declined by teacher");
        private Integer code;
        private String userMessage;
        public String getUserMessage() {
            return userMessage;
        }
        ConnectionRequestStatus(Integer c, String userMessage){
            this.code = c;
            this.userMessage = userMessage;
        }
    }
    public enum GearType {
        MANUAL(1, "manual"),
        AUTOMATIC(2, "automatic"),
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
                    String mCity, String mEmail, String teacherId,Integer totalExpense,
                   Integer numberOfLessons, String mGearType, String imageUrl, String status,
                   String mRequest) {
        super(mId, mFirstName, mLastName, mPhoneNumber, mCity, mEmail, imageUrl, status);
        this.teacherId = teacherId;
        this.totalExpense = totalExpense;
        this.numberOfLessons = numberOfLessons;
        this.gearType = mGearType;
        this.request = mRequest;
    }

    public Student(String mId, String mFirstName, String mLastName, String mPhoneNumber,
                   String mCity, String mEmail, String teacherId,Integer totalExpense,
                   Integer numberOfLessons, String mGearType, String imageUrl, String status) {
        super(mId, mFirstName, mLastName, mPhoneNumber, mCity, mEmail, imageUrl, status);
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

    public int getBalance() { return 0; }

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
        // TODO: fix that logic
        if(ConnectionRequestStatus.SENT.getUserMessage().equals(request)){
            return true;
        }
        if(teacherId != null){
            return (!teacherId.isEmpty());
        }
        return false;
    }

//    public Boolean hasPendingRequest(){
//        if(hasTeacher()){
//            // make sure we are consistent
//            // no request if already has a teacher
//            assert request == null;
//            return false;
//        }
//        if(request != null){
//            return request.getStatus() == StudentConnectionRequest.Status.SENT;
//        }
//        return false;
//    }

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
        request = in.readString();
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
        dest.writeString(request);
    }

    @Override
    public String getClassRoom() {
        return teacherId;
    }

    public static class Comparators{
        static int compareStrings(String s1, String s2){
            if(s1 != null){
                return s1.compareTo(s2);
            }
            return "".compareTo(s2);
        }
        static final class Name implements Comparator<Student>{
            @Override
            public int compare(Student u1, Student u2) {
                if(u1 == null || u2 == null){
                    return 0;
                }
                return compareStrings(u1.getFullName(), u2.getFullName());
            }
        }
        static final class City implements Comparator<Student>{
            @Override
            public int compare(Student u1, Student u2) {
                if(u1 == null || u2 == null){
                    return 0;
                }
                return compareStrings(u1.getCity(), u2.getCity());
            }
        }
        static final class Balance implements Comparator<Student>{
            @Override
            public int compare(Student u1, Student u2) {
                if(u1 == null || u2 == null){
                    return 0;
                }
                return u1.getBalance() - u2.getBalance();
            }
        }
        static final class NumberOfLessons implements Comparator<Student>{
            @Override
            public int compare(Student s1, Student s2) {
                if(s1 == null || s2 == null){
                    return 0;
                }
                return s1.getNumberOfLessons() - s2.getNumberOfLessons();
            }
        }
        static final class TotalExpense implements Comparator<Student>{
            @Override
            public int compare(Student s1, Student s2) {
                if(s1 == null || s2 == null){
                    return 0;
                }
                return s1.getTotalExpense() - s2.getTotalExpense();
            }
        }
    }
    public static class Sort{
        private Sort(){}
        public static void name(List<Student> users, Order order){
            Comparator<Student> c = new Comparators.Name();
            sort(users, order, c);
        }
        public static void city(List<Student> users, Order order){
            Comparator<Student> c = new Comparators.City();
            sort(users, order, c);
        }
        public static void balance(List<Student> users, Order order){
            Comparator<Student> c = new Comparators.Balance();
            sort(users, order, c);
        }
        private static void sort(List<Student> students, Order order, Comparator<Student> c){
            if(order == null || order.equals(Order.ASCENDING)){
                Collections.sort(students, c);
            } else {
                Collections.sort(students, Collections.reverseOrder(c));
            }
        }
        public void numberOfLessons(List<Student> users, Order order){
            Comparator<Student> c = new Comparators.NumberOfLessons();
            sort(users, order, c);
        }
        public void totalExpense(List<Student> students, Order order){
            Comparator<Student> c = new Comparators.TotalExpense();
            sort(students, order, c);
        }
    }
    public static class Filter{
        private interface Cond{
            Boolean condition(Student s);
        }
        private Filter(){}
        private static LinkedList<Student> filter(List<Student> students, Cond c){
            LinkedList<Student> filteredList = new LinkedList<>(students);
            Iterator<Student> i = filteredList.iterator();
            while (i.hasNext()) {
                Student student = i.next();
                if(c.condition(student)){
                    i.remove();
                }
            }
            return filteredList;
        }
        public static LinkedList<Student> nameEquals(LinkedList<Student> users, final String name){
            return filter(users, new Cond() {
                @Override
                public Boolean condition(Student u) {
                    assert u != null;
                    if(u.getFullName() != null){
                        return u.getFullName().equals(name);
                    }
                    return name == null;
                }
            });
        }
        public static LinkedList<Student> cityEquals(LinkedList<Student> users, final String city){
            return filter(users, new Cond() {
                @Override
                public Boolean condition(Student u) {
                    assert u != null;
                    if(u.getCity() != null){
                        return u.getCity().equals(city);
                    }
                    return city == null;
                }
            });
        }
        public static List<Student> balanceGreaterEquals(LinkedList<Student> users, final int balance){
            return filter(users, new Cond() {
                @Override
                public Boolean condition(Student u) {
                    assert u != null;
                    return u.getBalance() >= balance;
                }
            });
        }
        public List<Student> gearTypeEquals(List<Student> students, final String gearType){
            return filter(students, new Cond() {
                @Override
                public Boolean condition(Student s) {
                    assert s != null;
                    if(s.getGearType() != null){
                        return s.getGearType().equals(gearType);
                    }
                    return gearType == null;
                }
            });
        }
        public List<Student> numberOfLessonsGreaterEquals(List<Student> students, final int n){
            return filter(students, new Cond() {
                @Override
                public Boolean condition(Student s) {
                    assert s != null;
                    return s.getNumberOfLessons() >= n;
                }
            });
        }
        public List<Student> totalExpenseGreaterEquals(List<Student> students, final int n){
            return filter(students, new Cond() {
                @Override
                public Boolean condition(Student s) {
                    assert s != null;
                    return s.getTotalExpense() >= n;
                }
            });
        }
    }
}
