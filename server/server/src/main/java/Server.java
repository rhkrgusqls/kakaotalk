import observer.ChatGroup;
import console.ObserverConsole;

public class Server {
    public static void main(String[] args) {
        ChatGroup chatGroup = new ChatGroup();

        ObserverConsole console = new ObserverConsole(chatGroup);

        console.addObserver("ahnys2024@daum.net");
        console.addObserver("choijh555@outlook.com");

        chatGroup.notifyGroup("1", "Hello Room 1!");
        console.addObserver("hwangsj99@gmail.com");
        console.addObserver("unknown");
        console.addObserver("choijh555@outlook.com");
        chatGroup.notifyGroup("1", "Hello Room 2!");
        chatGroup.notifyGroup("2", "Hello Room 3!");
    }
}
