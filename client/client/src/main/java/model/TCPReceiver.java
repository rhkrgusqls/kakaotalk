package model;

import clientcontroller.MainController;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

public class TCPReceiver extends Thread{
	private final BufferedReader in;
	private volatile boolean isRunning = true;
	
	//소켓으로부터 InputStream. 받아서 BufferedReader를 생성
	public TCPReceiver(Socket socket) throws IOException {
		this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}
	
	@Override
	public void run() {
		while(isRunning) {
			try {
				String message = in.readLine(); //서버로부터 메시지가 올 때까지 대기
				if(message == null) {
					System.out.println("서버와 연결이 끊어짐");
					break;
				}
				System.out.println("서버로 부터 메시지 수신 :" + message);
				// TODO수시한 메시지를 처리할 MainController로 전달
			} catch(SocketException e) {
				System.out.println("소켓 닫힘으로 인한 수신 중단");
				break;
			} catch(IOException e) {
				if(isRunning){
					System.err.println("메시지 수신 중 오류 : " + e.getMessage());
				}
				break;
			}
		}
	}
	
	public void shutdown() {
		 isRunning = false;
		 try {
			 if(in != null) in.close();
		 } catch (IOException e) {
			 
		 }
		 this.interrupt();
	 }
//받기 전용, 안주고 받기만
}
