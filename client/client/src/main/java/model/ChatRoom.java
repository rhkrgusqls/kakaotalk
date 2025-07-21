package model;

public class ChatRoom {
    private int chatRoomNum;
    private String roomType;
    private String roomName;
    private String lastMessage;
    private String lastMessageTime;

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
    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
    public String getLastMessageTime() { return lastMessageTime; }
    public void setLastMessageTime(String lastMessageTime) { this.lastMessageTime = lastMessageTime; }
}
