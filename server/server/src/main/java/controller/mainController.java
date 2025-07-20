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
		if(db.isRegisteredUser(phoneNum)) {
			return "%Register%&success$true";
		}
		return "%Register%&success$false";
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
	
	public static String loadChatRoomData(String id) {
        DBManagerModule db = new DBManagerModule();
        // == 매개변수 id를 사용하여 해당 사용자의 채팅방만 가져옵니다.
        List<ChatRoomData> rooms = db.loadChatRoom(id); 
        
        StringBuilder builder = new StringBuilder();
        builder.append("%LoadChatRoomData%");
        
        if (rooms != null) { // DB 조회 결과가 null이 아닌지 확인
            for (ChatRoomData room : rooms) {
                builder.append("&chatRoomNum$").append(room.chatRoomNum)
                       .append("&roomType$").append(room.roomType)
                       .append("&roomName$").append(room.roomName);
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
    
    public static String addFriend(String userId, String id, String phoneNum) {
        DBManagerModule db = new DBManagerModule();
        String resultPhone = db.addFriend(userId, id, phoneNum);

        StringBuilder builder = new StringBuilder();
        builder.append("%ADDFRIEND%");
        
        if (resultPhone != null && !resultPhone.startsWith("DB 처리 중 오류") && !resultPhone.startsWith("해당 유저")) {
            builder.append("&phoneNum$").append(resultPhone);
        } else {
            builder.append("&error$").append(resultPhone); // 오류 메시지도 전송
        }

        builder.append("%");
        return builder.toString();
    }

}
