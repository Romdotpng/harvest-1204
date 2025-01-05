package tech.harvest.core.types.event;

import java.util.ArrayList;
import java.util.List;
import tech.harvest.core.util.LoginApi;

public class EventManager {
    private final List<EventListener> LISTENER_REGISTRY = new ArrayList<>();

    public void call(EventArgument argument) {
        if (!LoginApi.logged) {
            return;
        }
        new ArrayList<>(this.LISTENER_REGISTRY).forEach(argument::call);
    }

    public void register(EventListener listener) {
        this.LISTENER_REGISTRY.add(listener);
    }

    public boolean unregister(EventListener listener) {
        return this.LISTENER_REGISTRY.remove(listener);
    }
}
