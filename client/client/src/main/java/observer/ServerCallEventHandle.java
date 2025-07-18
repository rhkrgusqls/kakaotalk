package observer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerCallEventHandle {

    private static final List<Observer> observers = new CopyOnWriteArrayList<>();

    public static void registerObserver(Observer observer) {
        observers.add(observer);
    }

    public static void unregisterObserver(Observer observer) {
        observers.remove(observer);
    }

    public static void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.onMessageReceived(message);
        }
    }
}
