package controller;

import model.DBManagerModule;
import model.UserData;

public class mainController {

	/**
	 * 사용자 전화번호로 친구 목록을 문자열로 반환
	 * 포맷: &friendIndex$0&friendId$abc123&friendName$김철수&friendProfile$Base64...&friendIndex$1...
	 * @param userPhoneNum 사용자 전화번호
	 * @return 문자열 포맷 결과
	 */
	public static String getFriendListAsString(String userPhoneNum) {
	    DBManagerModule db = new DBManagerModule();
	    String[] friendIds = db.loadFriend(userPhoneNum);  // 전화번호로 친구 ID 배열 받아오기
	    if (friendIds == null || friendIds.length == 0) return "";

	    StringBuilder builder = new StringBuilder();

	    for (int i = 0; i < friendIds.length; i++) {
	        UserData user = db.getUserDataById(friendIds[i]);
	        if (user == null) continue;

	        builder.append("&friendIndex$").append(i)
	               .append("&friendId$").append(friendIds[i])
	               .append("&friendName$").append(user.name)
	               .append("&friendProfile$").append(user.getProfileImage() != null ? user.getProfileImage() : "");
	    }

	    return builder.toString();
	}
	
	public static String login(String id, String password) {
		return "";
	}
	
	public static boolean isregistered(String phoneNum) {
		return true;
	}
	
	public static boolean register(String phoneNum) {
		return true;
	}
}