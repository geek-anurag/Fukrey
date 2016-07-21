package com.example.neelmani.fukrey.CommentHandlers;

/**
 * Created by Anurag on 12/8/2015.
 */
public class Comments {

    private long id;
    private boolean isMe;
    private boolean isTitle;
    private boolean isInfo;
    private String message;
    private Long userId;
    private String dateTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean getIsInfo() {
        return isInfo;
    }

    public void setIsInfo(boolean isInfo) {
        this.isInfo = isInfo;
    }

    public boolean getIsme() {
        return isMe;
    }

    public void setMe(boolean isMe) {
        this.isMe = isMe;
    }

    public boolean getIsTitle() {
        return isTitle;
    }

    public void setTitle(boolean isTitle) {
        this.isTitle = isTitle;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

   public String getDate() {
        return dateTime;
    }

    public void setDate(String dateTime) {
        this.dateTime = dateTime;
    }
}
