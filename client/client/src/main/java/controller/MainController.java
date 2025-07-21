package controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

import model.Chat;
import model.ChatRoom;
import model.DBManager;
import model.DataParsingModule;
import model.TCPManager;
import model.User;
import model.LocalDBManager;
import observer.Observer;
import observer.ServerCallEventHandle;

public class MainController implements Observer {
	private static User loggedInUser; 
    static TCPManager tcp;
    static MainController instance = new MainController();

    private MainController() {
        tcp = TCPManager.getInstance();
        tcp.connect();
        System.out.println("[LOG][TCP]:TCP 연결시도");
        if (tcp.isConnected()) {
            System.out.println("TCP 연결 성공");
        }
        // 비동기 메시지 수신을 위해 옵저버 등록
        ServerCallEventHandle.registerObserver(this);
    }

    public static MainController getInstance() {
        return instance;
    }


    
    // 동기 로그인 처리
    public static boolean login(String id, String password) {
        DataParsingModule data = new DataParsingModule();
        if (tcp.isConnected()) {
            System.out.println("TCP 연결 성공! 로그인 메시지를 보냅니다.");
            String response = tcp.sendSyncMessage("%Login%&id$" + id + "&password$" + password + "%");
            if (response != null) {
                data.parseData(response);
            } else {
                System.err.println("[ERROR] 서버 응답이 null입니다.");
                return false;
            }
        } else {
            System.out.println("TCP 연결 실패!");
            return false;
        }
        if (data.getUserName() == null) {
            return false;
        }
        // phoneNum도 User 객체에 세팅
        User myData = new User(id, password, data.getUserName(), "./profile/self/coldplay.jpg", data.getPhoneNum());
        loggedInUser = myData;
        // 내 로컬DB 자동 탐색: 1~8번 DB 중 내 id가 저장된 DB를 찾음
        String myLocalDb = null;
        for (int i = 1; i <= 8; i++) {
            String dbName = "kakaotalkUser" + i + "TestData";
            DBManager.getInstance().setDBName(dbName);
            if (DBManager.getInstance().idExistsInDB(dbName, id)) {
                myLocalDb = dbName;
                break;
            }
        }
        if (myLocalDb != null) {
            DBManager.getInstance().setDBName(myLocalDb);
            System.out.println("[DEBUG] 로그인 후 DB: " + DBManager.getInstance().getCurrentDBName());
        }
        // saveUser 호출 전 phoneNum 로그 출력
        System.out.println("[DEBUG] saveUser 호출 전 user.getPhoneNum() = " + myData.getPhoneNum());
        DBManager.getInstance().saveUser(myData);
        // 앱 시작 시 채팅방 목록 동기화 메시지 전송
        String syncMsg = "%chatListLoad%&id$" + id + "%";
        tcp.sendSyncMessage(syncMsg);
        requestChatRoomsFromServer(id);
        return true;
    }
    
    public static User getLoggedInUser() {
        return loggedInUser;
    }
    

    
    // 비동기 메시지를 수신하는 콜백 (Observer 인터페이스 구현)
    @Override
    public void onMessageReceived(String message) {
        System.out.println("[ASYNC] 서버로부터 비동기 메시지 수신: " + message);

        if (message.startsWith("%chatListLoad%")) {
            List<ChatRoom> rooms = parseChatListLoad(message);
            LocalDBManager db = new LocalDBManager();
            for (ChatRoom room : rooms) db.saveOrUpdateChatRoom(room);
            // UI 새로고침 신호
            ServerCallEventHandle.notifyChatRoomListUpdated(db.loadChatRooms());
        }
        if (message.startsWith("%Notice%")) {
            String content = parseValue(message, "content");
        }
        // 추가적인 메시지 유형 처리 가능
    }

    // 메시지 안의 key$value 형태 파싱 헬퍼
    public static String parseValue(String message, String key) {
        if (message == null) return null;
        String[] parts = message.split("&");
        for (String part : parts) {
            if (part.startsWith(key + "$")) {
                return part.substring((key + "$" ).length()).replace("%", "");
            }
        }
        return null;
    }
    
