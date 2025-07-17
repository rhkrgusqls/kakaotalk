package observer;

import java.util.*;

public class ChatGroup {
    private Map<String, Set<Observer>> groupObservers = new HashMap<>();

    public void addObserver(String group, Observer observer) {
        groupObservers.computeIfAbsent(group, k -> new HashSet<>()).add(observer);
    }

    public void removeObserver(String group, Observer observer) {
        Set<Observer> set = groupObservers.get(group);
        if (set != null) {
            set.remove(observer);
        }
    }

    public void notifyGroup(String group, String message) {
        Set<Observer> set = groupObservers.get(group);
        if (set != null) {
            for (Observer o : set) {
                o.update(message);
            }
        }
    }

    public void notifyAllGroups(String message) {
        for (Set<Observer> set : groupObservers.values()) {
            for (Observer o : set) {
                o.update(message);
            }
        }
    }
}
