package model;

public class User {
    private String id;
    private String password;
    private String name;
    private String profileDir;

    public User() {}

    public User(String id, String password, String name, String profileDir) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.profileDir = profileDir;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileDir() {
        return profileDir;
    }

    public void setProfileDir(String profileDir) {
        this.profileDir = profileDir;
    }
}
