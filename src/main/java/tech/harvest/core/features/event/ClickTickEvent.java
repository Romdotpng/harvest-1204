package tech.harvest.core.features.event;

import tech.harvest.core.types.event.EventArgument;
import tech.harvest.core.types.event.EventListener;

public class ClickTickEvent extends EventArgument {
    @Override
    public void call(EventListener listener) {
        listener.onClickTick(this);
    }
}
