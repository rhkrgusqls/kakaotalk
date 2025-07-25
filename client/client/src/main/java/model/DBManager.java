package model;

import java.sql.*;
import java.util.*;
//로컬 저장소를 통해 데이터 불러오기(전화번호, 이미지 처럼)
public class DBManager {
    private static DBManager instance;
    private String DB_NAME = "kakaotalkUser1TestData";
    private final String DATA_BASE_IP = "34.47.125.114";
    private final int DATA_BASE_PORT = 3306;
    private final String DB_USER = "root";
    private final String DB_PASSWORD = "QWER1234!";

    public static DBManager getInstance() {
        if (instance == null) instance = new DBManager();
        return instance;
    }

    // 사용자 ID(예: 3)로 DB_NAME을 동적으로 변경
    public void setUserDB(String userId) {
        // userId에서 숫자만 추출 (예: user3 → 3)
        String num = userId.replaceAll("\\D", "");
        if (!num.isEmpty()) {
            this.DB_NAME = "kakaotalkUser" + num + "TestData";
        }
    }

    public void setDBName(String dbName) { this.DB_NAME = dbName; }

    // UserData에 해당 id가 이미 존재하는지 확인
    public boolean isUserExists(String id) {
        String sql = "SELECT COUNT(*) FROM UserData WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 특정 DB에 id가 존재하는지 확인
    public boolean idExistsInDB(String dbName, String id) {
        String sql = "SELECT COUNT(*) FROM UserData WHERE id = ?";
        try (Connection conn = getConnectionForDB(dbName);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // DB 이름을 지정해서 커넥션 생성
    public Connection getConnectionForDB(String dbName) throws Exception {
        String url = "jdbc:mysql://" + DATA_BASE_IP + ":" + DATA_BASE_PORT + "/" + dbName +
                "?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Seoul";
        return DriverManager.getConnection(url, DB_USER, DB_PASSWORD);
    }

    // getConnection을 public으로 변경
    public Connection getConnection() throws Exception {
        String url = "jdbc:mysql://" + DATA_BASE_IP + ":" + DATA_BASE_PORT + "/" + DB_NAME +
                "?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Seoul";
        if (DB_NAME.equals("kakaotalk")) {
            System.err.println("[ERROR] Access to production database 'kakaotalk' is forbidden from client.");
            System.exit(1);
        }
        return DriverManager.getConnection(url, DB_USER, DB_PASSWORD);
    }

    public User getLoggedInUser() {
        String selectSql = "SELECT id, password, name, profileDir FROM UserData LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectSql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                String id = rs.getString("id");
                String password = rs.getString("password");
                String name = rs.getString("name");
                String profileDir = rs.getString("profileDir");

                return new User(id, password, name, profileDir);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 데이터가 없으면 빈 User 반환 혹은 null 반환 선택 가능
        return null;
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
    
    // UserData, PhoneData 모두에 phoneNum 저장 (로컬DB)
    public void saveUser(User user) {
        System.out.println("[DEBUG] saveUser: id=" + user.getId() + ", name=" + user.getName() + ", phoneNum=" + user.getPhoneNum() + ", profileDir=" + user.getProfileDir());
        String deleteSql = "DELETE FROM UserData WHERE id = ?";
        String insertSql = "INSERT INTO UserData(id, password, name, profileDir, phoneNum) VALUES (?, ?, ?, ?, ?)";
        String insertPhoneSql = "INSERT INTO PhoneData(phoneNum, user_id) VALUES (?, ?)";
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
                insertPstmt.setString(5, user.getPhoneNum());
                insertPstmt.executeUpdate();
            }
            // PhoneData에도 저장
            try (PreparedStatement insertPhonePstmt = conn.prepareStatement(insertPhoneSql)) {
                insertPhonePstmt.setString(1, user.getPhoneNum());
                insertPhonePstmt.setString(2, user.getId());
                insertPhonePstmt.executeUpdate();
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


    // 친구 추가: friendPhoneNum(전화번호)로 저장
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

    // 친구 목록 불러오기: friendPhoneNum(전화번호) 리스트 반환
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
        Set<Integer> seenRoomNums = new HashSet<>();
        String sql = "SELECT * FROM ChatRoomList";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int roomNum = rs.getInt("chatRoomNum");
                if (seenRoomNums.contains(roomNum)) continue; // 중복 제거
                seenRoomNums.add(roomNum);
                ChatRoom room = new ChatRoom();
                room.setChatRoomNum(roomNum);
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
    public List<ChatRoom> loadChatRoomsForUser(String userId) {
        List<ChatRoom> list = new ArrayList<>();
        String sql = "SELECT * FROM ChatRoomList WHERE userId = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
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

    public String loadLastMessageForRoom(int roomNum) {
        String sql = "SELECT text FROM ChatList WHERE chatRoomNum = ? ORDER BY time DESC LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, roomNum);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("text");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "메시지 없음";  // 기본값
    }

    public String getCurrentDBName() { return DB_NAME; }

    // phoneNum으로 id 찾기
    public String getIdByPhoneNum(String phoneNum) {
        String sql = "SELECT id FROM UserData WHERE phoneNum = ? LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, phoneNum);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // id로 DB명 반환 (존재하는 1~8번 DB 중 실제 id가 있는 DB만 반환)
    public String getDBNameById(String id) {
        for (int i = 1; i <= 8; i++) {
            String dbName = "kakaotalkUser" + i + "TestData";
            if (idExistsInDB(dbName, id)) {
                return dbName;
            }
        }
        return null;
    }
}


