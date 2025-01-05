package tech.harvest.core.features.event;

import tech.harvest.core.types.event.EventArgument;
import tech.harvest.core.types.event.EventListener;
import net.minecraft.client.input.Input;

public class InputEvent extends EventArgument {
    private final Input input;
    private final float slowDownFactor;
    public boolean moveFix = false;

    public InputEvent(Input input, float slowDownFactor) {
        this.input = input;
        this.slowDownFactor = slowDownFactor;
    }

    public Input getInput() {
        return this.input;
    }

    public float getSlowDownFactor() {
        return this.slowDownFactor;
    }

    @Override
    public void call(EventListener listener) {
        listener.onInput(this);
    }
}
