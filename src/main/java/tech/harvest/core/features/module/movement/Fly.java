package tech.harvest.core.features.module.movement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import tech.harvest.core.features.event.MoveEvent;
import tech.harvest.core.features.event.PreUpdateEvent;
import tech.harvest.core.features.event.SendPacketEvent;
import tech.harvest.core.features.setting.DoubleSetting;
import tech.harvest.core.features.setting.ModeSetting;
import tech.harvest.core.types.module.Module;
import tech.harvest.core.types.module.ModuleCategory;
import tech.harvest.core.util.MCTimerUtil;
import tech.harvest.core.util.MoveUtil;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdatePlayerAbilitiesC2SPacket;

public class Fly extends Module {
    private final DoubleSetting speed = DoubleSetting.build().name("Speed").value(1.0).range(0.1, 10.0).end();
    private final ModeSetting mode = ModeSetting.build().name("Mode").option("Vanilla", "Debug").end();
    private final List<Packet<?>> packets = new ArrayList<>();
    private int stage;
    private int tpId;
    private boolean releasing;
    private float[] yOffsets = new float[]{0.0f, 0.097f, 0.11066f, 0.0426468f};
    private float[] speedOffsets = new float[0];

    public Fly() {
        super("Fly", "", ModuleCategory.Movement);
        this.getSettings().addAll(Arrays.asList(this.speed, this.mode));
    }

    private void release() {
        this.stage = 0;
        this.releasing = true;
        if (mc.getNetworkHandler() != null) {
            this.packets.forEach(p -> mc.getNetworkHandler().sendPacket(p));
        }
        this.packets.clear();
        this.releasing = false;
    }

    @Override
    public void onDisable() {
        this.release();
        MCTimerUtil.setTimer(1.0f);
        super.onDisable();
    }

    @Override
    public void onEnable() {
        this.stage = 0;
        this.yOffsets = new float[]{0.097f, 0.013660001f, -0.0680132f, -0.14805295f};
        super.onEnable();
    }

    @Override
    public void onSendPacket(SendPacketEvent event) {
        switch (this.mode.getValue()) {
            case "Debug": {
                if (this.releasing) {
                    return;
                }
                if (Fly.mc.player == null) {
                    this.release();
                    return;
                }
                boolean onGround = false;
                double yOffset = 0.0;
                Packet<?> packet = event.getPacket();
                if (packet instanceof PlayerMoveC2SPacket.Full) {
                    PlayerMoveC2SPacket.Full packet2 = (PlayerMoveC2SPacket.Full)packet;
                    this.packets.add(new PlayerMoveC2SPacket.Full(packet2.getX(Fly.mc.player.getX()), packet2.getY(Fly.mc.player.getY()) - yOffset, packet2.getZ(Fly.mc.player.getZ()), 0.0f, 0.0f, true));
                } else {
                    packet = event.getPacket();
                    if (packet instanceof PlayerMoveC2SPacket.PositionAndOnGround) {
                        PlayerMoveC2SPacket.PositionAndOnGround packet3 = (PlayerMoveC2SPacket.PositionAndOnGround)packet;
                        this.packets.add(new PlayerMoveC2SPacket.PositionAndOnGround(packet3.getX(Fly.mc.player.getX()), packet3.getY(Fly.mc.player.getY()) - yOffset, packet3.getZ(Fly.mc.player.getZ()), true));
                    } else {
                        packet = event.getPacket();
                        if (packet instanceof PlayerMoveC2SPacket.LookAndOnGround) {
                            PlayerMoveC2SPacket.LookAndOnGround packet4 = (PlayerMoveC2SPacket.LookAndOnGround)packet;
                            this.packets.add(new PlayerMoveC2SPacket.LookAndOnGround(0.0f, 0.0f, true));
                        } else {
                            packet = event.getPacket();
                            if (packet instanceof PlayerMoveC2SPacket.OnGroundOnly) {
                                PlayerMoveC2SPacket.OnGroundOnly packet5 = (PlayerMoveC2SPacket.OnGroundOnly)packet;
                                this.packets.add(new PlayerMoveC2SPacket.OnGroundOnly(true));
                            } else {
                                event.cancel();
                            }
                        }
                    }
                }
                event.cancel();
            }
        }
        super.onSendPacket(event);
    }

    @Override
    public void onMove(MoveEvent event) {
        switch (this.mode.getValue()) {
            case "Debug": {
                MCTimerUtil.setTimer(500.0f);
            }
        }
        super.onMove(event);
    }

    @Override
    public void onPreUpdate(PreUpdateEvent event) {
        switch (this.mode.getValue()) {
            case "Debug": {
                this.release();
                if (Fly.mc.options.jumpKey.isPressed()) {
                    return;
                }
                mc.player.setVelocity(mc.player.getVelocity().x, 3.0E-4, mc.player.getVelocity().z);
                ++this.stage;
                double maxspeed = 0.00425;
                double speed = mc.player.getVelocity().horizontalLength();
                if (!(speed > maxspeed)) break;
                double scale = maxspeed / speed;
                mc.player.setVelocity(mc.player.getVelocity().x * scale, mc.player.getVelocity().y, Fly.mc.player.getVelocity().z * scale);
                break;
            }
            case "Vanilla": {
                PlayerAbilities playerCapabilities = new PlayerAbilities();
                playerCapabilities.flying = true;
                playerCapabilities.allowFlying = true;
                mc.getNetworkHandler().sendPacket(new UpdatePlayerAbilitiesC2SPacket(playerCapabilities));
                if (mc.options.jumpKey.isPressed()) {
                    mc.player.setVelocity(mc.player.getVelocity().x, this.speed.getValue() / 2.0, mc.player.getVelocity().z);
                    return;
                }
                if (mc.options.sneakKey.isPressed()) {
                    mc.player.setVelocity(mc.player.getVelocity().x, -this.speed.getValue() / 2.0, mc.player.getVelocity().z);
                    return;
                }
                mc.player.setVelocity(mc.player.getVelocity().x, 0.0, mc.player.getVelocity().z);
                MoveUtil.strafe((float)this.speed.getValue());
            }
        }
    }
}