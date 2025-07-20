package model;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import observer.ServerCallEventHandle;

public class TCPReceiver extends Thread {
	private final BufferedReader in;
	private final TCPSender sender;
	private volatile boolean isRunning = true;

	public TCPReceiver(Socket socket, TCPSender sender) throws IOException {
		this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.sender = sender;
	}

	@Override
	public void run() {
		while (isRunning) {
			try {
				String message = in.readLine();
				if (message == null) {
					System.out.println("서버와 연결이 끊어짐");
					break;
				}
				System.out.println("[RECV] " + message);
				sender.handleIncomingMessage(message); // UUID 매칭
				ServerCallEventHandle.notifyObservers(message); // 이벤트 알림
			} catch (SocketException e) {
				System.out.println("소켓 종료 감지");
				break;
			} catch (IOException e) {
				if (isRunning) {
					System.err.println("수신 중 오류: " + e.getMessage());
				}
				break;
			}
		}
	}

	public void shutdown() {
		isRunning = false;
		try {
			if (in != null) in.close();
		} catch (IOException ignored) {}
		this.interrupt();
	}
}

