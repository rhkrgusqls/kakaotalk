package console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

import controller.ParsingController;
import controller.mainController;

public class ClientHandler extends Thread {
    private final Socket clientSocket;
    private PrintWriter out;
    private String userId; // 이 핸들러가 담당하는 사용자의 ID

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        // [수정] NullPointerException 방지를 위해 userId를 임시값으로 초기화
        this.userId = "UnknownUser-" + this.getId();
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("클라이언트(" + this.userId + ")에게 받은 메시지: " + inputLine);
                
                String opcode = ParsingController.extractOpcode(inputLine);
                Map<String, String> dataMap = ParsingController.extractDataToMap(inputLine);

                switch (opcode) {
                    case "Login":
                        String response = ParsingController.controllerHandle(inputLine);
                        out.println(response);
                        
                        if (response.contains("&UserName$")) {
                            // 로그인 성공 시, UserManager에서 이전 임시 ID를 제거하고 실제 ID로 등록
                            UserManager.getInstance().removeUser(this.userId); // UnknownUser 제거
                            this.userId = dataMap.get("id"); // 실제 ID 할당
                            UserManager.getInstance().addUser(this.userId, this);
                        }
                        break;
                        
                    case "Chat":
                        // [수정] 로그인하지 않은 사용자의 채팅 시도 방지
                        if (this.userId.startsWith("UnknownUser")) {
                            out.println("%Error%&message$Login required to chat%");
                            break;
                        }

                        String recipientId = dataMap.get("recipientId");
                        String chatText = dataMap.get("message");
                        UserManager.getInstance().sendMessageToUser(recipientId, inputLine);

                        String chatRoomNum = dataMap.get("chatRoomNum");
                        mainController.saveChatMessage(chatRoomNum, this.userId, chatText);
                        break;
                        
                    default:
                        String defaultResponse = ParsingController.controllerHandle(inputLine);
                        out.println(defaultResponse);
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("클라이언트(" + this.userId + ")와 통신 중 오류 발생: " + e.getMessage());
        } finally {
            // [수정] 연결 종료 시 UserManager에서 제거하고, 소켓을 반드시 닫아줍니다.
            UserManager.getInstance().removeUser(this.userId);
            try {
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.err.println("소켓 종료 중 오류: " + e.getMessage());
            }
            System.out.println("[클라이언트 연결 종료] " + this.userId);
        }
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }
}
