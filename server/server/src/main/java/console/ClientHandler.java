package console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import controller.ParsingController;

public class ClientHandler extends Thread {
    private final Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            // 연결된 클라이언트에게 3초 후 일방적으로 메시지 보내기
            new Thread(() -> {
                try {
                    System.out.println("3초 후 메시지 전송 예정 (" + clientSocket.getInetAddress() + ")");
                    Thread.sleep(3000);
                    String notice = "%Chat%message$안녕하세요 반갑습니다 다들~";
                    out.println(notice);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();

            // 클라이언트의 요청을 계속 받아서 처리하고 응답하기
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("클라이언트(" + clientSocket.getInetAddress() + ")에게 받은 메시지 : " + inputLine);
                
                String response = ParsingController.controllerHandle(inputLine);
                
                out.println(response);
                System.out.println("클라이언트(" + clientSocket.getInetAddress() + ")로 보냄: " + response);
            }

        } catch (IOException e) {
            System.out.println("클라이언트(" + clientSocket.getInetAddress() + ")와 통신 중 오류 발생: " + e.getMessage());
        } finally {
            System.out.println("[클라이언트 연결 종료] " + clientSocket.getInetAddress());
            try {
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.err.println("소켓 종료 중 오류: " + e.getMessage());
            }
        }
    }
}
