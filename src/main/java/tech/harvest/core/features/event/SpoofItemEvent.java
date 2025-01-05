package tech.harvest.core.features.event;

import tech.harvest.core.types.event.EventArgument;
import tech.harvest.core.types.event.EventListener;

public class SpoofItemEvent extends EventArgument {
    private int current;

    public SpoofItemEvent(int current) {
        this.current = current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getCurrent() {
        return this.current;
    }

    @Override
    public void call(EventListener listener) {
        listener.onSpoofItem(this);
    }
}
