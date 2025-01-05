package tech.harvest.core.features.event;

import tech.harvest.core.types.event.EventArgument;
import tech.harvest.core.types.event.EventListener;

public class UpdateVelocityEvent extends EventArgument {
    public float speed;
    public float yaw;

    public UpdateVelocityEvent(float speed, float yaw) {
        this.speed = speed;
        this.yaw = yaw;
    }

    @Override
    public void call(EventListener listener) {
        listener.onUpdateVelocity(this);
    }
}
