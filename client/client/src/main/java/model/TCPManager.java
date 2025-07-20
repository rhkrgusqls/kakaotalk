package model;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class TCPManager {
	private static TCPManager instance;

	private TCPManager() {}
	public static synchronized TCPManager getInstance() {
		if (instance == null) instance = new TCPManager();
		return instance;
	}

	private Socket socket;
	private TCPSender sender;
	private TCPReceiver receiver;

	private boolean isConnected = false;
	private final String serverIp = "localhost";
	private final int serverPort = 9002;

	public void connect() {
		if (isConnected) {
			System.out.println("현재 서버와 연결되어 있음");
			return;
		}
		try {
			System.out.println("서버 연결 중... (" + serverIp + ":" + serverPort + ")");
			socket = new Socket(serverIp, serverPort);

			sender = new TCPSender(socket);
			receiver = new TCPReceiver(socket, sender);

			sender.start();
			receiver.start();

			isConnected = true;
			System.out.println("연결 성공");
		} catch (IOException e) {
			System.err.println("서버 연결 실패: " + e.getMessage());
			disconnect();
		}
	}

	public void disconnect() {
		System.out.println("서버와의 연결 종료");
		try {
			if (sender != null) sender.shutdown();
			if (receiver != null) receiver.shutdown();
			if (socket != null && !socket.isClosed()) socket.close();
		} catch (IOException e) {
			System.err.println("소켓 종료 중 오류: " + e.getMessage());
		} finally {
			isConnected = false;
			socket = null;
			sender = null;
			receiver = null;
		}
	}

	public boolean isConnected() {
		return socket != null && !socket.isClosed() && socket.isConnected() && isConnected;
	}

	public void checkConnectionAndReconnect() {
		if (!isConnected()) {
			System.out.println("재연결 중...");
			disconnect();
			connect();
		}
	}

	public void sendMessage(String message) {
		if (!isConnected || sender == null) {
			System.out.println("서버에 연결되어 있지 않아서 메시지를 보낼 수 없음");
			return;
		}
		sender.sendMessage(message);
	}

	public synchronized String sendSyncMessage(String message) {
		if (!isConnected || sender == null) return null;

		String uuid = UUID.randomUUID().toString();
		String fullMessage = message + "&uuid$" + uuid;

		try {
			CompletableFuture<String> future = sender.sendMessageWithResponse(fullMessage, uuid);
			String response = future.get(5, TimeUnit.SECONDS); // 최대 5초 대기
			System.out.println("[LOG][SERVERMSG]: 동기화 응답: " + response);
			return response;
		} catch (Exception e) {
			System.err.println("동기 응답 처리 중 오류: " + e.getMessage());
			checkConnectionAndReconnect();
			return null;
		}
	}
}

