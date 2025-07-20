package observer;

import model.Chat;
import model.ChatRoom;
import java.util.List;

public interface Observer {
    void onMessageReceived(String message);
    
    void onChatRoomListUpdated(List<ChatRoom> rooms); //채팅방 목록이 업데이트되었을 때 호출될 메서드
    
    void onChatDataUpdated(int chatRoomNum, List<Chat> chats); //채팅 데이터가 업데이트되었을 때 호출될 메서드
}