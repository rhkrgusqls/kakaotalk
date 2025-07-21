// 파일 경로: server/src/main/java/controller/ParsingController.java
package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.DBManagerModule;

import observer.ChatGroupManager;

/**
 * ToDo:예외처리할것
 */
public class ParsingController {

    private static class DataStruct {
        String[] id;
        String[] password;
        String[] name;
        String[] profileDir;
        String[] phoneNum;
        String[] chatRoomNum;
        String[] chatData; // ➕ 추가
    }

    public static String extractOpcode(String input) {
        int start = input.indexOf('%');
        int end = input.indexOf('%', start + 1);
        if (start != -1 && end != -1) {
            return input.substring(start + 1, end);
        }
        return null;
    }

    public static String extractUserId(String input) {
        int lastPercent = input.lastIndexOf('%');
        if (lastPercent != -1 && lastPercent + 1 < input.length()) {
            return input.substring(lastPercent + 1);
        }
        return null;
    }

    public static DataStruct extractData(String input) {
        DataStruct data = new DataStruct();
        Map<String, List<String>> tempMap = new HashMap<>();

        int firstPercent = input.indexOf('%');
        int secondPercent = input.indexOf('%', firstPercent + 1);
        int lastPercent = input.lastIndexOf('%');

        if (firstPercent == -1 || secondPercent == -1 || lastPercent == -1 || secondPercent >= lastPercent) {
            return data;
        }

        String dataSection = input.substring(secondPercent + 1, lastPercent);
        String[] entries = dataSection.split("&");

        for (String entry : entries) {
            if (entry.isEmpty()) continue;
            String[] kv = entry.split("\\$", 2);
            if (kv.length == 2) {
                tempMap.computeIfAbsent(kv[0], k -> new ArrayList<>()).add(kv[1]);
            }
        }

        data.id = toArray(tempMap.get("id"));
        data.password = toArray(tempMap.get("password"));
        data.name = toArray(tempMap.get("name"));
        data.profileDir = toArray(tempMap.get("profileDir"));
        data.phoneNum = toArray(tempMap.get("phoneNum"));
        data.chatRoomNum = toArray(tempMap.get("chatRoomNum"));
        data.chatData = toArray(tempMap.get("chatData")); // ➕ 추가

        return data;
    }

    private static String[] toArray(List<String> list) {
        return list != null ? list.toArray(new String[0]) : null;
    }
    
