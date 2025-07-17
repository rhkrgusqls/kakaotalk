package controller;

import java.util.List;

import model.DBManagerModule;
import model.UserData;
import model.ChatRoomData;
import model.ChatData;

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
	               .append("&friendProfile$").append(user.getProfileImage() != null ? user.getProfileImage() : "");
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
	    if(db.login(phoneNum)) {
	        UserData user = db.getUserDataById(db.getIdByPhoneNum(phoneNum));
	        builder.append("%Login%")
	               .append("&UserName$").append(user.name)
	               .append("&friendProfile$").append(user.getProfileImage() != null ? user.getProfileImage() : "");
	    }
	    else{
	        builder.append("%Login%Error:Login Failed");
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
        List<ChatRoomData> rooms = db.loadChatRoom(id);
        StringBuilder builder = new StringBuilder();
        builder.append("%LoadChatRoomData%");
        for (ChatRoomData room : rooms) {
            builder.append("&chatRoomNum$").append(room.chatRoomNum)
                   .append("&roomType$").append(room.roomType)
                   .append("&roomName$").append(room.roomName);
        }
        builder.append("%");
        return builder.toString();
    }
}