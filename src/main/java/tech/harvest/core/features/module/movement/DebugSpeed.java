package tech.harvest.core.features.module.movement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Direction;
import tech.harvest.core.features.event.*;
import tech.harvest.core.features.module.combat.KillAura;
import tech.harvest.core.features.module.combat.Velocity;
import tech.harvest.core.features.setting.BooleanSetting;
import tech.harvest.core.features.setting.DoubleSetting;
import tech.harvest.core.features.setting.ModeSetting;
import tech.harvest.core.types.module.Module;
import tech.harvest.core.types.module.ModuleCategory;
import tech.harvest.core.util.MCTimerUtil;
import tech.harvest.core.util.MoveUtil;
import tech.harvest.core.util.RotationUtil;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;

public class DebugSpeed extends Module {
    private final DoubleSetting timerSpeed = DoubleSetting.build().name("Timer Speed").value(5.0).range(1.0, 5.0).end();
    private final ModeSetting mode = ModeSetting.build().name("Mode").value("Matrix").option("Matrix", "ShotBow", "Legit Fast").end();
    private final BooleanSetting breakAnnoy = BooleanSetting.build().name("Break Annoy").value(true).end();
    private final BooleanSetting targetStrafe = BooleanSetting.build().name("Target Strafe").value(true).end();
    private final BooleanSetting targetStrafeWhileSpace = BooleanSetting.build().name("Target Strafe While Space").value(true).end();
    private float yaw;
    private int jumpCount;
    private int airTick;
    private boolean direction;
    boolean shouldDisable;
    List<Packet<?>> packets = new ArrayList<>();

    public static boolean isToggled = false;

    public DebugSpeed() {
        super("DebugSpeed", "", ModuleCategory.Movement);
        this.getSettings().addAll(Arrays.asList(this.timerSpeed, this.mode, this.breakAnnoy, this.targetStrafe, this.targetStrafeWhileSpace));
    }

    @Override
    public String getPrefix() {
        return this.mode.getValue();
    }

    @Override
    public void onEnable() {
        this.jumpCount = 0;
        this.airTick = 0;
        isToggled = true;
        this.yaw = mc.player.getYaw();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        isToggled = false;
        MCTimerUtil.setTimer(1.0f);
        switch (this.mode.getValue()) {
            case ("ShotBow"): {
                sendMatrixPacket();
            }
        }
        super.onDisable();
    }

    @Override
    public void onMove(MoveEvent event) {
        super.onMove(event);
    }

    @Override
    public void onUpdateVelocity(UpdateVelocityEvent event) {
        switch (this.mode.getValue()) {
            case "Matrix": {
                if (!MoveUtil.isMoving()) {
                    return;
                }
                event.yaw = this.yaw + 0.1f;
            }
        }
        super.onUpdateVelocity(event);
    }

