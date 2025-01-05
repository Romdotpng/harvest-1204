package tech.harvest.core.features.event;

import tech.harvest.core.types.event.EventArgument;
import tech.harvest.core.types.event.EventListener;

public class PostUpdateEvent extends EventArgument {
    @Override
    public void call(EventListener listener) {
        listener.onPostUpdate(this);
    }
}
