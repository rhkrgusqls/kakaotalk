package model;

import java.sql.Timestamp;

public class ChatData {
    private int chatIndex;
    private int chatRoomNum;
    private String text;
    private String userId;
    private Timestamp time;

    public int getChatIndex() {
        return chatIndex;
    }

    public void setChatIndex(int chatIndex) {
        this.chatIndex = chatIndex;
    }

    public int getChatRoomNum() {
        return chatRoomNum;
    }

    public void setChatRoomNum(int chatRoomNum) {
        this.chatRoomNum = chatRoomNum;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}
