package tech.harvest.core.features.module.combat;

import java.util.Arrays;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.common.KeepAliveS2CPacket;
import net.minecraft.network.packet.s2c.play.*;
import tech.harvest.core.features.event.GetPacketEvent;
import tech.harvest.core.features.event.PreUpdateEvent;
import tech.harvest.core.features.setting.DoubleSetting;
import tech.harvest.core.features.setting.ModeSetting;
import tech.harvest.core.types.module.Module;
import tech.harvest.core.types.module.ModuleCategory;
import tech.harvest.core.util.MoveUtil;
import tech.harvest.core.util.RotationUtil;

public class Velocity extends Module {
    private final ModeSetting mode = ModeSetting.build().name("Mode").option("Vanilla", "Legit").end();
    private final DoubleSetting horizontal = DoubleSetting.build().name("Horizontal").value(0.0d).range(0.0d, 100.0d).end();
    private final DoubleSetting vertical = DoubleSetting.build().name("Vertical").value(0.0d).range(0.0d, 100.0d).end();
    public static long lastVelocity = System.currentTimeMillis();

    public Velocity() {
        super("Velocity", "Pre", ModuleCategory.Combat);
        getSettings().addAll(Arrays.asList(this.mode, this.horizontal, this.vertical));
    }

    @Override
    public void onPreUpdate(PreUpdateEvent event) {
        super.onPreUpdate(event);
    }

