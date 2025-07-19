package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.*;
public class DBManagerModule {
    
    private final String DATA_BASE_IP = "34.47.125.114";
    private final int DATA_BASE_PORT = 3306;
    private final String DB_NAME = "kakaotalk";
    private final String DB_USER = "root";
    private final String DB_PASSWORD = "QWER1234!";

    public DBManagerModule() {}

    private Connection getConnection() throws Exception {
        String url = "jdbc:mysql://" + DATA_BASE_IP + ":" + DATA_BASE_PORT + "/" + DB_NAME +
                     "?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Seoul";
        return DriverManager.getConnection(url, DB_USER, DB_PASSWORD);
    }

    /**
     * 이미 가입된 번호인지 확인
     * @param phoneNum
     * @return
     */
    public boolean isRegisteredUser(String phoneNum) {
        String sql = "SELECT COUNT(*) FROM UserData WHERE phoneNum = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, phoneNum);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 회원가입
     * @param id
     * @param password
     * @param name
     * @param profileDir
     * @param phoneNum
     * @return
     */
    public boolean registerUser(String id, String password, String name, String profileDir, String phoneNum) {
        String sql = "INSERT INTO UserData (id, password, name, profileDir, phoneNum) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.setString(2, password);
            stmt.setString(3, name);
            stmt.setString(4, profileDir);
            stmt.setString(5, phoneNum);
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * 아이디 비밀번호를 통한 로그인
     * @param id
     * @param password
     * @return
     */
    public boolean login(String id, String password) {
        String sql = "SELECT COUNT(*) FROM UserData WHERE id = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 휴대전화 번호를 통한 로그인
     * @param phoneNum
     * @return
     */
    public boolean login(String phoneNum) {
        String sql = "SELECT COUNT(*) FROM UserData WHERE phoneNum = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, phoneNum);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * 양방향 친구 전화번호 목록 조회 후, 해당 친구들의 id 배열 반환
     * @param phoneNum 사용자 전화번호
     * @return 친구 id 배열
     */
    public String[] loadFriend(String phoneNum) {
        String sql = "SELECT friendPhone FROM (" +
                     " SELECT userPhone2 AS friendPhone FROM FriendList WHERE userPhone1 = ? " +
                     " UNION " +
                     " SELECT userPhone1 AS friendPhone FROM FriendList WHERE userPhone2 = ? " +
                     ") AS Friends";

        List<String> friendPhones = new ArrayList<>();
        List<String> friendIds = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, phoneNum);
            stmt.setString(2, phoneNum);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                friendPhones.add(rs.getString("friendPhone"));
            }

            // 친구 전화번호 리스트로부터 id 조회
            String idSql = "SELECT id FROM UserData WHERE phoneNum = ?";
            try (PreparedStatement idStmt = conn.prepareStatement(idSql)) {
                for (String friendPhone : friendPhones) {
                    idStmt.setString(1, friendPhone);
                    ResultSet idRs = idStmt.executeQuery();
                    if (idRs.next()) {
                        friendIds.add(idRs.getString("id"));
                    }
                    idRs.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return friendIds.toArray(new String[0]);
    }    
    
    /*
     	DBManagerModule db = new DBManagerModule();
		UserData user = db.getUserDataByPhone("01012345678");
		
		if (user != null) {
		    System.out.println("Name: " + user.name);
		    System.out.println("Profile Dir: " + user.profileDir);
		} else {
		    System.out.println("User not found.");
		}
     */
    
    public UserData getUserDataById(String id) {
        String userSql = "SELECT id, name, profileDir FROM UserData WHERE id = ?";
        String anonSql = "SELECT anonymousId AS id, nickName AS name, profileDir FROM AnonymousUserData WHERE anonymousId = ?";
        try (Connection conn = getConnection()) {

            try (PreparedStatement userStmt = conn.prepareStatement(userSql)) {
                userStmt.setString(1, id);
                ResultSet userRs = userStmt.executeQuery();

                if (userRs.next()) {
                    return new UserData(
                        userRs.getString("id"),
                        userRs.getString("name"),
                        userRs.getString("profileDir")
                    );
                }
            }

            try (PreparedStatement anonStmt = conn.prepareStatement(anonSql)) {
                anonStmt.setString(1, id);
                ResultSet anonRs = anonStmt.executeQuery();

                if (anonRs.next()) {
                    return new UserData(
                        anonRs.getString("id"),
                        anonRs.getString("name"),
                        anonRs.getString("profileDir")
                    );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    
    /**
     * 두 전화번호 간의 친구 관계 설정 (중복 방지 포함)
     * @param phone1 첫 번째 사용자 전화번호
     * @param phone2 두 번째 사용자 전화번호
     * @return 성공 여부
     */
    public boolean setFriendData(String phone1, String phone2) {
        String sql = "INSERT INTO FriendList (userPhone1, userPhone2) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // 항상 phone1 < phone2 순서로 저장
            if (phone1.compareTo(phone2) > 0) {
                String temp = phone1;
                phone1 = phone2;
                phone2 = temp;
            }

            stmt.setString(1, phone1);
            stmt.setString(2, phone2);
            stmt.executeUpdate();
            return true;

        } catch (SQLIntegrityConstraintViolationException e) {
            // 이미 친구일 경우 무시
            System.out.println("Already friends: " + e.getMessage());
            return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    
    public List<ChatRoomData> loadChatRoom(String id) {
        List<ChatRoomData> roomList = new ArrayList<>();
        String sql = "SELECT cr.chatRoomNum, cr.roomType, cr.roomName " +
                     "FROM ChatRoomList cr " +
                     "JOIN ChatRoomMember rm ON cr.chatRoomNum = rm.chatRoomNum " +
                     "WHERE rm.id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int num = rs.getInt("chatRoomNum");
                String type = rs.getString("roomType");
                String name = rs.getString("roomName");

                ChatRoomData room = new ChatRoomData(num, type, name);
                roomList.add(room);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return roomList;
    }
    
    public String getIdByPhoneNum(String phoneNum) {
        String sql = "SELECT id FROM UserData WHERE phoneNum = ?";
        
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
    
    public List<ChatData> getChatData(int chatRoomNum) {
        List<ChatData> chatList = new ArrayList<>();
        String sql = "SELECT * FROM ChatList WHERE chatRoomNum = ? ORDER BY time ASC";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, chatRoomNum);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ChatData chat = new ChatData();
                chat.setChatIndex(rs.getInt("chatIndex"));
                chat.setChatRoomNum(rs.getInt("chatRoomNum"));
                chat.setText(rs.getString("text"));
                chat.setUserId(rs.getString("userId"));
                chat.setTime(rs.getTimestamp("time"));
                chatList.add(chat);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return chatList;
    }
    public boolean insertChatData(int chatRoomNum, String userId, String text) {
        String sql = "INSERT INTO ChatList (chatRoomNum, userId, text) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, chatRoomNum);
            stmt.setString(2, userId);
            stmt.setString(3, text);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
