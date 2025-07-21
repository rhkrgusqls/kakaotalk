package controller;

import java.util.List;

import model.DBManagerModule;
import model.UserData;
import model.ChatRoomData;
import model.ChatData;

import console.UserManager;

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
		    builder.append("&chatRoomNum$").append(chatRoomNum);
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
        // 내가 참여자인 채팅방만 조회 (ChatRoomMember를 통해 접근)
        List<ChatRoomData> rooms = db.loadChatRoomsForUser(id);
        
        StringBuilder builder = new StringBuilder();
        builder.append("%LoadChatRoomData%");
        
        if (rooms != null) { // DB 조회 결과가 null이 아닌지 확인
            for (ChatRoomData room : rooms) {
                // 각 채팅방의 최근 메시지와 시간도 함께 가져오기
                String lastMessage = db.getLastMessageForRoom(room.chatRoomNum);
                String lastMessageTime = db.getLastMessageTimeForRoom(room.chatRoomNum);
                
                builder.append("&chatRoomNum$").append(room.chatRoomNum)
                       .append("&roomType$").append(room.roomType)
                       .append("&roomName$").append(room.roomName)
                       .append("&lastMessage$").append(lastMessage != null ? lastMessage : "메시지 없음")
                       .append("&lastMessageTime$").append(lastMessageTime != null ? lastMessageTime : "");
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
        // 친구 관계 저장 (양방향)
        boolean ok = db.setFriendData(myPhone, targetPhone);
        if (ok) {
            // 상대방에게도 친구 추가 알림 전송
            UserManager.getInstance().sendMessageToUser(friendId, 
                "%FriendAdded%&friendId$" + myId + "&friendName$" + db.getUserNameById(myId) + "%");
            // [추가] 양쪽 모두에게 친구 목록 갱신 알림 전송
            UserManager.getInstance().sendMessageToUser(myId, "%FriendListUpdated%&userId$" + myId + "%");
            UserManager.getInstance().sendMessageToUser(friendId, "%FriendListUpdated%&userId$" + friendId + "%");
            return String.format("%%ADDFRIEND%%&phoneNum$%s&friendId$%s&friendName$%s&friendProfileDir$%s%%", targetPhone, friendId, friendName, friendProfileDir);
        } else {
            // 이미 친구여도 양쪽 모두에게 갱신 메시지 전송
            UserManager.getInstance().sendMessageToUser(myId, "%FriendListUpdated%&userId$" + myId + "%");
            UserManager.getInstance().sendMessageToUser(friendId, "%FriendListUpdated%&userId$" + friendId + "%");
            return "%ADDFRIEND%&error$이미 친구이거나 DB 오류입니다.%";
        }
    }

    // [수정] 방 생성 시 ChatRoomList와 ChatRoomMember 모두에 저장
    public static String createChatRoomWithMembers(int chatRoomNum, List<String> memberIds) {
        return createChatRoomWithMembers(chatRoomNum, memberIds, null);
    }

    // [추가] 채팅 메시지 저장 후 해당 방 참여자에게 메시지 중계
    public static void broadcastChatMessage(int chatRoomNum, String userId, String text) {
        // 1. DB에 저장
        DBManagerModule db = new DBManagerModule();
        db.insertChatData(chatRoomNum, userId, text);
        // 2. 참여자 목록 조회
        List<String> memberIds = db.getChatRoomMemberIds(chatRoomNum);
        // 3. 각 참여자에게 메시지 전송
        String msg = String.format("%%ChatBroadcast%%&chatRoomNum$%d&userId$%s&text$%s%%", chatRoomNum, userId, text);
        for (String memberId : memberIds) {
            UserManager.getInstance().sendMessageToUser(memberId, msg);
            System.out.println("[브로드캐스트] to " + memberId + ": " + msg);
        }
    }

    // [추가] 비밀번호 재설정
    public static String resetPassword(String id, String currentPassword, String newPassword) {
        DBManagerModule db = new DBManagerModule();
        
        try (java.sql.Connection conn = db.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT password FROM UserData WHERE id = ?")) {
            pstmt.setString(1, id);
            java.sql.ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                if (storedPassword.equals(currentPassword)) {
                    // 현재 비밀번호가 맞으면 새 비밀번호로 업데이트
                    try (java.sql.PreparedStatement updateStmt = conn.prepareStatement(
                        "UPDATE UserData SET password = ? WHERE id = ?")) {
                        updateStmt.setString(1, newPassword);
                        updateStmt.setString(2, id);
                        updateStmt.executeUpdate();
                        return "%ResetPassword%&success$true%";
                    }
                } else {
                    return "%ResetPassword%&success$false&error$현재 비밀번호가 올바르지 않습니다.%";
                }
            } else {
                return "%ResetPassword%&success$false&error$존재하지 않는 사용자입니다.%";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "%ResetPassword%&success$false&error$비밀번호 변경 중 오류가 발생했습니다.%";
        }
    }

    // [추가] 채팅방 삭제
    public static String deleteChatRoom(int chatRoomNum, String userId) {
        DBManagerModule db = new DBManagerModule();
        
        try (java.sql.Connection conn = db.getConnection()) {
            // 1. 해당 사용자가 채팅방의 참여자인지 확인
            try (java.sql.PreparedStatement checkStmt = conn.prepareStatement(
                "SELECT COUNT(*) FROM ChatRoomMember WHERE chatRoomNum = ? AND id = ?")) {
                checkStmt.setInt(1, chatRoomNum);
                checkStmt.setString(2, userId);
                java.sql.ResultSet rs = checkStmt.executeQuery();
                
                if (rs.next() && rs.getInt(1) > 0) {
                    // 2. ChatRoomMember에서 해당 사용자 제거
                    try (java.sql.PreparedStatement deleteMemberStmt = conn.prepareStatement(
                        "DELETE FROM ChatRoomMember WHERE chatRoomNum = ? AND id = ?")) {
                        deleteMemberStmt.setInt(1, chatRoomNum);
                        deleteMemberStmt.setString(2, userId);
                        deleteMemberStmt.executeUpdate();
                    }
                    
                    // 3. 해당 채팅방의 남은 참여자 수 확인
                    try (java.sql.PreparedStatement countStmt = conn.prepareStatement(
                        "SELECT COUNT(*) FROM ChatRoomMember WHERE chatRoomNum = ?")) {
                        countStmt.setInt(1, chatRoomNum);
                        java.sql.ResultSet countRs = countStmt.executeQuery();
                        
                        if (countRs.next() && countRs.getInt(1) == 0) {
                            // 참여자가 없으면 채팅방과 관련 데이터 모두 삭제
                            try (java.sql.PreparedStatement deleteChatStmt = conn.prepareStatement(
                                "DELETE FROM ChatList WHERE chatRoomNum = ?")) {
                                deleteChatStmt.setInt(1, chatRoomNum);
                                deleteChatStmt.executeUpdate();
                            }
                            
                            try (java.sql.PreparedStatement deleteRoomStmt = conn.prepareStatement(
                                "DELETE FROM ChatRoomList WHERE chatRoomNum = ?")) {
                                deleteRoomStmt.setInt(1, chatRoomNum);
                                deleteRoomStmt.executeUpdate();
                            }
                        }
                    }
                    
                    // 4. 남은 참여자들에게 채팅방 목록 업데이트 알림
                    List<String> remainingMembers = db.getChatRoomMemberIds(chatRoomNum);
                    for (String memberId : remainingMembers) {
                        UserManager.getInstance().sendMessageToUser(memberId, 
                            "%ChatRoomListUpdated%&userId$" + memberId + "%");
                    }
                    
                    return "%DeleteChatRoom%&success$true%";
                } else {
                    return "%DeleteChatRoom%&success$false&error$해당 채팅방에 참여하고 있지 않습니다.%";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "%DeleteChatRoom%&success$false&error$채팅방 삭제 중 오류가 발생했습니다.%";
        }
    }

    public static String createChatRoomWithMembers(int chatRoomNum, List<String> memberIds, String customRoomName) {
        DBManagerModule db = new DBManagerModule();
        // 1. 채팅방 번호가 이미 존재하는지 확인
        try (java.sql.Connection conn = db.getConnection();
             java.sql.PreparedStatement checkStmt = conn.prepareStatement(
                 "SELECT COUNT(*) FROM ChatRoomList WHERE chatRoomNum = ?")) {
            checkStmt.setInt(1, chatRoomNum);
            java.sql.ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                // 이미 존재하는 경우 새로운 번호 생성
                chatRoomNum = (int)(System.currentTimeMillis() / 1000) + (int)(Math.random() * 1000);
                System.out.println("[DEBUG] 채팅방 번호 중복, 새로운 번호 생성: " + chatRoomNum);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "%CreateChatRoom%&success$false%";
        }
        String roomType = "1:1";
        String roomName;
        if (customRoomName != null && !customRoomName.isEmpty()) {
            roomName = customRoomName;
        } else if (memberIds.size() == 2) {
            String creatorName = db.getUserNameById(memberIds.get(0));
            roomName = creatorName != null ? creatorName + "와의 1:1 채팅" : "1:1 채팅방";
        } else {
            roomName = "그룹 채팅방 " + chatRoomNum;
        }
        try (java.sql.Connection conn = db.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(
                 "INSERT INTO ChatRoomList(chatRoomNum, roomType, roomName) VALUES (?, ?, ?)")) {
            pstmt.setInt(1, chatRoomNum);
            pstmt.setString(2, roomType);
            pstmt.setString(3, roomName);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return "%CreateChatRoom%&success$false%";
        }
        int successCount = 0;
        for (String userId : memberIds) {
            if (db.insertChatRoomMember(chatRoomNum, userId)) {
                successCount++;
            }
        }
        for (String userId : memberIds) {
            String notificationMsg = "%ChatRoomListUpdated%&userId$" + userId + "%";
            UserManager.getInstance().sendMessageToUser(userId, notificationMsg);
            System.out.println("[DEBUG] 채팅방 생성 알림 전송: " + userId + " -> " + notificationMsg);
        }
        if (successCount == memberIds.size()) {
            String broadcastMsg = String.format("%%ChatRoomCreated%%&chatRoomNum$%d&roomType$%s&roomName$%s%%", 
                chatRoomNum, roomType, roomName);
            for (String memberId : memberIds) {
                try {
                    UserManager.getInstance().sendMessageToUser(memberId, broadcastMsg);
                    System.out.println("[DEBUG] 채팅방 생성 알림 전송 to " + memberId + ": " + broadcastMsg);
                } catch (Exception e) {
                    System.out.println("[ERROR] 채팅방 생성 알림 전송 실패 to " + memberId + ": " + e.getMessage());
                }
            }
            return "%CreateChatRoom%&success$true&chatRoomNum$" + chatRoomNum + "%";
        } else {
            return "%CreateChatRoom%&success$partial%";
        }
    }
}
