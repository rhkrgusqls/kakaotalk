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
    private final int connectionId; // 접속 순서
    private PrintWriter out;
    private String userId; // 로그인 후 사용될 실제 사용자 ID (String)

    public ClientHandler(Socket socket, int connectionId) {
        this.clientSocket = socket;
        this.connectionId = connectionId;
        this.userId = "UnknownUser-" + connectionId; // ◀ 임시 ID
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("클라이언트(" + this.userId + " / connId:" + connectionId + ")에게 받은 메시지: " + inputLine);
                
                // [요구사항 반영] 이벤트 1: 클라이언트가 보낸 메시지에 "수신받음"을 더해 응답
                System.out.println(inputLine + " 수신받음!");
                
                String response = ParsingController.controllerHandle(inputLine);
                out.println();
                out.println(response);
//                switch (opcode) {
//                case "Login":
//                    // --- 테스트를 위한 임시 수정 ---
//                    // 실제 컨트롤러를 호출하는 대신, 무조건 성공 응답을 보내도록 함
//                    String tempSuccessResponse = "%Login%&Result$OK&UserName$테스트유저%";
//                    out.println(tempSuccessResponse);
//                    
//                    // 강제로 UserManager에 사용자 추가
//                    this.userId = dataMap.get("id"); // "testuser1"
//                    UserManager.getInstance().addUser(this.userId, this);
//                    System.out.println("임시 로그인 성공: " + this.userId);
//                    // --------------------------------- 임시 로직임 id
////                    case "Login":
////                        String response = ParsingController.controllerHandle(inputLine);
////                        out.println(response);
////                        if (response.contains("&UserName$")) {
////                            UserManager.getInstance().removeUser(this.userId);
////                            this.userId = dataMap.get("id");
////                            UserManager.getInstance().addUser(this.userId, this);
////                        }
//                        break;
//                    case "Chat":
//                        if (this.userId.startsWith("UnknownUser")) {
//                            out.println("%Error%&message$Login required to chat%");
//                            break;
//                        }
//                        String recipientId = dataMap.get("recipientId");
//                        
//                        // 중복 로직 제거, sendMessageToUser 하나만 호출
//                        UserManager.getInstance().sendMessageToUser(recipientId, inputLine); 
//
//                        // String chatRoomNum = dataMap.get("chatRoomNum");
//                        // mainController.saveChatMessage(chatRoomNum, this.userId, chatText);
//                        break;
//                    default:
//                        // String defaultResponse = ParsingController.controllerHandle(inputLine);
//                        // out.println(defaultResponse);
//                        break;
//                }
            }
        } catch (IOException e) {
            System.out.println("클라이언트(" + this.userId + ") 통신 오류: " + e.getMessage());
        } finally {
            UserManager.getInstance().removeUser(this.userId);
            try { clientSocket.close(); } catch (IOException ignored) {}
            System.out.println("[클라이언트 연결 종료] " + this.userId);
        }
    }

    public void sendMessage(String message) {
        if (out != null) { out.println(message); }
    }
}
//    @Override
//    public void run() {
//        try {
//            out = new PrintWriter(clientSocket.getOutputStream(), true);
//            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//
//            String inputLine;
//            while ((inputLine = in.readLine()) != null) {
//                System.out.println("클라이언트(" + this.userId + ")에게 받은 메시지: " + inputLine);
//                
//                String opcode = ParsingController.extractOpcode(inputLine);
//                Map<String, String> dataMap = ParsingController.extractDataToMap(inputLine);
//
//                switch (opcode) {
//                    case "Login":
//                        String response = ParsingController.controllerHandle(inputLine);
//                        out.println(response);
//                        
//                        if (response.contains("&UserName$")) {
//                            // 로그인 성공 시, UserManager에서 이전 임시 ID를 제거하고 실제 ID로 등록
//                            UserManager.getInstance().removeUser(this.userId); // UnknownUser 제거
//                            this.userId = dataMap.get("id"); // 실제 ID 할당
//                            UserManager.getInstance().addUser(this.userId, this);
//                        }
//                        break;
//                        
//                    case "Chat":
//                        // [수정] 로그인하지 않은 사용자의 채팅 시도 방지
//                        if (this.userId.startsWith("UnknownUser")) {
//                            out.println("%Error%&message$Login required to chat%");
//                            break;
//                        }
//
//                        String recipientId = dataMap.get("recipientId");
//                        String chatText = dataMap.get("message");
//                        
//                        // 지정된 ID의 소켓만 찾아 전송!!!(다른 소켓 무시)
//                        ClientHandler targetHandler = UserManager.getInstance().getHandlerById(recipientId);
//                        if(targetHandler != null) {
//                        	targetHandler.sendMessage(inputLine); // 지정된 소켓에만 전달함
//                        	System.out.println("전달된 메시지 : "+ this.userId + " -> " + recipientId);
//                        } else {
//                        	out.println("%Error%&message$Recipient not found%"); // 지정 오류나면 에러메시지
//                        }
//                        UserManager.getInstance().sendMessageToUser(recipientId, inputLine);
//
//                        String chatRoomNum = dataMap.get("chatRoomNum");
//                        mainController.saveChatMessage(chatRoomNum, this.userId, chatText);
//                        break;
//                        
//                    default:
//                        String defaultResponse = ParsingController.controllerHandle(inputLine);
//                        out.println(defaultResponse);
//                        break;
//                }
//            }
//        } catch (IOException e) {
//            System.out.println("클라이언트(" + this.userId + ")와 통신 중 오류 발생: " + e.getMessage());
//        } finally {
//            // [수정] 연결 종료 시 UserManager에서 제거하고, 소켓을 반드시 닫아줍니다.
//            UserManager.getInstance().removeUser(this.userId);
//            try {
//                if (clientSocket != null && !clientSocket.isClosed()) {
//                    clientSocket.close();
//                }
//            } catch (IOException e) {
//                System.err.println("소켓 종료 중 오류: " + e.getMessage());
//            }
//            System.out.println("[클라이언트 연결 종료] " + this.userId);
//        }
//    }
//
//    public void sendMessage(String message) {
//        if (out != null) {
//            out.println(message);
//        }
//    }
//}