    public static void sendChatMessage(int chatRoomNum, String message) {
    	User userId = getLoggedInUser();
    	
        if (userId.getId() == null || userId.getId().isEmpty()) {
            System.err.println("User is not logged in.");
            return;
        }
	    StringBuilder builder = new StringBuilder();
        builder.append("%LoadFriendData%");
        builder.append("&chatRoomNum$").append(chatRoomNum)
               .append("&chatData$").append(message)
               .append("%&user$").append(userId.getId());

        tcp.sendMessage(builder.toString());
    }

    
    public static boolean addFriend(String input) {
        // 서버에 ADDFRIEND 요청 (내 id와 친구 입력값 모두 전송)
        User me = getLoggedInUser();
        if (me == null) {
            JOptionPane.showMessageDialog(null, "로그인 정보가 없습니다.");
            return false;
        }
        String myId = me.getId();
        String msg = String.format("%%ADDFRIEND%%&myId$%s&targetId$%s%%", myId, input);
        String response = tcp.sendSyncMessage(msg);
        if (response != null && response.contains("phoneNum$")) {
            String friendPhoneNum = parseValue(response, "phoneNum");
            String friendId = parseValue(response, "friendId");
            String friendName = parseValue(response, "friendName");
            String friendProfileDir = parseValue(response, "friendProfileDir");
            // UserData에 친구 정보 저장 (없으면 insert, 있으면 update)
            if (friendId != null && friendName != null && friendProfileDir != null && friendPhoneNum != null) {
                DBManager.getInstance().saveUser(new User(friendId, "", friendName, friendProfileDir, friendPhoneNum));
            }
            DBManager.getInstance().saveFriend(friendPhoneNum);
            JOptionPane.showMessageDialog(null, "친구 추가 성공!");
            return true;
        } else {
            String errorMsg = parseValue(response, "error");
            if (errorMsg == null) errorMsg = "존재하지 않는 회원입니다.";
            JOptionPane.showMessageDialog(null, errorMsg);
            return false;
        }
    }
    //서버에 채팅방 목록을 요청하고 결과를 파싱하는 메서드
    public static void requestChatRoomsFromServer(String userId) {
        new Thread(() -> {
            // [추가합니다] 서버에 요청 메시지 전송
            String requestMessage = "%LoadChatRoomData%&id$" + userId + "%";
            String response = tcp.sendSyncMessage(requestMessage);
            
            // [수정합니다] UI 업데이트는 Swing EDT에서 처리
            SwingUtilities.invokeLater(() -> {
                if (response != null && response.startsWith("%LoadChatRoomData%")) {
                    System.out.println("[LOG] 서버로부터 채팅방 데이터 수신 성공. 파싱 및 UI 업데이트 시작...");
                    List<ChatRoom> chatRooms = parseChatRoomResponse(response);
                    // [이 줄이 실행되는지 확인]
                    System.out.println("[LOG] 파싱 완료. 방 개수: " + chatRooms.size());
                    ServerCallEventHandle.notifyChatRoomListUpdated(chatRooms); // [이 줄이 핵심]
                } else {
                    System.err.println("[ERROR] 서버 응답 실패 또는 잘못된 응답: " + response);
                }
            });
        }).start();
    }
  
