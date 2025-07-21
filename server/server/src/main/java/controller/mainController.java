package controller;

import java.util.List;

import model.DBManagerModule;
import model.UserData;
import model.ChatRoomData;
import model.ChatData;

/**
 * ToDo:인코딩문자열통일할것
 */

public class mainController {

	/**
	 * 사용자 전화번호로 친구 목록을 문자열로 반환
	 * 포맷: %LoadFriendData%&friendId$abc123&friendName$김철수&friendProfile$Base64...&....
	 * @param userPhoneNum 사용자 전화번호
	 * @return 문자열 포맷 결과
	 */
	public static String getFriendListAsString(String userPhoneNum) {
	    DBManagerModule db = new DBManagerModule();
	    String[] friendIds = db.loadFriend(userPhoneNum);  // 전화번호로 친구 ID 배열 받아오기
	    if (friendIds == null || friendIds.length == 0) return "";

	    StringBuilder builder = new StringBuilder();
        builder.append("%LoadFriendData%");
	    for (int i = 0; i < friendIds.length; i++) {
	        UserData user = db.getUserDataById(friendIds[i]);
	        if (user == null) continue;

	        builder.append("&friendId$").append(friendIds[i])
	               .append("&friendName$").append(user.name)
	               .append("&friendProfile$").append(user.getProfileImage() != null ? user.getProfileImage() : "");
	    }
        builder.append("%");
	    return builder.toString();
	}
	
	public static String login(String id, String password) {
	    DBManagerModule db = new DBManagerModule();
	    StringBuilder builder = new StringBuilder();
        builder.append("%Login%");
	    if(db.login(id, password)) {
	        UserData user = db.getUserDataById(id);
	        builder.append("&UserName$").append(user.name)
	               .append("&Profile$").append(user.getProfileImage() != null ? user.getProfileImage() : "");
	        // phoneNum도 포함
	        builder.append("&phoneNum$").append(db.getPhoneNumById(id));
	    }
	    else{
	        builder.append("Error:Login Failed");
	    }
        builder.append("%");
		return builder.toString();
	}
	public static String login(String phoneNum) {
	    DBManagerModule db = new DBManagerModule();
	    StringBuilder builder = new StringBuilder();
	    builder.append("%Login%");
	    if(db.login(phoneNum)) {
	        UserData user = db.getUserDataById(db.getIdByPhoneNum(phoneNum));
	        builder.append("&UserName$").append(user.name)
	               .append("&Profile$").append(user.getProfileImage() != null ? user.getProfileImage() : "");
	    }
	    else{
	        builder.append("Error:Login Failed");
	    }
        builder.append("%");
		return builder.toString();
	}
	public static String isregistered(String phoneNum) {
	    DBManagerModule db = new DBManagerModule();
		if(db.isRegisteredUser(phoneNum)) {
			return "%CheckRegistered%&isRegistered$true";
		}
		return "%CheckRegistered%&isRegistered$false";
	}
	
	public static String register(String id, String password, String name, String profileDir, String phoneNum) {
        DBManagerModule db = new DBManagerModule();
        if (db.isUserExists(id)) {
            return "%Register%&success$false%";
        }
        boolean ok = db.insertUser(id, password, name, profileDir, phoneNum);
        if (ok) {
            // 회원가입 성공 시 phoneNum도 포함해서 응답
            return "%Register%&success$true&phoneNum$" + phoneNum + "%";
        } else {
            return "%Register%&success$false%";
        }
    }
	
	public static String loadChatData(String chatRoom) {
	    int roomNum = Integer.parseInt(chatRoom);
	    DBManagerModule db = new DBManagerModule();
	    List<ChatData> chats = db.getChatData(roomNum);

	    StringBuilder builder = new StringBuilder();
	    builder.append("%LoadChatData%");

	    for (ChatData chat : chats) {
	        builder.append("&chatIndex$").append(chat.getChatIndex())
	               .append("&chatRoomNum$").append(chat.getChatRoomNum())
	               .append("&text$").append(chat.getText())
	               .append("&userId$").append(chat.getUserId())
	               .append("&time$").append(chat.getTime());
	    }

	    builder.append("%");
	    return builder.toString();
	}
	
