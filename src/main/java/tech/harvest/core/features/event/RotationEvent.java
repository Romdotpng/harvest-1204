package tech.harvest.core.features.event;

import tech.harvest.core.types.event.EventArgument;
import tech.harvest.core.types.event.EventListener;

public class RotationEvent extends EventArgument {
    private float yaw;
    private float pitch;
    private float yawSpeed = 180.0f;
    private float pitchSpeed = 180.0f;

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setYawSpeed(float yawSpeed) {
        this.yawSpeed = yawSpeed;
    }

    public void setPitchSpeed(float pitchSpeed) {
        this.pitchSpeed = pitchSpeed;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public float getYawSpeed() {
        return this.yawSpeed;
    }

    public float getPitchSpeed() {
        return this.pitchSpeed;
    }

    public RotationEvent(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public void call(EventListener listener) {
        listener.onRotation(this);
    }
}