    @Override
    public void onPreUpdate(PreUpdateEvent event) {
        this.airTick = mc.player.isOnGround() ? 0 : ++this.airTick;
        if (mc.player.horizontalCollision) {
            this.direction = !this.direction;
        }
        switch (this.mode.getValue()) {
            case "Legit Fast": {
                if (!MoveUtil.isMoving()) {
                    this.jumpCount = 0;
                    return;
                }
                if (KillAura.getTarget() == null && mc.player.isOnGround() && !mc.player.isTouchingWater()) {
                    mc.player.jump();
                    ++this.jumpCount;
                }
                mc.player.setVelocity(mc.player.getVelocity().x, mc.player.getVelocity().y - 0.0034, mc.player.getVelocity().z);
                break;
            }
            case "Matrix": {
                float speed;
                if (!MoveUtil.isMoving()) {
                    this.jumpCount = 0;
                    return;
                }
                DebugSpeed.mc.player.setSprinting(true);
                double yaw = MoveUtil.getDirection(RotationUtil.virtualYaw);
                MCTimerUtil.setTimer(1.0f);
                double radius = 4.0;
                if (KillAura.getTarget() != null && this.targetStrafe.getValue() && (!this.targetStrafeWhileSpace.getValue() || mc.options.jumpKey.isPressed())) {
                    MCTimerUtil.setTimer((float)this.timerSpeed.getValue());
                    yaw = RotationUtil.rotation(KillAura.getTarget().getPos(), mc.player.getPos())[0];
                    if (!(mc.player.getPos().distanceTo(KillAura.getTarget().getPos()) > 4.0)) {
                        float scale = System.currentTimeMillis() - Velocity.lastVelocity > 3000L ? 70 : 90;
                        yaw = this.direction ? yaw + scale : yaw - scale;
                    }
                    yaw = Math.toRadians(yaw);
                }
                this.yaw = RotationUtil.rotation(mc.player.getPos().add(-Math.sin((float)yaw), mc.player.getVelocity().y, Math.cos((float)yaw)), mc.player.getPos())[0];
                if (mc.player.isOnGround()) {
                    ++this.jumpCount;
                    speed = (float)MoveUtil.getSpeed();
                    mc.player.setVelocity(-Math.sin((float)yaw) * (double)speed, mc.player.getVelocity().y, Math.cos((float)yaw) * (double)speed);
                    if (!mc.options.jumpKey.isPressed()) {
                        mc.player.jump();
                    }
                    speed = (float)MoveUtil.getSpeed();
                    mc.player.setVelocity(-Math.sin((float)yaw) * (double)speed, mc.player.getVelocity().y, Math.cos((float)yaw) * (double)speed);
                }
                if (System.currentTimeMillis() - Velocity.lastVelocity < 1500L) {
                    if (mc.player.isOnGround()) {
                        Velocity.lastVelocity = System.currentTimeMillis() - 1500L;
                    }
                    speed = (float)MoveUtil.getSpeed() * 0.99999f;
                    mc.player.setVelocity(-Math.sin((float)yaw) * (double)speed, mc.player.getVelocity().y + 0.0034, Math.cos((float)yaw) * (double)speed);
                    break;
                }
                mc.player.setVelocity(mc.player.getVelocity().x, mc.player.getVelocity().y - 0.0034, mc.player.getVelocity().z);
                break;
            }
            case "ShotBow": {
                // made in nigeria. made by rom

                // sui ren no ha迫害
                if (breakAnnoy.getValue()) {
                    int x = -2;
                    while (x <= 2) {
                        for (int y = -2; y <= 2; y++) {
                            for (int z = -2; z <= 2; z++) {
                                if (mc.world.getBlockState(mc.player.getBlockPos().add(x, y, z)).getBlock() == Blocks.LILY_PAD) {
                                    mc.interactionManager.updateBlockBreakingProgress(mc.player.getBlockPos().add(x, y, z), Direction.UP);
                                    mc.player.swingHand(Hand.MAIN_HAND);
                                    break;
                                }
                            }
                        }
                        x++;
                    }
                }

                // base
                if (mc.interactionManager.isBreakingBlock()) {
                    MCTimerUtil.setTimer(1.0f);
                    return;
                }
                MCTimerUtil.setTimer((float) timerSpeed.getValue());
                if (mc.player.isSprinting()) {
                    mc.player.setSprinting(true);
                    mc.options.sprintKey.setPressed(true);
                    mc.player.setSprinting(true);
                }
                else {
                    mc.player.setSprinting(false);
                    mc.options.sprintKey.setPressed(false);
                    mc.player.setSprinting(false);
                }
                if (mc.player.isSubmergedInWater()) {
                    return;
                }
                if (mc.player.isInLava()) {
                    mc.player.setVelocity(mc.player.getVelocity().multiply(0.6, 1.0, 0.6));
                }
                else {
                    float scala = 0.7f;
                    if (mc.player.getStatusEffects().stream().anyMatch(p -> p.getEffectType() == StatusEffects.SPEED)) {
                        int amp = mc.player.getStatusEffects().stream().filter(p -> p.getEffectType() == StatusEffects.SPEED).findAny().get().getAmplifier();
                        switch (amp) {
                            case 2: {
                                scala = 0.5f;
                                break;
                            }
                        }
                    }
                    mc.player.setVelocity(mc.player.getVelocity().multiply(scala, 1.0, scala));
                }
                sendMatrixPacket();
                break;
            }
        }
        super.onPreUpdate(event);
    }

    private void sendMatrixPacket() {
        this.shouldDisable = true;
        int index = 0;
        for (Packet<?> p : this.packets) {
            if (p instanceof PlayerActionC2SPacket) {
                mc.getNetworkHandler().sendPacket(p);
            } else {
                ++index;
                double x = mc.player.getX();
                double y = mc.player.getY();
                double z = mc.player.getZ();
                if (index % 2 != 0) {
                    x = mc.player.prevX;
                    y = mc.player.prevY;
                    z = mc.player.prevZ;
                }
                if (p instanceof PlayerMoveC2SPacket.PositionAndOnGround o) {
                    mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(o.getX(x), o.getY(y), o.getZ(z), o.isOnGround()));
                }
                else if (p instanceof PlayerMoveC2SPacket.LookAndOnGround o2) {
                    mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(o2.getYaw(mc.player.getYaw()), o2.getPitch(mc.player.getPitch()), o2.isOnGround()));
                }
                else if (p instanceof PlayerMoveC2SPacket.Full o3) {
                    mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.Full(o3.getX(x), o3.getY(y), o3.getZ(z), o3.getYaw(mc.player.getYaw()), o3.getPitch(mc.player.getPitch()), o3.isOnGround()));
                }
                else {
                    mc.getNetworkHandler().sendPacket(p);
                }
            }
        }
        this.shouldDisable = false;
        this.packets.clear();
    }

    @Override
    public void onGetPacket(GetPacketEvent event) {
        if (mc.player == null) {
            return;
        }
        Packet<?> packet = event.getPacket();
        if (packet instanceof UpdateSelectedSlotS2CPacket) {
            UpdateSelectedSlotS2CPacket updateSlotPacket = (UpdateSelectedSlotS2CPacket)packet;
            if (updateSlotPacket.getSlot() != mc.player.getInventory().selectedSlot) {
                mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().selectedSlot));
            }
            event.cancel();
        }
        super.onGetPacket(event);
    }

    @Override
    public void onSendPacket(SendPacketEvent event) {
        switch (this.mode.getValue()) {
            case ("ShotBow"): {
                if (this.shouldDisable) {
                    return;
                }
                Packet<?> p = event.getPacket();
                if (!(p instanceof PlayerActionC2SPacket)) {
                    Packet<?> packet = event.getPacket();
                    if (packet instanceof PlayerMoveC2SPacket) {
                        this.shouldDisable = false;
                    }
                }
            }
            break;
        }
        super.onSendPacket(event);
    }
}
