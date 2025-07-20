package model;

import java.io.*;
import java.util.List;

public class ChatFileManager {
    private static final String DIR = "chat_data";

    public ChatFileManager() {
        File dir = new File(DIR);
        if (!dir.exists()) dir.mkdirs();
    }

    // 메시지 전체 저장 (덮어쓰기)
    public void saveChatsToFile(int chatRoomNum, List<Chat> chats) {
        File file = new File(DIR, "chat_" + chatRoomNum + ".txt");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
            for (Chat chat : chats) {
                String line = String.format("[%s] %s: %s",
                        chat.getTime() != null ? chat.getTime().toString() : "",
                        chat.getUserId(),
                        chat.getText());
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 파일에서 메시지 읽기
    public String loadChatsFromFile(int chatRoomNum) {
        File file = new File(DIR, "chat_" + chatRoomNum + ".txt");
        if (!file.exists()) return "";
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
} 