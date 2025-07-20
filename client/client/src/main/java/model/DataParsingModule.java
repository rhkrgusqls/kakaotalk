package model;

public class DataParsingModule {
    private String id;
    private String userName;
    private String chatName;
    private String profile;
    private String phoneNum; // ✅ 추가

    public DataParsingModule() {}

    // 데이터를 파싱해서 필드에 저장하는 메서드
    public void parseData(String input) {
        id = null;
        userName = null;
        chatName = null;
        profile = null;
        phoneNum = null;

        String[] parts = input.split("&");
        for (String part : parts) {
            if (part.contains("$")) {
                String[] kv = part.split("\\$", 2);
                if (kv.length < 2) continue;

                String key = kv[0];
                String value = kv[1].trim();
                if (value.endsWith("%")) {
                    value = value.substring(0, value.length() - 1);
                }

                switch (key) {
                    case "id":
                        id = value;
                        break;
                    case "UserName":
                        userName = value;
                        break;
                    case "ChatName":
                        chatName = value;
                        break;
                    case "Profile":
                        profile = value;
                        break;
                    case "phoneNum":
                        phoneNum = value;
                        break;
                }
            }
        }
        // phoneNum 파싱 결과 로그
        System.out.println("[DEBUG] DataParsingModule.parseData() phoneNum=" + phoneNum);
    }


    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getChatName() {
        return chatName;
    }

    public String getProfile() {
        return profile;
    }

    public String getPhoneNum() { // ✅ 추가
        return phoneNum;
    }
}

