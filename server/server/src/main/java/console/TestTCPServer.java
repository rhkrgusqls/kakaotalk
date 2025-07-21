package console;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TestTCPServer {
    public static void main(String[] args) throws IOException {
        System.out.println("[메인 서버 시작] 2000 포트에서 대기 중...");
        try (ServerSocket serverSocket = new ServerSocket(9002)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, 2); //클라이언트 핸들러로 다중접속할 수 있게 함
                clientHandler.start();
            }
        }
    }
}
