package model;

public class DataParsingModule {
    private String id;
    private String userName;
    private String chatName;
    private String profile;

    public DataParsingModule() {}

    // 데이터를 파싱해서 필드에 저장하는 메서드
    public void parseData(String input) {
        // 입력 예: %Login%&UserName$안예슬&Profile$iVBORw
        // 먼저 %Login% 같은 토큰을 처리하거나 무시 가능
        // & 변수명 $ 값 의 형태 반복으로 가정

        // 초기화
        id = null;
        userName = null;
        chatName = null;
        profile = null;

        // 입력 문자열을 &로 분리
        String[] parts = input.split("&");
        for (String part : parts) {
            if (part.contains("$")) {
                String[] kv = part.split("\\$", 2);
                String key = kv[0];
                String value = kv[1];

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
                }
            }
        }
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
}
