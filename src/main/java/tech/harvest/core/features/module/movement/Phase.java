package tech.harvest.core.features.module.movement;

import tech.harvest.core.features.event.PreUpdateEvent;
import tech.harvest.core.features.event.SendPacketEvent;
import tech.harvest.core.types.module.Module;
import tech.harvest.core.types.module.ModuleCategory;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class Phase extends Module {

    public Phase() {
        super("Phase", "", ModuleCategory.Movement);
    }

    @Override
    public void onSendPacket(SendPacketEvent event) {
        boolean skipPacket = false;
        if (!skipPacket) {
            super.onSendPacket(event);
        }
    }

    @Override
    public void onPreUpdate(PreUpdateEvent event) {
        if (mc.options.attackKey.isPressed()) {
            mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() + 1.0E-5d, mc.player.getZ(), false));
            mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() - 1.0E-5d, mc.player.getZ(), false));
        }
        super.onPreUpdate(event);
    }
}
