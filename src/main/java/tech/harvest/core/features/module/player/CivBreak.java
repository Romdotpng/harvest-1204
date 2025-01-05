package tech.harvest.core.features.module.player;

import me.x150.renderer.render.Renderer3d;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import tech.harvest.core.features.event.PreRender3DEvent;
import tech.harvest.core.features.event.PreUpdateEvent;
import tech.harvest.core.features.event.RotationEvent;
import tech.harvest.core.features.event.SendPacketEvent;
import tech.harvest.core.features.setting.DoubleSetting;
import tech.harvest.core.features.setting.ModeSetting;
import tech.harvest.core.types.module.Module;
import tech.harvest.core.types.module.ModuleCategory;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CivBreak extends Module {
    private final DoubleSetting range = DoubleSetting.build().name("Range").value(6.0).range(1.0, 6.0).end();
    private final DoubleSetting power = DoubleSetting.build().name("Power").value(1.0).range(1.0, 20.0).increment(1.0).end();
    private final ModeSetting mode = ModeSetting.build().name("Mode").option("Packet", "Legit").onSetting(v -> prefix = v).end();
    private final List<Packet<?>> packets = new ArrayList<>();
    private BlockPos blockPos;
    private boolean recoding = false;
    private ItemStack stack = null;

    public CivBreak() {
        super("CivBreak", "Automatically search blocks and break", ModuleCategory.Player);
        getSettings().addAll(Arrays.asList(range, power));
    }

    public static float[] getAngleToBlockPos(BlockPos pos) {
        float[] angle = calcAngle(mc.player.getEyePos(), new Vec3d((float) pos.getX() + 0.5f, (float) pos.getY() + 0.5f, (float) pos.getZ() + 0.5f));
        return angle;
    }

    private static float[] calcAngle(Vec3d from, Vec3d to) {
        double difX = to.x - from.x;
        double difY = (to.y - from.y) * -1.0;
        double difZ = to.z - from.z;
        double dist = Math.sqrt((float) (difX * difX + difZ * difZ));
        return new float[]{(float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0), (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difY, dist)))};
    }

    @Override
    public void onPreRender3D(PreRender3DEvent event) {
        if (blockPos == null) {
            return;
        }
        Renderer3d.renderFilled(event.getMatrices(), Color.RED, new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()), new Vec3d(blockPos.getX() + 1, blockPos.getY() + 1, blockPos.getZ() + 1));
        super.onPreRender3D(event);
    }

    @Override
    public void onPreUpdate(PreUpdateEvent event) {
        float h;
        float g;
        if (blockPos == null) {
            blockPos = getBlock((int) range.getValue(), Blocks.END_STONE);
        }
        if (blockPos == null) {
            return;
        }
        float f = (float) (mc.player.getX() - (double) blockPos.getX());
        float dist = MathHelper.sqrt(f * f + (g = (float) (mc.player.getY() - (double) blockPos.getY())) * g + (h = (float) (mc.player.getZ() - (double) blockPos.getZ())) * h);
        if ((double) dist >= range.getValue()) {
            blockPos = null;
            return;
        }
        if (blockPos != null) {
            if (stack != mc.player.getMainHandStack()) {
                stack = mc.player.getMainHandStack();
                recoding = true;
                packets.clear();
            }
            if (recoding) {
                int i = 0;
                while ((double) i < power.getValue()) {
                    mc.interactionManager.updateBlockBreakingProgress(blockPos, Direction.UP);
                    mc.player.swingHand(Hand.MAIN_HAND);
                    ++i;
                }
            } else if (mc.player.age % 2 == 0) {
                packets.forEach(arg_0 -> mc.getNetworkHandler().sendPacket(arg_0));
            }
        }
        super.onPreUpdate(event);
    }

    @Override
    public void onSendPacket(SendPacketEvent event) {
        if (!recoding) {
            return;
        }
        Packet<?> packet = event.getPacket();
        if (packet instanceof PlayerActionC2SPacket packet2) {
            packets.add(event.getPacket());
            if (packet2.getAction() == PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK) {
                recoding = false;
            }
        }
        super.onSendPacket(event);
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
                    BlockPos current = new BlockPos((int) (mc.player.getX() + (double) x), (int) (mc.player.getY() + (double) y), (int) (mc.player.getZ() + (double) z));
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
        mc.inGameHud.getChatHud().addMessage(Text.literal("速いツルハシを使ってね"));
        recoding = true;
        stack = null;
        packets.clear();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        blockPos = null;
        super.onDisable();
    }
}