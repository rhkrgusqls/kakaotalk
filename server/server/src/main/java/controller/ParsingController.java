// 파일 경로: server/src/main/java/controller/ParsingController.java
package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        return data;
    }

    private static String[] toArray(List<String> list) {
        return (list == null || list.isEmpty()) ? new String[0] : list.toArray(new String[0]);
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
                // Chat은 실시간 전송 후 DB 저장만 하므로, 여기서는 별도의 응답을 주지 않음
                return ""; // 성공했다는 의미로 ACK(Acknowledge) 응답을 보내거나 빈 문자열 반환
            case "ADDFRIEND":
                // id와 phoneNum은 DataStruct에 배열로 담겨있음, 보통 한개씩
                if (data.id != null && data.id.length > 0 && data.phoneNum != null && data.phoneNum.length > 0) {
                    return mainController.addFriend(senderId,data.id[0],data.phoneNum[0]);
                } else {
                    return "%Error%&message$id or phoneNum missing for ADDFRIEND%";
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
