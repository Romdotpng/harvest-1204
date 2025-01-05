package tech.harvest.core.util;

import tech.harvest.MCHook;
import net.minecraft.block.FlowerBlock;
import net.minecraft.util.math.BlockPos;

public class BypassUtil implements MCHook {
    public static boolean IsMatrixOnGround() {
        if (mc.player.verticalCollision) {
            return true;
        }

        for (int i = 0; i < 10; ++i) {
            if ((mc.world
                    .getBlockState(new BlockPos((int) mc.player.getX(),
                            (int) (mc.player.getY() - i * 0.1), (int) mc.player.getZ()))
                    .getBlock()
                    instanceof FlowerBlock)
                    || (mc.world
                    .getBlockState(new BlockPos((int) mc.player.getX(),
                            (int) (mc.player.getY() - i * 0.1), (int) mc.player.getZ()))
                    .getBlock()
                    .getName()
                    .getString()
                    .toLowerCase()
                    .contains("grass")
                    && !mc.world
                    .getBlockState(new BlockPos((int) mc.player.getX(),
                            (int) (mc.player.getY() - i * 0.1),
                            (int) mc.player.getZ()))
                    .getBlock()
                    .getName()
                    .getString()
                    .toLowerCase()
                    .contains("grass block"))) {
                break;
            }

            if (!mc.world
                    .getBlockState(new BlockPos(
                            (int) (mc.player.getX() + mc.player.getVelocity().x),
                            (int) (mc.player.getY() - i * 0.1),
                            (int) (mc.player.getZ() + mc.player.getVelocity().z)))
                    .isAir()) {
                return true;
            }
        }
        return false;
    }

    public static boolean IsMatrixOnGround2() {
        if (mc.player.verticalCollision) {
            return true;
        }

        for (int i = 0; i < 10; ++i) {
            if ((mc.world
                    .getBlockState(new BlockPos((int) mc.player.getX(),
                            (int) (mc.player.getY() - i * 0.1), (int) mc.player.getZ()))
                    .getBlock()
                    instanceof FlowerBlock)
                    || (mc.world
                    .getBlockState(new BlockPos((int) mc.player.getX(),
                            (int) (mc.player.getY() - i * 0.1), (int) mc.player.getZ()))
                    .getBlock()
                    .getName()
                    .getString()
                    .toLowerCase()
                    .contains("grass")
                    && !mc.world
                    .getBlockState(new BlockPos((int) mc.player.getX(),
                            (int) (mc.player.getY() - i * 0.1),
                            (int) mc.player.getZ()))
                    .getBlock()
                    .getName()
                    .getString()
                    .toLowerCase()
                    .contains("grass block"))) {
                return false;
            }
        }

        if (!mc.world
                .getBlockState(new BlockPos((int) mc.player.getX(),
                        (int) (mc.player.getY() - 0.999), (int) mc.player.getZ()))
                .isAir()) {
            return true;
        }

        return false;
    }
}
