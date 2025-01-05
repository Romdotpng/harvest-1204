package tech.harvest.core.features.event;

import tech.harvest.core.types.event.EventArgument;
import tech.harvest.core.types.event.EventListener;
import net.minecraft.network.packet.Packet;

public class SendPacketEvent extends EventArgument {
    private final Packet<?> packet;

    public SendPacketEvent(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return this.packet;
    }

    @Override
    public void call(EventListener listener) {
        listener.onSendPacket(this);
    }
}
