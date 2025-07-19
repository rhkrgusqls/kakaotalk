package console;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class ServerTCP {
    private static final int PORT = 9002;
    private static UserManager userManager = UserManager.getInstance();
    private static int nextConnectionId = 1; // ◀ 접속 순서 ID

    public static void run() {
        // 5초 타이머로 test() 호출
        Timer timer = new Timer(true); // 데몬 스레드로 설정
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                test();
            }
        }, 0, 5000); // 0초 후 시작, 5초 간격

        System.out.println("[서버 구동 시작] 포트 " + PORT + "에서 대기 중...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                int connectionId = nextConnectionId++;
                System.out.println("새 클라이언트 접속: connId=" + connectionId);

                ClientHandler handler = new ClientHandler(clientSocket, connectionId);
                handler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 5초마다 로그인된 모든 사용자에게 지정된 메시지 전송
    private static void test() {/*
        System.out.println("[TEST] 5초마다 로그인된 사용자에게 메시지 푸시...");
        userManager.getAllUsers().forEach((userId, handler) -> {
            String dedicatedMessage = "로그인된 사용자 '" + userId + "'에게만 보내는 전용 메시지입니다.";
            handler.sendMessage(dedicatedMessage);
            System.out.println("[TEST] 보냄: " + dedicatedMessage);
        });*/
    }
}
