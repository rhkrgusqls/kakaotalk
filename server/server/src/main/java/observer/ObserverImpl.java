package observer;

import java.util.Objects;

public class ObserverImpl implements Observer {
    private final String id;

    public ObserverImpl(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void update(String message) {
        System.out.println("[" + id + "] Received: " + message);
    }

    // ID 기반 동등성 비교
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ObserverImpl)) return false;
        ObserverImpl that = (ObserverImpl) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}