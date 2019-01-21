package com.example.eliad.drive4u.notifications;

public class Data {

    private String sourceId;
    private int icon;
    private String body;
    private String title;
    private String receiverId;

    public Data(String sourceId, int icon, String body, String title, String receiverId) {
        this.sourceId = sourceId;
        this.icon = icon;
        this.body = body;
        this.title = title;
        this.receiverId = receiverId;
    }

    public Data() {
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }
}
