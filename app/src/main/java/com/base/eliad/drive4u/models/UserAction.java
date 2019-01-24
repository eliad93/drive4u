package com.base.eliad.drive4u.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

public class UserAction implements Parcelable {

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    private String senderId;
    private String receiverId;
    private String dateTime;
    private String description;
    private String notice;
    private String type;
    @Exclude private String actionId;

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    protected UserAction(Parcel in) {
        senderId = in.readString();
        receiverId = in.readString();
        dateTime = in.readString();
        description = in.readString();
        notice = in.readString();
        type = in.readString();
        actionId = in.readString();
    }

    public static final Creator<UserAction> CREATOR = new Creator<UserAction>() {
        @Override
        public UserAction createFromParcel(Parcel in) {
            return new UserAction(in);
        }

        @Override
        public UserAction[] newArray(int size) {
            return new UserAction[size];
        }
    };

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public UserAction(String mSender, String mReceiver, String mDateTime,
                      String mDescription, String mNotice, String mType){
        senderId = mSender;
        receiverId = mReceiver;
        dateTime = mDateTime;
        description = mDescription;
        notice = mNotice;
        type = mType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UserAction(){}

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(senderId);
        dest.writeString(receiverId);
        dest.writeString(dateTime);
        dest.writeString(description);
        dest.writeString(notice);
        dest.writeString(type);
        dest.writeString(actionId);
    }


    public enum Notice{
        UNSEEN("unseen"),
        SEEN("seen"),
        RESPONDED("responded");
        private String message;
        Notice(String mSeen) {
            message = mSeen;
        }
        public String getMessage(){
            return message;
        }
    }

    public enum Type{
        CONNECTION_REQUEST("connection request");
        String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        Type(String msg) {
            message = msg;
        }
    }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof UserAction)){
            return false;
        }
        if(actionId == null){
            return false;
        }
        return actionId.equals(((UserAction) o).actionId);
    }

}