    @Override
    public void onGetPacket(GetPacketEvent event) {
        if (Velocity.mc.player == null || Velocity.mc.world == null) {
            return;
        }
        final Packet<?> packet2 = event.getPacket();
        if (packet2 instanceof final EntitySetHeadYawS2CPacket p) {
            return;
        }
        final Packet<?> packet3 = event.getPacket();
        if (packet3 instanceof final EntityVelocityUpdateS2CPacket p2) {
            if (p2.getId() != Velocity.mc.player.getId()) {
                return;
            }
        }
        final Packet<?> packet4 = event.getPacket();
        if (packet4 instanceof final EntityS2CPacket p3) {
            return;
        }
        final Packet<?> packet5 = event.getPacket();
        if (packet5 instanceof final EntityAnimationS2CPacket p4) {
            return;
        }
        final Packet<?> packet6 = event.getPacket();
        if (packet6 instanceof final EntityPositionS2CPacket p5) {
            return;
        }
        final Packet<?> packet7 = event.getPacket();
        if (packet7 instanceof final WorldTimeUpdateS2CPacket p6) {
            return;
        }
        final Packet<?> packet8 = event.getPacket();
        if (packet8 instanceof final BossBarS2CPacket p7) {
            return;
        }
        final Packet<?> packet9 = event.getPacket();
        if (packet9 instanceof final EntityAttributesS2CPacket p8) {
            return;
        }
        final Packet<?> packet10 = event.getPacket();
        if (packet10 instanceof final EntityEquipmentUpdateS2CPacket p9) {
            return;
        }
        final Packet<?> packet11 = event.getPacket();
        if (packet11 instanceof final BlockUpdateS2CPacket p10) {
            return;
        }
        final Packet<?> packet12 = event.getPacket();
        if (packet12 instanceof final ParticleS2CPacket p11) {
            return;
        }
        final Packet<?> packet13 = event.getPacket();
        if (packet13 instanceof final BlockBreakingProgressS2CPacket p12) {
            return;
        }
        final Packet<?> packet14 = event.getPacket();
        if (packet14 instanceof final ItemPickupAnimationS2CPacket p13) {
            return;
        }
        final Packet<?> packet15 = event.getPacket();
        if (packet15 instanceof final EntitiesDestroyS2CPacket p14) {
            return;
        }
        final Packet<?> packet16 = event.getPacket();
        if (packet16 instanceof final PlayerListHeaderS2CPacket p15) {
            return;
        }
        final Packet<?> packet17 = event.getPacket();
        if (packet17 instanceof final PlayerActionResponseS2CPacket p16) {
            return;
        }
        final Packet<?> packet18 = event.getPacket();
        if (packet18 instanceof final EntityTrackerUpdateS2CPacket p17) {
            return;
        }
        final Packet<?> packet19 = event.getPacket();
        if (packet19 instanceof final ChunkDeltaUpdateS2CPacket p18) {
            return;
        }
        final Packet<?> packet20 = event.getPacket();
        if (packet20 instanceof final WorldEventS2CPacket p19) {
            return;
        }
        final Packet<?> packet21 = event.getPacket();
        if (packet21 instanceof final KeepAliveS2CPacket p20) {
            return;
        }
        final Packet<?> packet22 = event.getPacket();
        if (packet22 instanceof final PlaySoundS2CPacket p21) {
            return;
        }
        final Packet<?> packet23 = event.getPacket();
        if (packet23 instanceof final PlayerListS2CPacket p22) {
            return;
        }
        final Packet<?> packet24 = event.getPacket();
        if (packet24 instanceof final ChunkDataS2CPacket p23) {
            return;
        }
        final Packet<?> packet25 = event.getPacket();
        if (packet25 instanceof final ChunkRenderDistanceCenterS2CPacket p24) {
            return;
        }
        final Packet<?> packet26 = event.getPacket();
        if (packet26 instanceof final BlockEventS2CPacket p25) {
            return;
        }
        final Packet<?> packet27 = event.getPacket();
        if (packet27 instanceof final BundleS2CPacket p26) {
            return;
        }
        final Packet<?> packet28 = event.getPacket();
        if (packet28 instanceof final UnloadChunkS2CPacket p27) {
            return;
        }
        final Packet<?> packet29 = event.getPacket();
        if (packet29 instanceof final OverlayMessageS2CPacket p28) {
            return;
        }
        final Packet<?> packet30 = event.getPacket();
        if (packet30 instanceof final RemoveEntityStatusEffectS2CPacket p29) {
            return;
        }
        final Packet<?> packet31 = event.getPacket();
        if (packet31 instanceof final LightUpdateS2CPacket p30) {
            return;
        }
        final Packet<?> packet32 = event.getPacket();
        if (packet32 instanceof final GameMessageS2CPacket p31) {
            return;
        }
        final Packet<?> packet33 = event.getPacket();
        if (packet33 instanceof final EntityDamageS2CPacket p32) {
            if (p32.entityId() == mc.player.getId() && p32.sourceTypeId() != 8) {
                lastVelocity = System.currentTimeMillis();
            }
        }
        if (this.mode.getValue().equals("Vanilla")) {
            final Packet<?> packet34 = event.getPacket();
            if (packet34 instanceof final EntityVelocityUpdateS2CPacket packet) {
                if (packet.getId() == mc.player.getId()) {
                    double scale = Math.hypot(packet.getVelocityX(), packet.getVelocityZ()) / 80000.0;
                    if (System.currentTimeMillis() - lastVelocity < 1000L) {
                        scale *= 1.0;
                    }
                    double yaw = MoveUtil.getDirection(mc.player.getYaw());
                    yaw = RotationUtil.rotation(mc.player.getPos().add(-Math.sin((float)yaw), mc.player.getVelocity().y, Math.cos((float)yaw)), mc.player.getPos())[0];
                    yaw = Math.toRadians(yaw);
                    mc.player.setVelocity(mc.player.getVelocity().add(-Math.sin((float)yaw) * scale, packet.getVelocityY() / 80000.0 * 10.0, Math.cos((float)yaw) * scale));
                    event.cancel();
                }
            }
        }
        else {
            final Packet<?> packet35 = event.getPacket();
            if (packet35 instanceof final EntityVelocityUpdateS2CPacket packet) {
                if (packet.getId() == Velocity.mc.player.getId()) {
                    Velocity.mc.player.jump();
                }
            }
        }
        super.onGetPacket(event);
    }
}
