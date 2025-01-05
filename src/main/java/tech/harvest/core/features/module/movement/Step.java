package tech.harvest.core.features.module.movement;

import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import tech.harvest.core.features.event.MotionEvent;
import tech.harvest.core.features.setting.ModeSetting;
import tech.harvest.core.types.module.Module;
import tech.harvest.core.types.module.ModuleCategory;
import tech.harvest.core.util.BypassUtil;
import tech.harvest.core.util.MCTimerUtil;
import tech.harvest.core.util.MoveUtil;

import java.util.Arrays;

public class Step extends Module {
    private final ModeSetting mode = ModeSetting.build().name("Mode").value("Matrix").option("Matrix").end();

    int int0;
    int int1;
    int int2;
    int int3;

    double double0 = 0;
    double double1 = 0;
    double double2 = 0;

    public Step() {
        super("Step", "test module mf", ModuleCategory.Movement);
        this.getSettings().addAll(Arrays.asList(this.mode));
    }

    @Override
    public void onEnable() {
        int0 = 0;
        int1 = 0;
        int2 = 0;
        int3 = 0;
        double0 = 0;
        double1 = 0;
        double2 = 0;
    }

    @Override
    public void onMotion(MotionEvent event) {
        switch (mode.getValue()) {
            case ("Matrix"): {
                double yaw = Math.toRadians(MoveUtil.getdir());
                double a = -Math.sin((float) yaw);
                double b = Math.cos((float) yaw);
                double motion = 0.4D;

                if (mc.player.verticalCollision && mc.player.isOnGround() && mc.player.fallDistance >= -0.01 && MoveUtil.getdir() != -1 && (MoveUtil.getms() != 0 || MoveUtil.getmf() != 0)) {

                    if (mc.world.getBlockState(new BlockPos((int) mc.player.getX(), (int) mc.player.getY(), (int) mc.player.getZ())).isAir() &&
                            match(mc.player.getX(), mc.player.getY() + 0.6, mc.player.getZ()) &&
                            mc.world.getBlockState(new BlockPos((int) (mc.player.getX() + a * motion), (int) (mc.player.getY() + 1.4), (int) (mc.player.getZ() + b * motion))).isAir() &&
                            mc.world.getBlockState(new BlockPos((int) (mc.player.getX() + a * motion), (int) (mc.player.getY() + 2.1), (int) (mc.player.getZ() + b * motion))).isAir()) {

                        int1 = 0;
                        int0 = 1;
                        double1 = mc.player.getY() + 1;
                        double0 = mc.player.getX() + a * motion;
                        double2 = mc.player.getZ() + b * motion;
                        mc.player.jump();
                        if (!mc.player.isSprinting())
                            mc.player.setSprinting(true);
                        MCTimerUtil.setTimer(1.1f);
                        int3 = 0;

                    } else if (mc.world.getBlockState(new BlockPos((int) mc.player.getX(), (int) mc.player.getY(), (int) mc.player.getZ())).isAir() &&
                            !mc.world.getBlockState(new BlockPos((int) (mc.player.getX() + a * motion), (int) (mc.player.getY() + 1.4), (int) (mc.player.getZ() + b * motion))).isAir() &&
                            mc.world.getBlockState(new BlockPos((int) (mc.player.getX() + a * motion), (int) (mc.player.getY() + 2.1), (int) (mc.player.getZ() + b * motion))).isAir() &&
                            mc.world.getBlockState(new BlockPos((int) (mc.player.getX() + a * motion), (int) (mc.player.getY() + 3.1), (int) (mc.player.getZ() + b * motion))).isAir()) {

                        int1 = 1;
                        int0 = 1;
                        double1 = mc.player.getY() + 2;
                        double0 = mc.player.getX() + a * motion;
                        double2 = mc.player.getZ() + b * motion;
                        mc.player.jump();
                        if (!mc.player.isSprinting())
                            mc.player.setSprinting(true);
                        MCTimerUtil.setTimer(1.1f);
                        int3 = 0;
                    }

                } else if (int0 > 0) {
                    if (int1 == 0) {
                        if (!mc.player.isOnGround() && int0 == 1 && BypassUtil.IsMatrixOnGround()) {
                            event.onGround = true;
                            mc.player.setOnGround(true);
                            mc.player.setVelocity(mc.player.getVelocity().x, 0.42, mc.player.getVelocity().z);
                        }
                        int0++;

                    } else if (int1 == 1) {
                        if (int0 == 2 && BypassUtil.IsMatrixOnGround()) {
                            event.onGround = true;
                            mc.player.setOnGround(true);
                            mc.player.setVelocity(mc.player.getVelocity().x, 0.42, mc.player.getVelocity().z);
                        }
                        int0++;
                    }

                }

                if ((mc.player.getY() >= double1 || int0 >= 15 || mc.player.verticalCollision) && int3 == 0) {
                    MCTimerUtil.setTimer(1f);
                    MoveUtil.strafe(0.15f);
                    int3 = 1;
                }
                break;
            }
        }
    }

    boolean match(double x, double y, double z) {
        double yawt = Math.toRadians(MoveUtil.getdir());
        double xt = -Math.sin((float) yawt);
        double zt = Math.cos((float) yawt);
        double mot = 0.4D;

        BlockPos pos = new BlockPos((int) (x + xt * mot), (int) y, (int) (z + zt * mot));
        BlockState state = mc.world.getBlockState(pos);

        if (!state.isAir() &&
                !(state.getBlock() instanceof SlabBlock) &&
                !(state.getBlock() instanceof StairsBlock) &&
                !(state.getBlock() instanceof SignBlock) &&
                !(state.getBlock() instanceof PressurePlateBlock) &&
                !(state.getBlock() instanceof FlowerBlock) &&
                !(state.getBlock() instanceof TallPlantBlock) &&
                !(state.getBlock() instanceof ButtonBlock)) {
            return true;
        }
        return false;
    }
}