	public static String uploadChat(int chatRoomNum, String text, String userId) {
		 DBManagerModule db = new DBManagerModule();
		    StringBuilder builder = new StringBuilder();
		    builder.append("%Chat%");
		 if(db.uploadChat(chatRoomNum, text, userId)){
		    builder.append("&result$SUCCESS");
		    builder.append("%");
		 }
		 else{
			    builder.append("&result$FAIED");
			    builder.append("%");
		 }
		 return builder.toString();
	}
	
	public static String loadChatRoomData(String id) {
        DBManagerModule db = new DBManagerModule();
        // == 매개변수 id를 사용하여 해당 사용자의 채팅방만 가져옵니다.
        List<ChatRoomData> rooms = db.loadChatRoom(id); 
        
        StringBuilder builder = new StringBuilder();
        builder.append("%LoadChatRoomData%");
        
        if (rooms != null) { // DB 조회 결과가 null이 아닌지 확인
            for (ChatRoomData room : rooms) {
                // 각 채팅방의 최근 메시지도 함께 가져오기
                String lastMessage = db.getLastMessageForRoom(room.chatRoomNum);
                
                builder.append("&chatRoomNum$").append(room.chatRoomNum)
                       .append("&roomType$").append(room.roomType)
                       .append("&roomName$").append(room.roomName)
                       .append("&lastMessage$").append(lastMessage != null ? lastMessage : "메시지 없음");
            }
        }
        
        builder.append("%");
        return builder.toString();
    }
    /**
     * 클라이언트가 보낸 채팅 메시지를 DB에 저장합니다.
     * @param chatRoomNum 채팅방 번호
     * @param userId 메시지를 보낸 사용자 ID
     * @param text 채팅 내용
     */
    public static void saveChatMessage(String chatRoomNum, String userId, String text) {
        // DB 작업을 위해 DBManagerModule 객체를 생성합니다.
        DBManagerModule db = new DBManagerModule();
        
        // DBManagerModule의 insertChatData 메서드를 호출하여 DB에 저장합니다.
        // String을 int로 변환해줍니다.
        try {
            int roomNum = Integer.parseInt(chatRoomNum);
            db.insertChatData(roomNum, userId, text);
        } catch (NumberFormatException e) {
            System.err.println("채팅방 번호 변환 오류: " + chatRoomNum);
        }
    }
    
    // 클라이언트에서 myId(내 id), targetId(상대 id/전화번호)로 요청 시 처리
    public static String addFriend(String myId, String targetId) {
        DBManagerModule db = new DBManagerModule();
        // 내 phoneNum 조회
        String myPhone = db.getPhoneNumById(myId);
        if (myPhone == null) {
            return "%ADDFRIEND%&error$내 정보(phoneNum)를 찾을 수 없습니다.%";
        }
        // 상대 phoneNum 조회 (id 또는 phoneNum 입력 가능)
        String targetPhone = db.getPhoneNumById(targetId);
        if (targetPhone == null) {
            targetPhone = targetId.matches("^01[0-9]{8,9}$") ? targetId : null;
        }
        if (targetPhone == null) {
            return "%ADDFRIEND%&error$상대방 정보를 찾을 수 없습니다.%";
        }
        // 친구 정보 조회
        String friendId = db.getIdByPhoneNum(targetPhone);
        String friendName = null;
        String friendProfileDir = null;
        try (java.sql.Connection conn = db.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement("SELECT name, profileDir FROM UserData WHERE phoneNum = ? LIMIT 1")) {
            pstmt.setString(1, targetPhone);
            java.sql.ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                friendName = rs.getString("name");
                friendProfileDir = rs.getString("profileDir");
            }
        } catch (Exception e) { e.printStackTrace(); }
        // 친구 관계 저장
        boolean ok = db.setFriendData(myPhone, targetPhone);
        if (ok) {
            return String.format("%%ADDFRIEND%%&phoneNum$%s&friendId$%s&friendName$%s&friendProfileDir$%s%%", targetPhone, friendId, friendName, friendProfileDir);
        } else {
            return "%ADDFRIEND%&error$이미 친구이거나 DB 오류입니다.%";
        }
    }

}
