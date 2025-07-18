package model;

public class ChatRoom {
    private int chatRoomNum;
    private String roomType;
    private String roomName;

    public ChatRoom() {}

    public ChatRoom(int chatRoomNum, String roomType, String roomName) {
        this.chatRoomNum = chatRoomNum;
        this.roomType = roomType;
        this.roomName = roomName;
    }

    public int getChatRoomNum() {
        return chatRoomNum;
    }

    public void setChatRoomNum(int chatRoomNum) {
        this.chatRoomNum = chatRoomNum;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}
