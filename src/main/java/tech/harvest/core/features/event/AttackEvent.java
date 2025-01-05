package tech.harvest.core.features.event;

import tech.harvest.core.types.event.EventArgument;
import tech.harvest.core.types.event.EventListener;
import net.minecraft.entity.Entity;

public class AttackEvent extends EventArgument {
    private final Entity target;

    public AttackEvent(Entity target) {
        this.target = target;
    }

    public Entity getTarget() {
        return this.target;
    }

    @Override
    public void call(EventListener listener) {
        listener.onAttack(this);
    }
}
