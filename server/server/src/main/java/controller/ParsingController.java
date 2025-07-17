package controller;

import java.util.*;

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

    public static String controllerHandle(String input) {
        String opcode = extractOpcode(input);
        String userId = extractUserId(input);
        DataStruct data = extractData(input);

        switch (opcode) {
            case "Login":
            	return mainController.login(data.id[0],data.password[0]);
            default:
                return "%ErrorUnknownOpcode%" + opcode;
        }
    }
}

