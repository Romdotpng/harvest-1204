package tech.harvest.core.features.module.player;

import java.awt.Color;
import java.util.Arrays;
import tech.harvest.core.features.event.ClickTickEvent;
import tech.harvest.core.features.event.PreRender3DEvent;
import tech.harvest.core.features.event.PreUpdateEvent;
import tech.harvest.core.features.event.RotationEvent;
import tech.harvest.core.features.setting.DoubleSetting;
import tech.harvest.core.types.module.Module;
import tech.harvest.core.types.module.ModuleCategory;
import me.x150.renderer.render.Renderer3d;
import net.minecraft.block.Block;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class AutoBreaker extends Module {
    private final DoubleSetting range = DoubleSetting.build().name("Range").value(6.0d).range(1.0d, 6.0d).end();
    private final DoubleSetting power = DoubleSetting.build().name("Power").value(1.0d).range(1.0d, 20.0d).increment(1.0d).end();
    private BlockPos blockPos;
    private Block currentBlock;

    public AutoBreaker() {
        super("AutoBreaker", "Automatically search blocks and break", ModuleCategory.Player);
        getSettings().addAll(Arrays.asList(this.range, this.power));
    }

    public static float[] getAngleToBlockPos(BlockPos pos) {
        float[] angle = calcAngle(mc.player.getEyePos(), new Vec3d(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f));
        return angle;
    }

    private static float[] calcAngle(Vec3d from, Vec3d to) {
        double difX = to.x - from.x;
        double difY = (to.y - from.y) * (-1.0d);
        double difZ = to.z - from.z;
        double dist = Math.sqrt((float) ((difX * difX) + (difZ * difZ)));
        return new float[]{(float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0d), (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difY, dist)))};
    }

    @Override
    public void onClickTick(ClickTickEvent event) {
        super.onClickTick(event);
    }

    @Override
    public void onPreRender3D(PreRender3DEvent event) {
        if (this.blockPos != null) {
            Renderer3d.renderFilled(event.getMatrices(), Color.RED, new Vec3d(this.blockPos.getX(), this.blockPos.getY(), this.blockPos.getZ()), new Vec3d(this.blockPos.getX() + 1, this.blockPos.getY() + 1, this.blockPos.getZ() + 1));
            super.onPreRender3D(event);
        }
    }

    @Override
    public void onPreUpdate(PreUpdateEvent event) {
        if (mc.interactionManager.isBreakingBlock() && mc.crosshairTarget != null) {
            BlockHitResult blockHitResult = (BlockHitResult) mc.crosshairTarget;
            if (blockHitResult instanceof BlockHitResult) {
                this.currentBlock = mc.world.getBlockState(blockHitResult.getBlockPos()).getBlock();
            }
        }
        if (this.currentBlock != null) {
            if (this.blockPos == null) {
                this.blockPos = getBlock((int) this.range.getValue(), this.currentBlock);
            }
            if (this.blockPos != null) {
                float f = (float) (mc.player.getX() - this.blockPos.getX());
                float g = (float) (mc.player.getY() - this.blockPos.getY());
                float h = (float) (mc.player.getZ() - this.blockPos.getZ());
                float dist = MathHelper.sqrt((f * f) + (g * g) + (h * h));
                if (dist >= this.range.getValue() || mc.world.getBlockState(this.blockPos).getBlock() != this.currentBlock) {
                    this.blockPos = null;
                    return;
                } else if (this.blockPos != null) {
                    for (int i = 0; i < this.power.getValue(); i++) {
                        mc.interactionManager.updateBlockBreakingProgress(this.blockPos, Direction.UP);
                        mc.player.swingHand(Hand.MAIN_HAND);
                    }
                }
            } else {
                return;
            }
        }
        super.onPreUpdate(event);
    }

    @Override
    public void onRotation(RotationEvent event) {
        super.onRotation(event);
    }

    private BlockPos getBlock(int range, Block block) {
        BlockPos pos = null;
        BlockPos playerPos = mc.player.getBlockPos();
        double currentDist = Double.MAX_VALUE;
        for (int x = -range; x < range; ++x) {
            for (int y = -range; y < range; ++y) {
                for (int z = -range; z < range; ++z) {
                    BlockPos current = new BlockPos((int)(mc.player.getX() + (double)x), (int)(AutoBreaker.mc.player.getY() + (double)y), (int)(mc.player.getZ() + (double)z));
                    double dist = playerPos.getSquaredDistance(current);
                    if (mc.world.getBlockState(current).getBlock() != block || !(currentDist > dist)) continue;
                    currentDist = dist;
                    pos = current;
                }
            }
        }
        return pos;
    }

    @Override
    public void onEnable() {
        mc.inGameHud.getChatHud().addMessage(Text.literal("壊したいブロックをクリックしてね(鉄鉱石とか)"));
        super.onEnable();
    }

    @Override
    public void onDisable() {
        this.blockPos = null;
        super.onDisable();
    }
}
