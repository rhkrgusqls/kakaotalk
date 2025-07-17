package console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import controller.ParsingController;

public class TestTCPServer {
	public static void main(String[] args) throws IOException {
		System.out.println("[시작]");
		try (ServerSocket serverSocket = new ServerSocket(9002)){
			Socket clientSocket = serverSocket.accept();
			System.out.println("[연결완료]");
			
			try(PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
					BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))){
				
				String inputLine;
				while((inputLine = in.readLine()) != null) {
					System.out.println("클라이언트에게 받은 메시지 : " + inputLine );
					//TODO : EX) response = ParsingController.opcode(input); // 바로반환해주는게 아니고 메서드를 걸쳐서 response에 담고 넘어가는
					//sender는 진짜 send만 한다.
					String response;
					response = ParsingController.controllerHandle(inputLine);
					/*
					if(inputLine.startsWith("C1")) {
						response = "S1 반환";
					} else if(inputLine.startsWith("C2")) {
						response = "S2 사용자 정보";
					} else {
						response = "알수없는 명령어";
					}*/
					out.println(response);
					System.out.println("클라이언트로 보냄: :" + response);
				}
				
			}
		}
	}
}
