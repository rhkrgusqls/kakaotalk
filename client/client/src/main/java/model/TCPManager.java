package model;

import controller.MainController;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPManager {
	//싱글톤
	private static TCPManager instance;
	
	private TCPManager() {}; // 외부 생성자 호출 방지
	
	public static synchronized TCPManager getInstance() {
		if(instance == null) {
			instance = new TCPManager();
		}
		return instance;
	}
	// ---------------------------------------------------
	private Socket socket;
	private TCPSender sender;
	private TCPReceiver receiver;
	
	private MainController mainController;
	
	private PrintWriter out;
	private BufferedReader in;
	
//	private final String serverIp = "34.47.125.114";
//	private final int serverPort = 3306;
	private final String serverIp = "localhost";
	private final int serverPort = 9002;
	private boolean isConnected = false;
	
	public void connect(MainController controller) {
		if(isConnected) {
			System.out.println("현재 서버와 연결되어 있음");
			return;
		}
		try {
			this.mainController = controller; // 전달받은 컨트롤를 멤버 변수에 저장
			System.out.println("서버 연결 중...("+serverIp + ":" + serverPort + ")");
			socket = new Socket(serverIp, serverPort);
			
			this.out = new PrintWriter(socket.getOutputStream(), true);
			this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			sender = new TCPSender(socket);
			receiver = new TCPReceiver(socket, controller);
			
			sender.start();
			receiver.start();
			
			isConnected = true;
			System.out.println("연결 성공");
		} catch(IOException e) {
			System.err.println("서버 연결에 실패" + e.getMessage());
			disconnect();
		}
	}
	
	// 모든 스레드 및 소켓 연결 해제
	public void disconnect() {
		System.out.println("서버와의 연결 종료");
		try {
			if(sender != null) {
				sender.shutdown(); //TCPSender 스레드 종료
			}
			if(receiver != null) {
				receiver.shutdown();
			}
			if(socket != null && !socket.isClosed()) {
				socket.close(); 
			}
		} catch(IOException e){
			System.err.println("소켓 종료 중 오류 : " + e.getMessage());
		} finally {
			isConnected = false;
			socket = null;
			sender = null;
			receiver = null;
		}
	}
	
	// 응답을 받고 반환, 여러 요청이 꼬이지 않도록 동기화 키워드 사용
	public synchronized String sendSyncMessage(String message) {
		if(!isConnected()) {
			System.err.println("서버에 연결이 되어 있지 않기 때문에 메시지를 보낼 수 없음");
			return null;
		}
		try {
			// 서버로 메시지 전송
			System.out.println("사용자 -> 서버 : " + message);
			out.println(message);
			
			// 서버로부터 응답 대기 및 수신
			String response = in.readLine();
			System.out.println("사용자 <- 서버 : " + response);
			
			return response; //응답 반환
		} catch(IOException e) {
			System.err.println("통신 중 오류 : " + e.getMessage());
			checkConnectionAndReconnect(); // 재연결 시도
			return null;
		}
	}
	
	//TCPSender를 통해 서버로 메시지를 전송하도록 요청
	public void sendMessage(String message) {
		if(!isConnected || sender == null) {
			System.out.println("서버에 연결이 되있지 않아서 메시지를 보낼 수 없음");
			return;
		}
		sender.sendMessage(message);
	}
	//현재 통신 상태 반환
	public boolean isConnected() {
		return socket != null && !socket.isClosed() && socket.isConnected() && isConnected;
	}
	
	//통신상태 체크 후 끊겨있으면 재접속 시도
	public void checkConnectionAndReconnect() {
		if(!isConnected()) {
			System.out.println("재연결중...");
			disconnect();
			if(this.mainController != null) {
				connect(this.mainController);
			} else {
				System.out.println("재연결 실패");
			}
		}
	}
	//스레드 분기 리시버랑 샌더를 생성 디스커넥트랑 통신상태 체크 후 재접속
}
