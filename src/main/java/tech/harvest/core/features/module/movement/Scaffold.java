package tech.harvest.core.features.module.movement;

import net.minecraft.item.Item;
import tech.harvest.core.features.event.ClickTickEvent;
import tech.harvest.core.features.event.InputEvent;
import tech.harvest.core.features.event.MoveEvent;
import tech.harvest.core.features.event.PreUpdateEvent;
import tech.harvest.core.features.event.RotationEvent;
import tech.harvest.core.types.module.Module;
import tech.harvest.core.types.module.ModuleCategory;
import tech.harvest.core.util.BlockData;
import tech.harvest.core.util.MoveUtil;
import tech.harvest.core.util.RayCastUtil;
import tech.harvest.core.util.RotationUtil;
import tech.harvest.mixin.client.MinecraftClientAccessor;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

//普通にゴミだからやめて
public class Scaffold extends Module {
    private static final Direction[] invert = {Direction.UP, Direction.DOWN, Direction.SOUTH, Direction.NORTH, Direction.EAST, Direction.WEST};
    public static BlockData data;
    private int y;
    public static boolean isToggled;

    public Scaffold() {
        super("Scaffold", "Automatically place blocks under you", ModuleCategory.Movement);
    }

    @Override
    public void onEnable() {
        isToggled = true;
        this.y = ((int) mc.player.getY()) - 1;
        data = null;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        isToggled = false;
        super.onDisable();
    }

    @Override
    public void onClickTick(ClickTickEvent event) {
        if (data != null) {
            if (isGood(mc.crosshairTarget, data, false)) {
                ((MinecraftClientAccessor)mc).accessDoUseItem();
            }
            super.onClickTick(event);
        }
    }

    @Override
    public void onRotation(RotationEvent event) {
        handleItemSpoofTick();
        updateBlockData();
        float[] rotation = calcRotation();
        event.setYaw(rotation[0]);
        event.setPitch(rotation[1]);
        super.onRotation(event);
    }

    private void handleItemSpoofTick() {
        for (int i = 0; i < 9; i++) {
            Item item = mc.player.getInventory().getStack(i).getItem();
            if (item instanceof BlockItem) {
                if (((BlockItem) item).getBlock() != Blocks.AIR) {
                    mc.player.getInventory().selectedSlot = i;
                    return;
                }
            }
        }
    }

    @Override
    public void onInput(InputEvent event) {
        event.moveFix = true;
        super.onInput(event);
    }

    @Override
    public void onPreUpdate(PreUpdateEvent event) {
        super.onPreUpdate(event);
    }

    @Override
    public void onMove(MoveEvent event) {
        double speed = Math.sqrt((event.x * event.x) + (event.z * event.z));
        if (speed > 0.24d) {
            double scale = 0.24d / speed;
            event.x *= scale;
            event.z *= scale;
        }
        if (!MoveUtil.isMoving() && mc.options.jumpKey.isPressed() && mc.player.getVelocity().horizontalLength() == 0.0d) {
            if (event.y > 0.0d && event.y < 0.17d) {
                event.y = -0.2d;
            }
            if (mc.player.getY() == Math.round(mc.player.getY())) {
                event.y = 0.42d;
            }
            mc.player.setVelocity(mc.player.getVelocity().x, event.y, mc.player.getVelocity().z);
        }
        super.onMove(event);
    }

    private void updateBlockData() {
        int currentDist;
        data = null;
        if (mc.options.jumpKey.isPressed()) {
            this.y = ((int) mc.player.getY()) - 1;
        }
        BlockPos blockPos = new BlockPos((int) mc.player.getX(), this.y, (int) mc.player.getZ());
        if (mc.world.getBlockState(blockPos).getBlock() == Blocks.AIR) {
            int dist = Integer.MAX_VALUE;
            for (int x = -4; x <= 4; x++) {
                for (int y = -4; y <= 4; y++) {
                    for (int z = -4; z <= 4; z++) {
                        BlockPos offsetPos = blockPos.add(x, y, z);
                        for (Direction facing : Direction.values()) {
                            if (mc.world.getBlockState(offsetPos.offset(facing)).getBlock() != Blocks.AIR && (currentDist = Math.abs(x) + Math.abs(y) + Math.abs(z)) < dist) {
                                dist = currentDist;
                                data = new BlockData(offsetPos.offset(facing), invert[facing.ordinal()]);
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isGood(HitResult result, BlockData data2, boolean placeCheck) {
        if (result == null || data2 == null || !(result instanceof BlockHitResult blockResult)) {
            return false;
        }
        if (placeCheck) {
            return blockResult.getBlockPos().offset(blockResult.getSide()).toString().equals(data2.getPos().offset(data2.getDirection()).toString());
        }
        return true;
    }

    private float[] calcRotation() {
        if (data == null) {
            return new float[]{mc.player.getYaw(), 82.5f};
        }
        float stdYaw = RotationUtil.virtualYaw + 180.0f;
        float stdPitch = mc.player.getPitch(mc.getTickDelta());
        float[] yaws = {stdYaw - 45.0f, stdYaw + 45.0f, RotationUtil.rotation(data.getPos().toCenterPos(), mc.player.getPos())[0], stdYaw};
        for (float yaw : yaws) {
            for (float pitch = 90.0f; pitch > 80.0f; pitch -= 0.01f) {
                float[] current = {yaw, pitch};
                if (isGood(RayCastUtil.rayCast(current, 3.0d, 1.0f), data, true)) {
                    stdPitch = pitch;
                    stdYaw = yaw;
                }
            }
        }
        return new float[]{stdYaw, stdPitch};
    }
}
