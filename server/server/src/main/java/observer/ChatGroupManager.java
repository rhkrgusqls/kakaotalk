package observer;

public class ChatGroupManager {
    private static final ChatGroup instance = new ChatGroup();
    public static ChatGroup getInstance() {
        return instance;
    }
}
