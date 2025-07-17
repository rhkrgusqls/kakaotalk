package clientmodel;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

// 메시지를 큐에다가 저장하고, 순차적으로 보내기
public class TCPSender extends Thread{
	
	private final Socket socket;
	private final PrintWriter out;
	private final BlockingQueue<String> messageQueue; // 스레드에 메시지를 추가하고 보낼수 있는 큐
	private volatile boolean isRunning = true;
	
	//생성자 서버와 연결 및 초기화
	public TCPSender(Socket socket) throws IOException{
		this.socket = socket;
		this.out = new PrintWriter(socket.getOutputStream(), true); // 서버로 텍스트를 보내기위한 스트림
		this.messageQueue = new LinkedBlockingQueue<>();
	}
	//run 큐에서 메시지를 꺼내고 비어있으면 Blocking
	@Override
	public void run() {
		while (isRunning) {
			try {
				String message = messageQueue.take();
				out.println(message);
				System.out.println("메시지를 서버로 보냈음 : " + message);
			} catch (InterruptedException e) {
				System.out.println("메시지를 서버로 보내지 못했음");
				isRunning = false; // 현재 스레드 상태를 중단으로 설정
				Thread.currentThread().interrupt();
			}
		}
		;
	}
	 // 전송할 메시지를 큐에 추가, 실제 전송은 run()이 담당
	 public void sendMessage(String message) {
		 if(!isRunning) return;
		 try {
			 messageQueue.put(message);
		 } catch(InterruptedException e) {
			 Thread.currentThread().interrupt();
			 System.out.println("메시지를 큐에 넣는 중에 오류");
		 }
	 }
	 
	 //스레드와 소켓을 종료
	 public void shutdown() {
		 isRunning = false;
		 this.interrupt(); // run()메서드 내의 대기상태를 깨워서 종료
	 }
// 한번 주고 한번 결과를 반환받고
}
