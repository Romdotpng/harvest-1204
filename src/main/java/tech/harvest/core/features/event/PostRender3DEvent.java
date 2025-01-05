package tech.harvest.core.features.event;

import tech.harvest.core.types.event.EventArgument;
import tech.harvest.core.types.event.EventListener;
import net.minecraft.client.util.math.MatrixStack;

public class PostRender3DEvent extends EventArgument {
    private final float tick;
    private final MatrixStack matrices;

    public PostRender3DEvent(float tick, MatrixStack matrices) {
        this.tick = tick;
        this.matrices = matrices;
    }

    public float getTick() {
        return this.tick;
    }

    public MatrixStack getMatrices() {
        return this.matrices;
    }

    @Override
    public void call(EventListener listener) {
        listener.onPostRender3D(this);
    }
}