    // 중복 정의된 controllerHandle 메서드를 하나로 합침
    public static String controllerHandle(String input) {
        String opcode = extractOpcode(input);
        DataStruct data = extractData(input);
        String senderId = extractSenderUserId(input);
        // 데이터가 없는 경우를 대비한 방어 코드
        if (data.id == null || data.id.length == 0) {
            // 로그인 외 다른 요청들을 위해 opcode만 보고 분기할 수도 있음
            // 예: case "GET_SERVER_TIME": return mainController.getServerTime();
        }

        switch (opcode) {
            case "Login":
                if (data.id != null && data.id.length > 0 && data.password != null && data.password.length > 0) {
                    return mainController.login(data.id[0], data.password[0]);
                } else {
                    return "%Error%&message$ID or Password missing%";
                }
            case "Chat":
                Map<String, String> chatMap = extractDataToMap(input);
                String chatRoomNumStr = chatMap.get("chatRoomNum");
                String userId = chatMap.get("userId");
                String text = chatMap.get("text");
                if (chatRoomNumStr != null && userId != null && text != null) {
                    mainController.broadcastChatMessage(Integer.parseInt(chatRoomNumStr), userId, text);
                }
                int chatRoomNum = Integer.parseInt(data.chatRoomNum[0]);
                String chatText = data.chatData[0];
                String response = mainController.uploadChat(chatRoomNum, chatText, senderId);
                String reloadMessage = "%ReloadChatRoom%&chatRoomNum$" + chatRoomNum + "%";
                ChatGroupManager.getInstance().notifyGroup(String.valueOf(chatRoomNum), reloadMessage);
                return response;

            case "ADDFRIEND":
                // myId, targetId 파싱
                Map<String, String> addFriendMap = extractDataToMap(input);
                String myId = addFriendMap.get("myId");
                String targetId = addFriendMap.get("targetId");
                if (myId != null && targetId != null) {
                    return mainController.addFriend(myId, targetId);
                } else {
                    return "%Error%&message$myId or targetId missing for ADDFRIEND%";
                }
            case "LoadChatRoomData":
            	if (data.id != null && data.id.length > 0) {
                    return mainController.loadChatRoomData(data.id[0]);
                } else {
                    return "%Error%&message$User ID is missing for LoadChatRoomData%";
                }
            case "chatListLoad":
                if (data.id != null && data.id.length > 0) {
                    return mainController.loadChatRoomData(data.id[0]);
                } else {
                    return "%Error%&message$User ID is missing for chatListLoad%";
                }
            case "LoadChatData":
                if (data.chatRoomNum != null && data.chatRoomNum.length > 0) {
                    return mainController.loadChatData(data.chatRoomNum[0]);
                } else {
                    return "%Error%&message$Chat room number is missing for LoadChatData%";
                }
            case "Register":
                if (data.id != null && data.password != null && data.name != null && data.profileDir != null && data.phoneNum != null) {
                    return mainController.register(data.id[0], data.password[0], data.name[0], data.profileDir[0], data.phoneNum[0]);
                } else {
                    return "%Register%&success$false%";
                }
            case "CreateChatRoom":
                // id(여러 명) 파싱 (chatRoomNum은 서버에서 생성)
                String customRoomName = null;
                if (data.name != null && data.name.length > 0) {
                    customRoomName = data.name[0];
                }
                if (data.id != null && data.id.length > 0) {
                    // 서버에서 채팅방 번호 생성
                    int chatRoomNum2 = (int)(System.currentTimeMillis() / 1000) + (int)(Math.random() * 1000);
                    List<String> memberIds = new ArrayList<>();
                    DBManagerModule db = new DBManagerModule();
                    for (String id : data.id) {
                        // 전화번호인 경우 ID로 변환
                        String memberId = id;
                        if (id.matches("^01[0-9]{8,9}$")) {
                            String convertedId = db.getIdByPhoneNum(id);
                            if (convertedId != null) {
                                memberId = convertedId;
                            }
                        }
                        memberIds.add(memberId);
                    }
                    return mainController.createChatRoomWithMembers(chatRoomNum2, memberIds, customRoomName);
                } else {
                    return "%Error%&message$memberIds missing for CreateChatRoom%";
                }
            case "ResetPassword":
                // 비밀번호 재설정 처리
                Map<String, String> resetMap = extractDataToMap(input);
                String resetId = resetMap.get("id");
                String currentPassword = resetMap.get("currentPassword");
                String newPassword = resetMap.get("newPassword");
                if (resetId != null && currentPassword != null && newPassword != null) {
                    return mainController.resetPassword(resetId, currentPassword, newPassword);
                } else {
                    return "%ResetPassword%&success$false&error$필수 정보가 누락되었습니다.%";
                }
            case "DeleteChatRoom":
                // 채팅방 삭제 처리
                Map<String, String> deleteMap = extractDataToMap(input);
                String deleteChatRoomNum = deleteMap.get("chatRoomNum");
                String deleteUserId = deleteMap.get("userId");
                if (deleteChatRoomNum != null && deleteUserId != null) {
                    return mainController.deleteChatRoom(Integer.parseInt(deleteChatRoomNum), deleteUserId);
                } else {
                    return "%DeleteChatRoom%&success$false&error$채팅방 번호 또는 사용자 ID가 누락되었습니다.%";
                }
            // 여기에 친구 목록, 채팅방 목록 등 다른 OPCODE에 대한 처리를 추가
            // case "LoadFriendData":
            //     return mainController.getFriendListAsString(data.phoneNum[0]);

            default:
                return "%Error%&message$Unknown Opcode:" + opcode + "%";
        }
    }

    // [수정] 중복 정의된 extractDataToMap 메서드를 하나만 남김
    public static Map<String, String> extractDataToMap(String request) {
        Map<String, String> dataMap = new HashMap<>();
        try {
            String dataString = request.substring(request.indexOf("%", 1) + 1, request.lastIndexOf('%'));
            
            if (dataString.isEmpty()) {
                return dataMap;
            }
            
            String[] pairs = dataString.split("&");
            for (String pair : pairs) {
                if (pair.isEmpty()) continue;
                String[] keyValue = pair.split("\\$", 2);
                if (keyValue.length == 2) {
                    dataMap.put(keyValue[0], keyValue[1]);
                }
            }
        } catch (Exception e) {
            System.err.println("데이터를 Map으로 파싱하는 중 오류 발생: " + e.getMessage());
        }
        return dataMap;
    }
    
    
    public static String extractSenderUserId(String input) {
        try {
            int firstPercent = input.indexOf('%');
            int secondPercent = input.indexOf('%', firstPercent + 1);
            int lastPercent = input.lastIndexOf('%');
            if (firstPercent == -1 || secondPercent == -1 || lastPercent == -1 || secondPercent >= lastPercent) {
                return null;
            }

            // opcode 끝 다음부터 마지막 % 전까지 데이터 영역
            String dataSection = input.substring(secondPercent + 1, lastPercent);

            // 데이터 구간에서 &user$값 찾기
            String[] entries = dataSection.split("&");
            for (String entry : entries) {
                if (entry.startsWith("user$")) {
                    return entry.substring("user$".length());
                }
            }
        } catch (Exception e) {
            System.err.println("extractSenderUserId error: " + e.getMessage());
        }
        return null;
    }
    
}
