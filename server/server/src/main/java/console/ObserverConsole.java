package console;

import controller.mainController;

import observer.ChatGroup;
import observer.Observer;
import observer.ObserverImpl;

import java.util.regex.*;
import java.util.*;

public class ObserverConsole {
    private ChatGroup chatGroup;

    public ObserverConsole(ChatGroup chatGroup) {
        this.chatGroup = chatGroup;
    }

    public void addObserver(String id) {
        String rawData = mainController.loadChatRoomData(id);

        Pattern pattern = Pattern.compile("&chatRoomNum\\$(\\d+)");
        Matcher matcher = pattern.matcher(rawData);

        while (matcher.find()) {
            String roomNum = matcher.group(1);
            Observer observer = new ObserverImpl(id);
            chatGroup.addObserver(roomNum, observer);
        }
    }
}
