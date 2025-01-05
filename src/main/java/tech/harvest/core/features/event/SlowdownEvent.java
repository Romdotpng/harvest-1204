package tech.harvest.core.features.event;

import java.util.Objects;
import tech.harvest.core.types.event.EventArgument;
import tech.harvest.core.types.event.EventListener;

public class SlowdownEvent extends EventArgument {
    private final boolean slowdown = true;

    public boolean isSlowdown() {
        Objects.requireNonNull(this);
        return true;
    }

    @Override
    public void call(EventListener listener) {
        listener.onSlowdown(this);
    }
}
