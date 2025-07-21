package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LocalDBManager {
    private static final String DB_FILE = "local_kakao.db";

    public LocalDBManager() {
        createTablesIfNotExists();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + DB_FILE);
    }

    // 테이블 생성
    private void createTablesIfNotExists() {
        String createChatRoomList = "CREATE TABLE IF NOT EXISTS ChatRoomList (" +
                "chatRoomNum INTEGER PRIMARY KEY, " +
                "roomType TEXT, " +
                "roomName TEXT, " +
                "lastMessage TEXT" +
                ")";
        String createChatRoomMember = "CREATE TABLE IF NOT EXISTS ChatRoomMember (" +
                "chatRoomNum INTEGER, " +
                "id TEXT, " +
                "PRIMARY KEY (chatRoomNum, id)" +
                ")";
        String createChatList = "CREATE TABLE IF NOT EXISTS ChatList (" +
                "chatIndex INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "chatRoomNum INTEGER, " +
                "text TEXT, " +
                "userId TEXT, " +
                "time TEXT" +
                ")";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createChatRoomList);
            stmt.execute(createChatRoomMember);
            stmt.execute(createChatList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 채팅방 저장/업데이트
    public void saveOrUpdateChatRoom(ChatRoom room) {
        String sql = "INSERT INTO ChatRoomList (chatRoomNum, roomType, roomName, lastMessage) " +
                "VALUES (?, ?, ?, ?) " +
                "ON CONFLICT(chatRoomNum) DO UPDATE SET " +
                "roomType=excluded.roomType, roomName=excluded.roomName, lastMessage=excluded.lastMessage";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, room.getChatRoomNum());
            pstmt.setString(2, room.getRoomType());
            pstmt.setString(3, room.getRoomName());
            pstmt.setString(4, room.getLastMessage());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 채팅방 멤버 저장
    public void saveChatRoomMember(int chatRoomNum, String userId) {
        String sql = "INSERT OR IGNORE INTO ChatRoomMember (chatRoomNum, id) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, chatRoomNum);
            pstmt.setString(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 채팅 메시지 저장/업데이트
    public void saveOrUpdateChat(Chat chat) {
        String sql = "INSERT OR REPLACE INTO ChatList (chatIndex, chatRoomNum, text, userId, time) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, chat.getChatIndex());
            pstmt.setInt(2, chat.getChatRoomNum());
            pstmt.setString(3, chat.getText());
            pstmt.setString(4, chat.getUserId());
            pstmt.setString(5, chat.getTime() != null ? chat.getTime().toString() : null);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // 채팅 메시지 저장 (새로운 메시지용)
    public void saveChatMessage(int chatRoomNum, String userId, String text) {
        String sql = "INSERT INTO ChatList (chatRoomNum, text, userId, time) VALUES (?, ?, ?, datetime('now'))";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, chatRoomNum);
            pstmt.setString(2, text);
            pstmt.setString(3, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 사용자별 채팅방 목록 조회 (ChatRoomMember를 통해 접근)
    public List<ChatRoom> loadChatRoomsForUser(String userId) {
        List<ChatRoom> list = new ArrayList<>();
        String sql = "SELECT cr.* FROM ChatRoomList cr " +
                    "INNER JOIN ChatRoomMember crm ON cr.chatRoomNum = crm.chatRoomNum " +
                    "WHERE crm.id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ChatRoom room = new ChatRoom();
                room.setChatRoomNum(rs.getInt("chatRoomNum"));
                room.setRoomType(rs.getString("roomType"));
                room.setRoomName(rs.getString("roomName"));
                room.setLastMessage(rs.getString("lastMessage"));
                list.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 전체 채팅방 목록 조회 (기존 메서드 유지)
    public List<ChatRoom> loadChatRooms() {
        List<ChatRoom> list = new ArrayList<>();
        String sql = "SELECT * FROM ChatRoomList";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                ChatRoom room = new ChatRoom();
                room.setChatRoomNum(rs.getInt("chatRoomNum"));
                room.setRoomType(rs.getString("roomType"));
                room.setRoomName(rs.getString("roomName"));
                room.setLastMessage(rs.getString("lastMessage"));
                list.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 채팅방별 메시지 조회
    public List<Chat> loadChatsForRoom(int chatRoomNum) {
        List<Chat> list = new ArrayList<>();
        String sql = "SELECT * FROM ChatList WHERE chatRoomNum = ? ORDER BY time ASC";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, chatRoomNum);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Chat chat = new Chat();
                chat.setChatIndex(rs.getInt("chatIndex"));
                chat.setChatRoomNum(rs.getInt("chatRoomNum"));
                chat.setText(rs.getString("text"));
                chat.setUserId(rs.getString("userId"));
                chat.setTime(java.sql.Timestamp.valueOf(rs.getString("time")));
                list.add(chat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 모든 채팅방 및 멤버 정보 삭제
    public void deleteAllChatRooms() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM ChatRoomList");
            stmt.executeUpdate("DELETE FROM ChatRoomMember");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
} 