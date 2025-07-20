package view;

import model.Chat;
import model.ChatRoom;
import observer.Observer;
import observer.ServerCallEventHandle;
import model.LocalDBManager;
import model.ChatFileManager;

import javax.swing.JTextArea;
import java.util.List;

public class ChatWindowObserver implements Observer {
    private int targetChatRoomNum;
    private JTextArea otherUserChatArea;
    private JTextArea myChatArea;
    private String currentUserId;

    public ChatWindowObserver(int chatRoomNum, JTextArea otherUserChatArea, JTextArea myChatArea) {
        this.targetChatRoomNum = chatRoomNum;
        this.otherUserChatArea = otherUserChatArea;
        this.myChatArea = myChatArea;
        this.currentUserId = controller.MainController.getLoggedInUser().getId();
    }

    @Override
    public void onMessageReceived(String message) {
        // 채팅창에서는 일반 메시지는 처리하지 않음
    }

    @Override
    public void onChatRoomListUpdated(List<ChatRoom> rooms) {
        // 채팅창에서는 채팅방 목록 업데이트는 처리하지 않음
    }

    @Override
    public void onChatDataUpdated(int chatRoomNum, List<Chat> chats) {
        // 해당 채팅방의 데이터만 처리
        if (chatRoomNum == targetChatRoomNum) {
            ChatFileManager fileManager = new ChatFileManager();
            fileManager.saveChatsToFile(chatRoomNum, chats);
            // 기존 내용 초기화
            otherUserChatArea.setText("");
            myChatArea.setText("");
            // 파일에서 읽어와 표시
            String allChats = fileManager.loadChatsFromFile(chatRoomNum);
            otherUserChatArea.setText(allChats);
            myChatArea.setText(""); // 내 메시지만 분리해서 표시하려면 파싱 추가
        }
    }
    
    // 옵저버 해제 메서드
    public void unregister() {
        ServerCallEventHandle.unregisterObserver(this);
    }
} 