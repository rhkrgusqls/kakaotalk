package model;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.nio.file.*;

public class UserData {
    public String id;
    public String name;
    public String profileDir;
    private byte[] profileImage;

    public UserData(String id, String name, String profileDir) {
        this.id = id;
        this.name = name;
        this.profileDir = profileDir;
        this.profileImage = loadProfileImage(profileDir);
    }
    
    private byte[] loadProfileImage(String profileDir) {
        try {
            String path = profileDir.endsWith("/") || profileDir.endsWith("\\") ? 
                          profileDir + "profile.jpg" : 
                          profileDir + "/profile.jpg";

            Path profilePath = Paths.get(path);

            // 이미지 파일이 존재하지 않으면 default.jpg를 복사
            if (!Files.exists(profilePath)) {
                // 기본 이미지 경로 설정
                Path defaultImagePath = Paths.get("./profileSorce/01026374819/profile.jpg"); // 기본 경로는 프로젝트 루트 기준
                if (Files.exists(defaultImagePath)) {
                    // 디렉터리가 없다면 생성
                    Files.createDirectories(profilePath.getParent());
                    Files.copy(defaultImagePath, profilePath, StandardCopyOption.REPLACE_EXISTING);
                } else {
                    System.err.println("기본 이미지가 존재하지 않습니다: " + defaultImagePath.toAbsolutePath());
                    return null;
                }
            }

            // 이미지 파일 읽기
            return Files.readAllBytes(profilePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getProfileImage() {
        if (profileImage != null) {
            return Base64.getEncoder().encodeToString(profileImage);
        } else {
            return null;
        }
    }
}