    private static List<ChatRoom> parseChatRoomResponse(String response) {
        List<ChatRoom> rooms = new ArrayList<>();
        try {
            String dataPart = response.substring(response.indexOf('%', 1) + 1, response.lastIndexOf('%'));
            String[] entries = dataPart.split("&chatRoomNum\\$");
            for (int i = 1; i < entries.length; i++) {
                String entry = entries[i];
                String[] parts = entry.split("&");
                ChatRoom room = new ChatRoom();
                room.setChatRoomNum(Integer.parseInt(parts[0].trim()));
                for (int j = 1; j < parts.length; j++) {
                    String[] kv = parts[j].split("\\$", 2);
                    if (kv.length == 2) {
                        if (kv[0].equals("roomType")) room.setRoomType(kv[1]);
                        else if (kv[0].equals("roomName")) room.setRoomName(kv[1]);
                        else if (kv[0].equals("lastMessage")) room.setLastMessage(kv[1]);
                    }
                }
                rooms.add(room);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return rooms;
    }
    @Override
    public void onChatRoomListUpdated(List<ChatRoom> rooms) {
        // MainController는 이벤트를 전송하는 역할만 하므로, 수신부는 비움
    }
    
    @Override
    public void onChatDataUpdated(int chatRoomNum, List<Chat> chats) {
        // MainController는 이벤트를 전송하는 역할만 하므로, 수신부는 비움
    }
    
    // 채팅방의 채팅 데이터를 서버에서 요청하는 메서드
    public static void requestChatDataFromServer(int chatRoomNum) {
        new Thread(() -> {
            String requestMessage = "%LoadChatData%&chatRoomNum$" + chatRoomNum + "%";
            String response = tcp.sendSyncMessage(requestMessage);
            
            SwingUtilities.invokeLater(() -> {
                if (response != null && response.startsWith("%LoadChatData%")) {
                    System.out.println("[LOG] 서버로부터 채팅 데이터 수신 성공. 파싱 및 UI 업데이트 시작...");
                    List<Chat> chats = parseChatResponse(response);
                    System.out.println("[LOG] 파싱 완료. 채팅 개수: " + chats.size());
                    ServerCallEventHandle.notifyChatDataUpdated(chatRoomNum, chats);
                } else {
                    System.err.println("[ERROR] 서버 응답 실패 또는 잘못된 응답: " + response);
                }
            });
        }).start();
    }
    
    private static List<Chat> parseChatResponse(String response) {
        List<Chat> chats = new ArrayList<>();
        try {
            String dataPart = response.substring(response.indexOf('%', 1) + 1, response.lastIndexOf('%'));
            String[] entries = dataPart.split("&chatIndex\\$");
            for (int i = 1; i < entries.length; i++) {
                String entry = entries[i];
                String[] parts = entry.split("&");
                Chat chat = new Chat();
                chat.setChatIndex(Integer.parseInt(parts[0].trim()));
                for (int j = 1; j < parts.length; j++) {
                    String[] kv = parts[j].split("\\$", 2);
                    if (kv.length == 2) {
                        if (kv[0].equals("chatRoomNum")) chat.setChatRoomNum(Integer.parseInt(kv[1]));
                        else if (kv[0].equals("text")) chat.setText(kv[1]);
                        else if (kv[0].equals("userId")) chat.setUserId(kv[1]);
                        else if (kv[0].equals("time")) {
                            try {
                                chat.setTime(java.sql.Timestamp.valueOf(kv[1]));
                            } catch (Exception e) {
                                System.err.println("시간 파싱 오류: " + kv[1]);
                            }
                        }
                    }
                }
                chats.add(chat);
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return chats;
    }

    // chatListLoad 메시지 파싱 함수
    private List<ChatRoom> parseChatListLoad(String message) {
        List<ChatRoom> rooms = new ArrayList<>();
        try {
            String dataPart = message.substring(message.indexOf('%', 1) + 1, message.lastIndexOf('%'));
            String[] entries = dataPart.split("&chatRoomNum\\$");
            for (int i = 1; i < entries.length; i++) {
                String entry = entries[i];
                String[] parts = entry.split("&");
                ChatRoom room = new ChatRoom();
                room.setChatRoomNum(Integer.parseInt(parts[0].trim()));
                for (int j = 1; j < parts.length; j++) {
                    String[] kv = parts[j].split("\\$", 2);
                    if (kv.length == 2) {
                        if (kv[0].equals("roomType")) room.setRoomType(kv[1]);
                        else if (kv[0].equals("roomName")) room.setRoomName(kv[1]);
                        else if (kv[0].equals("lastMessage")) room.setLastMessage(kv[1]);
                    }
                }
                rooms.add(room);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return rooms;
    }
}

