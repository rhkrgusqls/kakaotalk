package view;

import model.Chat;
import model.ChatRoom;
import observer.Observer;
import observer.ServerCallEventHandle;
import model.LocalDBManager;
// import model.ChatFileManager; // 제거

import javax.swing.JTextArea;
import java.util.List;
import java.sql.Timestamp;

public class ChatWindowObserver implements Observer {
    private int targetChatRoomNum;
    private JTextArea chatArea;
    private String currentUserId;

    public ChatWindowObserver(int chatRoomNum, JTextArea chatArea, JTextArea unused) {
        this.targetChatRoomNum = chatRoomNum;
        this.chatArea = chatArea;
        this.currentUserId = controller.MainController.getLoggedInUser().getId();
    }

    @Override
    public void onMessageReceived(String message) {
        // [추가] 실시간 채팅 메시지 수신 처리
        if (message.startsWith("%ChatBroadcast%")) {
            // %ChatBroadcast%&chatRoomNum$1&userId$user1&text$안녕하세요%
            String[] parts = message.split("&");
            int chatRoomNum = -1;
            String userId = null;
            String text = null;
            for (String part : parts) {
                if (part.startsWith("chatRoomNum$")) {
                    chatRoomNum = Integer.parseInt(part.substring("chatRoomNum$".length()));
                } else if (part.startsWith("userId$")) {
                    userId = part.substring("userId$".length());
                } else if (part.startsWith("text$")) {
                    text = part.substring("text$".length()).replace("%", "");
                }
            }
            // 해당 채팅방의 메시지인 경우에만 처리
            if (chatRoomNum == targetChatRoomNum && userId != null && text != null) {
                // [추가] 로컬DB에 메시지 저장
                LocalDBManager dbManager = new LocalDBManager();
                Chat chat = new Chat();
                chat.setChatRoomNum(chatRoomNum);
                chat.setUserId(userId);
                chat.setText(text);
                chat.setTime(new Timestamp(System.currentTimeMillis()));
                dbManager.saveOrUpdateChat(chat);
                
                // 모든 메시지를 하나의 채팅 영역에 표시
                chatArea.append(userId + ": " + text + "\n");
            }
        }
    }

    @Override
    public void onChatRoomListUpdated(List<ChatRoom> rooms) {
        // 채팅창에서는 채팅방 목록 업데이트는 처리하지 않음
    }

    @Override
    public void onChatDataUpdated(int chatRoomNum, List<Chat> chats) {
        // 해당 채팅방의 데이터만 처리
        if (chatRoomNum == targetChatRoomNum) {
            System.out.println("[DEBUG] ChatWindowObserver: 채팅 데이터 업데이트 수신 - 방번호: " + chatRoomNum + ", 메시지 수: " + chats.size());
            LocalDBManager dbManager = new LocalDBManager();
            // 각 채팅을 로컬DB에 저장
            for (Chat chat : chats) {
                dbManager.saveOrUpdateChat(chat);
                System.out.println("[DEBUG] 채팅 저장: " + chat.getUserId() + " - " + chat.getText());
            }
            // 기존 내용 초기화
            chatArea.setText("");
            // DB에서 읽어와 표시
            List<Chat> loadedChats = dbManager.loadChatsForRoom(chatRoomNum);
            System.out.println("[DEBUG] 로컬 DB에서 로드한 채팅 수: " + loadedChats.size());
            for (Chat chat : loadedChats) {
                String displayText = chat.getUserId() + ": " + chat.getText() + "\n";
                chatArea.append(displayText);
                System.out.println("[DEBUG] 채팅 표시: " + displayText.trim());
            }
        }
    }
    
    // 옵저버 해제 메서드
    public void unregister() {
        ServerCallEventHandle.unregisterObserver(this);
    }
} 