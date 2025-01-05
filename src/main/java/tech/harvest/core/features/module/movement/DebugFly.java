package tech.harvest.core.features.module.movement;

import java.util.Arrays;
import java.util.Collections;

import tech.harvest.core.features.event.GetPacketEvent;
import tech.harvest.core.features.event.PreUpdateEvent;
import tech.harvest.core.features.event.SendPacketEvent;
import tech.harvest.core.features.setting.ModeSetting;
import tech.harvest.core.types.module.Module;
import tech.harvest.core.types.module.ModuleCategory;
import tech.harvest.core.util.MoveUtil;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;

public class DebugFly extends Module {
    private final ModeSetting mode = ModeSetting.build().name("Mode").value("Normal").option("Normal", "Clip").end();
    private double moveSpeed;
    private boolean started;
    private boolean notUnder;
    private boolean clipped;
    private boolean teleport;

    public DebugFly() {
        super("DebugFly", "Debug Fly", ModuleCategory.Movement);
        getSettings().add(this.mode);
    }

    @Override
    public void onEnable() {
        this.moveSpeed = 0.0d;
        this.notUnder = false;
        this.started = false;
        this.clipped = false;
        this.teleport = false;
        super.onEnable();
    }

    @Override
    public void onSendPacket(SendPacketEvent event) {
        super.onSendPacket(event);
    }

    @Override
    public void onGetPacket(GetPacketEvent event) {
        if (this.teleport && (event.getPacket() instanceof PlayerPositionLookS2CPacket)) {
            mc.inGameHud.getChatHud().addMessage(Text.literal("Teleported"));
            event.cancel();
            this.teleport = false;
        }
        super.onGetPacket(event);
    }

    @Override
    public void onPreUpdate(PreUpdateEvent event) {
        if (!this.mode.getValue().equals("Normal")) {
            Box bb = mc.player.getBoundingBox().offset(0.0d, 1.0d, 0.0d);
            if (mc.world.canCollide(mc.player, bb) && !this.started) {
                this.notUnder = true;
                if (!this.clipped) {
                    this.clipped = true;
                    mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.Full(mc.player.getX(), mc.player.getY(), mc.player.getZ(), mc.player.getYaw(), mc.player.getPitch(), false));
                    mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.Full(mc.player.getX(), mc.player.getY() - 0.1d, mc.player.getZ(), mc.player.getYaw(), mc.player.getPitch(), false));
                    mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.Full(mc.player.getX(), mc.player.getY(), mc.player.getZ(), mc.player.getYaw(), mc.player.getPitch(), false));
                    this.teleport = true;
                } else {
                    return;
                }
            }
            MoveUtil.strafe();
            return;
        }
        Box bb2 = mc.player.getBoundingBox().offset(0.0d, 1.0d, 0.0d);
        if (this.started) {
            mc.player.setVelocity(mc.player.getVelocity().add(0.0d, 0.025d, 0.0d));
            double d = this.moveSpeed * 0.9350000023841858d;
            this.moveSpeed = d;
            MoveUtil.strafe((float) d);
            if (mc.player.getVelocity().y < -0.5d && mc.world.getBlockState(mc.player.getBlockPos()).getBlock() == Blocks.AIR) {
                toggle();
            }
        }
        if (!mc.world.canCollide(mc.player, bb2) && !this.started) {
            this.started = true;
            mc.player.jump();
            this.moveSpeed = 9.0d;
            MoveUtil.strafe((float) 9.0d);
        }
        super.onPreUpdate(event);
    }
}
