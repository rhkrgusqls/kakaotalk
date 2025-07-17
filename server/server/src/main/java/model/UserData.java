package model;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

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
            return Files.readAllBytes(Paths.get(path));
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
