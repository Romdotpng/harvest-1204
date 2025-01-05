package tech.harvest.core.features.module.movement;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import tech.harvest.core.features.event.GetPacketEvent;
import tech.harvest.core.features.event.MotionEvent;
import tech.harvest.core.features.event.SendPacketEvent;
import tech.harvest.core.features.setting.DoubleSetting;
import tech.harvest.core.features.setting.ModeSetting;
import tech.harvest.core.types.module.Module;
import tech.harvest.core.types.module.ModuleCategory;
import tech.harvest.core.util.MCTimerUtil;
import tech.harvest.core.util.MoveUtil;
import tech.harvest.core.util.PlayerUtil;
import net.minecraft.util.math.MathHelper;

import java.util.Arrays;

public class AntiVoid extends Module {
    private final ModeSetting mode = ModeSetting.build().name("Mode").value("Test").option("Fly", "Test").end();
    private final DoubleSetting distance = DoubleSetting.build().name("Distance").value(3.0).range(3.0, 6.0).end();

    Packet<?>[] packets;
    int int0;

    double realx;
    double realy;
    double realz;

    boolean start = false;

    public AntiVoid() {
        super("AntiVoid", "Prevents falling into void once based on distance", ModuleCategory.Movement);
        this.packets = new Packet[1000];
        getSettings().addAll(Arrays.asList(mode, distance));
    }

    @Override
    public void onEnable() {
        int0 = 0;

        start = false;
    }

    @Override
    public void onDisable() {
        if (mc.player != null) {
            for (int i = 0; i < int0; i++) {
                mc.player.networkHandler.sendPacket(packets[i]);
            }
            MCTimerUtil.setTimer(1);
        }
    }

    @Override
    public void onMotion(MotionEvent event) {
        if (mc.player != null && mc.world != null) {
            switch (this.mode.getValue()) {
                case "Fly": {
                    if (mc.player.fallDistance > distance.getValue() && !
                            PlayerUtil.isBlockUnder() && mc.player.getY() + mc.player.getVelocity().y < MathHelper.floor(mc.player.getY())) {
                        mc.player.setVelocity(mc.player.getVelocity().x, MathHelper.floor(mc.player.getY()) - mc.player.getY(), mc.player.getVelocity().z);
                        if (mc.player.getVelocity().y == 10) {
                            event.onGround = true;
                            mc.player.setOnGround(true);
                        }
                    }
                    break;
                }
                case "Test": {
                    if (mc.player.getY() > -10) {
                        boolean a = false;

                        for (int i = 0; i < 50; i++) {
                            if (!mc.world.getBlockState(new BlockPos((int) (mc.player.getX() + mc.player.getVelocity().x), (int) (mc.player.getY() - i), (int) (mc.player.getZ() + mc.player.getVelocity().z))).isAir()) {
                                a = true;
                            }
                        }

                        boolean b = mc.world.getBlockState(new BlockPos((int) realx, (int) (realy + 0.5), (int) realz)).isAir();

                        if (!start && !a && !mc.player.verticalCollision) {
                            start = true;
                        } else if (mc.player.fallDistance > 6 && !a && mc.player.getVelocity().y < 0) {
                            if (b) {
                                start = false;
                                int0 = 0;
                                mc.player.setPos(realx, realy, realz);
                                MoveUtil.strafe(0.01f);
                                mc.player.setOnGround(true);
                            } else {
                                start = false;
                                for (int i = 0; i < int0; i++) {
                                    mc.player.networkHandler.sendPacket(packets[i]);
                                }
                                int0 = 0;
                                //mc.inGameHud.getChatHud().addMessage(Text.literal("failed"));
                            }
                        }

                        if ((mc.player.isOnGround() || mc.player.verticalCollision)) {
                            start = false;
                            for (int i = 0; i < int0; i++) {
                                mc.player.networkHandler.sendPacket(packets[i]);
                            }
                            int0 = 0;
                        }
                        break;
                    }
                    else {
                        start = false;
                        for (int i = 0; i < int0; i++) {
                            mc.player.networkHandler.sendPacket(packets[i]);
                        }
                        int0 = 0;
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void onSendPacket(SendPacketEvent event) {
        if (event.getPacket() instanceof PlayerMoveC2SPacket) {
            if (event.getPacket() instanceof PlayerMoveC2SPacket.PositionAndOnGround wtf) {
                if (wtf.isOnGround()) {
                    realx = wtf.getX(mc.player.getX());
                    realy = wtf.getY(mc.player.getY());
                    realz = wtf.getZ(mc.player.getZ());
                }
            } else if (event.getPacket() instanceof PlayerMoveC2SPacket.LookAndOnGround wtf2) {
                if (wtf2.isOnGround()) {
                    realx = wtf2.getX(mc.player.getX());
                    realy = wtf2.getY(mc.player.getY());
                    realz = wtf2.getZ(mc.player.getZ());
                }
            }
        }
        switch (this.mode.getValue()) {
            case "Test": {
                if (start) {
                    this.packets[this.int0] = event.getPacket();
                    this.int0++;
                    event.cancel();
                }
                break;
            }
        }
    }

    @Override
    public void onGetPacket(GetPacketEvent event) {
        if (mc.player != null) {
            switch (this.mode.getValue()) {
                case "Test": {
                    if (event.getPacket() instanceof PlayerPositionLookS2CPacket) {
                        start = false;
                        int0 = 0;
                        mc.player.setVelocity(0, 0, 0);
                    }
                    break;
                }
            }
        }
    }
}
