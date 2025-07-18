package model;

import java.sql.*;
import java.util.*;
//로컬 저장소를 통해 데이터 불러오기(전화번호, 이미지 처럼)
public class DBManager {
	/*SQLite용 커넥터
	private final String DB_FILE_PATH = "/path/to/database.sqlite";

	private Connection getConnection() throws Exception {
	    String url = "jdbc:sqlite:" + DB_FILE_PATH;
	    return DriverManager.getConnection(url);
	}
	*/

    public DBManager() {}
    
    private final String DATA_BASE_IP = "34.47.125.114";
    private final int DATA_BASE_PORT = 3306;
    private final String DB_NAME = "kakaotalkUser1TestData";
    private final String DB_USER = "root";
    private final String DB_PASSWORD = "QWER1234!";

    private Connection getConnection() throws Exception {
        String url = "jdbc:mysql://" + DATA_BASE_IP + ":" + DATA_BASE_PORT + "/" + DB_NAME +
                     "?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Seoul";
        return DriverManager.getConnection(url, DB_USER, DB_PASSWORD);
    }

    public String loadPhoneNum() {
        String sql = "SELECT phoneNum FROM PhoneData LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getString("phoneNum");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveUser(User user) {
        String deleteSql = "DELETE FROM UserData WHERE id = ?";
        String insertSql = "INSERT INTO UserData(id, password, name, profileDir) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement deletePstmt = conn.prepareStatement(deleteSql)) {
                deletePstmt.setString(1, user.getId());
                deletePstmt.executeUpdate();
            }

            try (PreparedStatement insertPstmt = conn.prepareStatement(insertSql)) {
                insertPstmt.setString(1, user.getId());
                insertPstmt.setString(2, user.getPassword());
                insertPstmt.setString(3, user.getName());
                insertPstmt.setString(4, user.getProfileDir());
                insertPstmt.executeUpdate();
            }

            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void deleteUsers() {
        String sql = "DELETE FROM UserData";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void saveFriend(String friendPhoneNum) {
        String sql = "INSERT IGNORE INTO FriendList(friendPhoneNum) VALUES (?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, friendPhoneNum);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> loadFriendList() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT friendPhoneNum FROM FriendList";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getString("friendPhoneNum"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void saveChatRoom(ChatRoom room) {
        String sql = "INSERT INTO ChatRoomList(chatRoomNum, roomType, roomName) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, room.getChatRoomNum());
            pstmt.setString(2, room.getRoomType());
            pstmt.setString(3, room.getRoomName());
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
                list.add(room);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void saveChat(Chat chat) {
        String sql = "INSERT INTO ChatList(chatRoomNum, text, userId) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, chat.getChatRoomNum());
            pstmt.setString(2, chat.getText());
            pstmt.setString(3, chat.getUserId());
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Chat> loadChatList(int roomNum) {
        List<Chat> list = new ArrayList<>();
        String sql = "SELECT * FROM ChatList WHERE chatRoomNum = ? ORDER BY time ASC";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, roomNum);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Chat chat = new Chat();
                chat.setChatIndex(rs.getInt("chatIndex"));
                chat.setChatRoomNum(rs.getInt("chatRoomNum"));
                chat.setText(rs.getString("text"));
                chat.setUserId(rs.getString("userId"));
                chat.setTime(rs.getTimestamp("time"));
                list.add(chat);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}


