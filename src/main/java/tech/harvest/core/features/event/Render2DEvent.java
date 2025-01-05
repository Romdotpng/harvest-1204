package tech.harvest.core.features.event;

import tech.harvest.core.types.event.EventArgument;
import tech.harvest.core.types.event.EventListener;
import net.minecraft.client.gui.DrawContext;

public class Render2DEvent extends EventArgument {
    private final float tick;
    private final DrawContext context;

    public Render2DEvent(float tick, DrawContext context) {
        this.tick = tick;
        this.context = context;
    }

    public float getTick() {
        return this.tick;
    }

    public DrawContext getContext() {
        return this.context;
    }

    @Override
    public void call(EventListener listener) {
        listener.onRender2D(this);
    }
}
