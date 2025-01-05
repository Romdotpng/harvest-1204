package tech.harvest.core.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

public class BlockData {
    private final BlockPos pos;
    private final Direction direction;

    public BlockPos getPos() {
        return pos;
    }

    public Direction getDirection() {
        return direction;
    }

    public Box toBox() {
        final BlockPos pos = this.pos;
        return switch (this.direction) {
            case DOWN -> new Box(pos.toCenterPos(), new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ() + 1).toCenterPos());
            case NORTH ->
                    new Box(new BlockPos(pos.getX(), pos.getY(), pos.getZ()).toCenterPos(), new BlockPos(pos.getX() + 1, pos.getY() + 1, pos.getZ()).toCenterPos());
            case EAST ->
                    new Box(new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ()).toCenterPos(), new BlockPos(pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).toCenterPos());
            case SOUTH ->
                    new Box(new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1).toCenterPos(), new BlockPos(pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).toCenterPos());
            case UP ->
                    new Box(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ()).toCenterPos(), new BlockPos(pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).toCenterPos());
            case WEST ->
                    new Box(new BlockPos(pos.getX(), pos.getY(), pos.getZ()).toCenterPos(), new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ() + 1).toCenterPos());
        };
    }

    public BlockData(BlockPos pos, Direction direction) {
        this.pos = pos;
        this.direction = direction;
    }
}
