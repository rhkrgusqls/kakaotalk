package controller;

import model.DBManager;
import model.DataParsingModule;
import model.TCPManager;
import model.User;
import observer.Observer;
import observer.ServerCallEventHandle;

public class MainController implements Observer {
    static TCPManager tcp;
    static MainController instance = new MainController();

    private MainController() {
        tcp = TCPManager.getInstance();
        tcp.connect();
        System.out.println("[LOG][TCP]:TCP 연결시도");
        if (tcp.isConnected()) {
            System.out.println("TCP 연결 성공");
        }
        // 비동기 메시지 수신을 위해 옵저버 등록
        ServerCallEventHandle.registerObserver(this);
    }

    public static MainController getInstance() {
        return instance;
    }

    // 동기 로그인 처리
    public static boolean login(String id, String password) {
        DataParsingModule data = new DataParsingModule();
        if (tcp.isConnected()) {
            System.out.println("TCP 연결 성공! 로그인 메시지를 보냅니다.");
            String response = tcp.sendSyncMessage("%Login%&id$" + id + "&password$" + password + "%");
            if (response != null) {
                data.parseData(response);
            } else {
                System.err.println("[ERROR] 서버 응답이 null입니다.");
                return false;
            }
        } else {
            System.out.println("TCP 연결 실패!");
            return false;
        }
        if (data.getUserName() == null) {
            return false;
        }
        User myData = new User(id, password, data.getUserName(), "./profile/self/coldplay.jpg");
        DBManager db = new DBManager();
        db.saveUser(myData);
        return true;
    }

    public static User getLoggedInUser() {
        DBManager db = new DBManager();
    	return db.getLoggedInUser();
    }
    
    // 비동기 메시지를 수신하는 콜백 (Observer 인터페이스 구현)
    @Override
    public void onMessageReceived(String message) {
        System.out.println("[ASYNC] 서버로부터 비동기 메시지 수신: " + message);

        if (message.startsWith("%Notice%")) {
            String content = parseValue(message, "content");
        }
        // 추가적인 메시지 유형 처리 가능
    }

    // 메시지 안의 key$value 형태 파싱 헬퍼
    private String parseValue(String message, String key) {
        if (message == null) return null;
        String[] parts = message.split("&");
        for (String part : parts) {
            if (part.startsWith(key + "$")) {
                return part.substring((key + "$").length()).replace("%", "");
            }
        }
        return null;
    }
    
    public static boolean addFriend(String idOrPhone) {
        DBManager db = new DBManager();
        
        DataParsingModule data = new DataParsingModule();
        if (tcp.isConnected()) {
            System.out.println("TCP 연결 성공! 로그인 메시지를 보냅니다.");
            String response = tcp.sendSyncMessage("%ADDFRIEND%&id$" + idOrPhone + "&phoneNum$" + idOrPhone + "%" + "&user$" + db.getLoggedInUser().getId()+ "%");
            if (response != null) {
                data.parseData(response);
            } else {
                System.err.println("[ERROR] 서버 응답이 null입니다.");
                return false;
            }
        } else {
            System.out.println("TCP 연결 실패!");
            return false;
        }
        if (data.getPhoneNum() == null) {
            return false;
        }
        db.saveFriend(data.getPhoneNum());
        return true;
    }
}
