package console;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 로그인한 사용자들을 관리하는 중앙 저장소 (싱글턴 패턴 적용)
 * 어떤 사용자가 현재 온라인 상태인지, 어떤 스레드가 담당하는지 기억합니다.
 */
public class UserManager {
    
    // --- 싱글턴(Singleton) 패턴 구현 ---
    // 프로그램 전체에서 단 하나의 UserManager 인스턴스만 존재하도록 보장합니다.
    private static UserManager instance = new UserManager();
    
    private UserManager() {} // 외부에서 생성자 호출 방지

    public static UserManager getInstance() {
        return instance;
    }
    // ------------------------------------

    // 여러 클라이언트 스레드(ClientHandler)에서 동시에 접근하므로,
    // 반드시 동기화된(synchronized) Map을 사용해야 데이터 충돌을 막을 수 있습니다.
    // Key: 사용자의 고유 ID (String), Value: 해당 사용자를 담당하는 ClientHandler 스레드
    private Map<String, ClientHandler> onlineUsers = Collections.synchronizedMap(new HashMap<>());

    /**
     * 사용자가 로그인에 성공하면 Map에 추가합니다.
     * @param userId 사용자의 고유 ID
     * @param handler 해당 사용자를 담당하는 ClientHandler 스레드
     */
    public void addUser(String userId, ClientHandler handler) {
        onlineUsers.put(userId, handler);
        System.out.println("[UserManager] " + userId + " 님이 접속했습니다. (현재 접속자: " + onlineUsers.size() + "명)");
    }

    /**
     * 사용자가 로그아웃하거나 연결이 끊기면 Map에서 제거합니다.
     * @param userId 사용자의 고유 ID
     */
    public void removeUser(String userId) {
        // userId가 null이 아닌 경우에만 제거 (NullPointerException 방지)
        if (userId != null && !userId.startsWith("UnknownUser")) {
            onlineUsers.remove(userId);
            System.out.println("[UserManager] " + userId + " 님이 접속을 종료했습니다. (현재 접속자: " + onlineUsers.size() + "명)");
        }
    }

    /**
     * 특정 사용자에게 메시지를 전달합니다. (1:1 채팅의 핵심 로직)
     * @param userId 메시지를 받을 사용자의 ID
     * @param message 전송할 메시지 (원본 프로토콜 문자열)
     */
    public void sendMessageToUser(String userId, String message) {
        ClientHandler handler = onlineUsers.get(userId);
        
        // 해당 유저가 온라인 상태일 경우에만 메시지 전송
        if (handler != null) {
            handler.sendMessage(message); // 해당 유저를 담당하는 스레드의 sendMessage 메서드를 호출
        } else {
            System.out.println("[UserManager] 메시지 전송 실패: " + userId + " 님은 오프라인 상태입니다.");
            // 여기서 보낸 사람에게 "상대방이 오프라인입니다" 라는 응답을 보내주는 로직을 추가할 수 있습니다.
        }
    }
}
