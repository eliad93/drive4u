package com.example.eliad.drive4u.models;
import com.example.eliad.drive4u.helpers.ConditionsHelper.Order;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Teacher extends User {
    private ArrayList<String> students = new ArrayList<>(); // storing students id
    private Integer totalPaid = 0;
    private String carModel;
    private Integer price;
    private String gearType;
    private Integer lessonLength;
    private ArrayList<String> connectionRequests = new ArrayList<>();
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
                   String mCity, String mEmail, String mCarModel, Integer mPrice,
                   String mGearType, Integer mlessonLength, String imageUrl, String status){
        super(mId, mFirstName, mLastName, mPhoneNumber, mCity, mEmail, imageUrl, status);
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

    public void addConnectionRequest(String studentId){
        connectionRequests.add(studentId);
    }

    public void removeConnectionRequest(String studentId){
        connectionRequests.remove(studentId);
    }

    public Double worth(){
        return Double.valueOf(this.lessonLength)
                / Double.valueOf(this.price);
    }

    public Integer compareByNumStudents(Teacher teacher){
        return this.numberOfStudents().compareTo(teacher.numberOfStudents());
    }

    public Integer compareByCity(Teacher teacher){
        return this.city.compareTo(teacher.getCity());
    }

    public Integer compareByPrice(Teacher teacher){
        return this.price.compareTo(teacher.getPrice());
    }

    public Integer compareByLessonLength(Teacher teacher){
        return this.lessonLength.compareTo(teacher.getLessonLength());
    }

    public Integer compareByWorth(Teacher teacher){
        double diff = this.worth() - teacher.worth();
        if(diff > 0){
            return 1;
        } else if(diff < 0){
            return -1;
        } else {
            return 0;
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

    public Integer getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(Integer totalPaid) {
        this.totalPaid = totalPaid;
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

    public ArrayList<String> getConnectionRequests() {
        return connectionRequests;
    }

    public void setConnectionRequests(ArrayList<String> connectionRequests) {
        this.connectionRequests = connectionRequests;
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
        totalPaid = in.readInt();
        carModel = in.readString();
        price = in.readInt();
        lessonLength = in.readInt();
        gearType = in.readString();
        connectionRequests = in.readArrayList(String.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        super.writeToParcel(dest, flags);
        dest.writeList(students);
        dest.writeInt(totalPaid);
        dest.writeString(carModel);
        dest.writeInt(price);
        dest.writeInt(lessonLength);
        dest.writeString(gearType);
        dest.writeList(connectionRequests);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String getClassRoom() {
        return id;
    }

    public static class Comparators{
        static int compareStrings(String s1, String s2){
            if(s1 != null){
                return s1.compareTo(s2);
            }
            return "".compareTo(s2);
        }
        static final class Name implements Comparator<Teacher>{
            @Override
            public int compare(Teacher u1, Teacher u2) {
                if(u1 == null || u2 == null){
                    return 0;
                }
                return compareStrings(u1.getFullName(), u2.getFullName());
            }
        }
        static final class City implements Comparator<Teacher>{
            @Override
            public int compare(Teacher u1, Teacher u2) {
                if(u1 == null || u2 == null){
                    return 0;
                }
                return compareStrings(u1.getCity(), u2.getCity());
            }
        }
        static final class Balance implements Comparator<Teacher>{
            @Override
            public int compare(Teacher u1, Teacher u2) {
                if(u1 == null || u2 == null){
                    return 0;
                }
                return u1.getBalance() - u2.getBalance();
            }
        }
        static final class NumberOfStudents implements Comparator<Teacher>{
            @Override
            public int compare(Teacher s1, Teacher s2) {
                if(s1 == null || s2 == null){
                    return 0;
                }
                return s1.numberOfStudents() - s2.numberOfStudents();
            }
        }
        static final class TotalPaid implements Comparator<Teacher> {
            @Override
            public int compare(Teacher s1, Teacher s2) {
                if(s1 == null || s2 == null){
                    return 0;
                }
                return s1.getTotalPaid() - s2.getTotalPaid();
            }
        }
        static final class Price implements Comparator<Teacher> {
            @Override
            public int compare(Teacher s1, Teacher s2) {
                if(s1 == null || s2 == null){
                    return 0;
                }
                return s1.getPrice() - s2.getPrice();
            }
        }
        static final class LessonLength implements Comparator<Teacher> {
            @Override
            public int compare(Teacher s1, Teacher s2) {
                if(s1 == null || s2 == null){
                    return 0;
                }
                return s1.getLessonLength() - s2.getLessonLength();
            }
        }
        static final class Worth implements Comparator<Teacher> {
            @Override
            public int compare(Teacher s1, Teacher s2) {
                if(s1 == null || s2 == null){
                    return 0;
                }
                return s1.worth().compareTo(s2.worth());
            }
        }
        static final class GearType implements Comparator<Teacher> {
            @Override
            public int compare(Teacher s1, Teacher s2) {
                if(s1 == null || s2 == null){
                    return 0;
                }
                return Comparators.compareStrings(s1.getGearType(),s2.getGearType());
            }
        }
    }
    public static class Sort{
        private Sort(){}
        private static void sort(List<Teacher> teachers, Order order, Comparator<Teacher> c){
            if(order == null || order.equals(Order.ASCENDING)){
                Collections.sort(teachers, c);
            } else {
                Collections.sort(teachers, Collections.reverseOrder(c));
            }
        }
        public static void name(List<Teacher> users, Order order){
            Comparator<Teacher> c = new Comparators.Name();
            sort(users, order, c);
        }
        public static void city(List<Teacher> users, Order order){
            Comparator<Teacher> c = new Comparators.City();
            sort(users, order, c);
        }
        public static void balance(List<Teacher> users, Order order){
            Comparator<Teacher> c = new Comparators.Balance();
            sort(users, order, c);
        }
        public static void numberOfStudents(List<Teacher> teachers, Order order){
            Comparator<Teacher> c = new Comparators.NumberOfStudents();
            sort(teachers, order, c);
        }
        public static void lessonLength(List<Teacher> teachers, Order order){
            Comparator<Teacher> c = new Comparators.LessonLength();
            sort(teachers, order, c);
        }
        public static void price(List<Teacher> teachers, Order order){
            Comparator<Teacher> c = new Comparators.Price();
            sort(teachers, order, c);
        }
        public static void worth(List<Teacher> teachers, Order order){
            Comparator<Teacher> c = new Comparators.Worth();
            sort(teachers, order, c);
        }
    }
    public static class Filter{
        private interface Cond{
            Boolean condition(Teacher teacher);
        }
        private Filter(){}
        private static void filter(List<Teacher> teachers, Cond c){
            Iterator<Teacher> i = teachers.iterator();
            while (i.hasNext()) {
                Teacher teacher = i.next();
                if(!c.condition(teacher)){
                    i.remove();
                }
            }
        }
        public static void nameEquals(List<Teacher> users, final String name){
            filter(users, new Cond() {
                @Override
                public Boolean condition(Teacher u) {
                    assert u != null;
                    if(u.getFullName() != null){
                        return u.getFullName().equals(name);
                    }
                    return name == null;
                }
            });
        }
        public static void cityEquals(List<Teacher> users, final String city){
            filter(users, new Cond() {
                @Override
                public Boolean condition(Teacher u) {
                    assert u != null;
                    if(u.getCity() != null){
                        return u.getCity().equals(city);
                    }
                    return city == null;
                }
            });
        }
        public static void balanceGreaterEquals(List<Teacher> users, final int balance){
            filter(users, new Cond() {
                @Override
                public Boolean condition(Teacher u) {
                    assert u != null;
                    return u.getBalance() >= balance;
                }
            });
        }
        public static void gearType(List<Teacher> teachers, final String gearType){
            filter(teachers, new Cond() {
                @Override
                public Boolean condition(Teacher teacher) {
                    assert teacher != null;
                    if(teacher.getGearType() != null){
                        return teacher.getGearType().equals(gearType);
                    }
                    return gearType == null;
                }
            });
        }
        public static void numberOfStudents(List<Teacher> teachers, final int n){
            filter(teachers, new Cond() {
                @Override
                public Boolean condition(Teacher teacher) {
                    assert teacher != null;
                    return teacher.numberOfStudents() >= n;
                }
            });
        }
    }
}
