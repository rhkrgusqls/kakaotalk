package observer;

public interface Observer {
    String getId();
    void update(String message);
}