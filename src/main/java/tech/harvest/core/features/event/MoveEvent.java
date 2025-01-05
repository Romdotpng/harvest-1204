package tech.harvest.core.features.event;

import tech.harvest.core.types.event.EventArgument;
import tech.harvest.core.types.event.EventListener;

public class MoveEvent extends EventArgument {
    public double x;
    public double y;
    public double z;

    public MoveEvent(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void call(EventListener listener) {
        listener.onMove(this);
    }
}
