package model;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.concurrent.ConcurrentHashMap;

public class TCPSender extends Thread {

	private final PrintWriter out;
	private final BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
	private final ConcurrentHashMap<String, CompletableFuture<String>> responseMap = new ConcurrentHashMap<>();
	private volatile boolean isRunning = true;

	public TCPSender(Socket socket) throws IOException {
		this.out = new PrintWriter(socket.getOutputStream(), true);
	}

	@Override
	public void run() {
		while (isRunning) {
			try {
				String message = messageQueue.take();
				out.println(message);
				System.out.println("[SEND] " + message);
			} catch (InterruptedException e) {
				isRunning = false;
				Thread.currentThread().interrupt();
			}
		}
	}

	public void sendMessage(String message) {
		if (!isRunning) return;
		messageQueue.offer(message);
	}

	public CompletableFuture<String> sendMessageWithResponse(String message, String uuid) {
		CompletableFuture<String> future = new CompletableFuture<>();
		responseMap.put(uuid, future);
		messageQueue.offer(message);
		return future;
	}

	public void handleIncomingMessage(String message) {
		String uuid = extractUUID(message);
		if (uuid != null && responseMap.containsKey(uuid)) {
			responseMap.get(uuid).complete(message);
			responseMap.remove(uuid);
		}
	}

	private String extractUUID(String message) {
		int idx = message.indexOf("&uuid$");
		if (idx == -1) return null;
		return message.substring(idx + 6).split("[&%]")[0];
	}

	public void shutdown() {
		isRunning = false;
		this.interrupt();
	}
}
