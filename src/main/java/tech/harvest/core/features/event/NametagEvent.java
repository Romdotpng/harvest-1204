package tech.harvest.core.features.event;

import tech.harvest.core.types.event.EventArgument;
import tech.harvest.core.types.event.EventListener;
import net.minecraft.entity.LivingEntity;

public class NametagEvent extends EventArgument {
    private final LivingEntity entity;

    public NametagEvent(LivingEntity entity) {
        this.entity = entity;
    }

    public LivingEntity getEntity() {
        return this.entity;
    }

    @Override
    public void call(EventListener listener) {
        listener.onNametag(this);
    }
}
