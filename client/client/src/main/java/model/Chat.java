package model;

import java.sql.Timestamp;

public class Chat {
    private int chatIndex;
    private int chatRoomNum;
    private String text;
    private String userId;
    private Timestamp time;

    // 기본 생성자
    public Chat() {}

    // 전체 필드 초기화 생성자
    public Chat(int chatIndex, int chatRoomNum, String text, String userId, Timestamp time) {
        this.chatIndex = chatIndex;
        this.chatRoomNum = chatRoomNum;
        this.text = text;
        this.userId = userId;
        this.time = time;
    }

    // Getter / Setter
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