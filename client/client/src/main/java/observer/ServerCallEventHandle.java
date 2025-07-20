package observer;

import model.Chat;
import model.ChatRoom;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerCallEventHandle {

    private static final List<Observer> observers = new CopyOnWriteArrayList<>();

    public static void registerObserver(Observer o) {
        observers.add(o);
        System.out.println("[DEBUG] 옵저버 등록, 현재 수: " + observers.size());
    }

    public static void unregisterObserver(Observer observer) {
        observers.remove(observer);
    }

    public static void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.onMessageReceived(message);
        }
    }
    public static void notifyChatRoomListUpdated(List<ChatRoom> rooms) {
        System.out.println("[LOG] notifyChatRoomListUpdated 호출! 옵저버 개수: " + observers.size() + ", 방 개수: " + rooms.size()); // [추가합니다] 디버깅용 로그
        for (Observer observer : observers) {
            observer.onChatRoomListUpdated(rooms);
        }
    }
    
    public static void notifyChatDataUpdated(int chatRoomNum, List<Chat> chats) {
        System.out.println("[LOG] notifyChatDataUpdated 호출! 옵저버 개수: " + observers.size() + ", 채팅방 번호: " + chatRoomNum + ", 채팅 개수: " + chats.size());
        for (Observer observer : observers) {
            observer.onChatDataUpdated(chatRoomNum, chats);
        }
    }
}
