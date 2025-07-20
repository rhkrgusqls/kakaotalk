package observer;

import model.ChatRoom;
import java.util.List;

public interface Observer {
    void onMessageReceived(String message);
    
    void onChatRoomListUpdated(List<ChatRoom> rooms); //채팅방 목록이 업데이트되었을 때 호출될 메서드
}