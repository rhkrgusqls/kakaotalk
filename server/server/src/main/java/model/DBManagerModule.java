package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    
    /**
     * 사용자 ID(String)로 이름과 프로필 경로를 불러온다
     * @param userId
     * @return UserData 객체 (id, name, profileDir 포함), 존재하지 않으면 null
     */
    public UserData getUserDataById(String userId) {
        String sql = "SELECT id, name, profileDir FROM UserData WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String profileDir = rs.getString("profileDir");
                return new UserData(id, name, profileDir);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
