package tech.harvest.core.features.module.render;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import tech.harvest.core.features.event.GetPacketEvent;
import tech.harvest.core.features.event.PostRender3DEvent;
import tech.harvest.core.types.module.Module;
import tech.harvest.core.types.module.ModuleCategory;
import me.x150.renderer.render.Renderer3d;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.WorldChunk;

public class ChestESP extends Module {
    private static final Vec3d box = new Vec3d(1.0d, 1.0d, 1.0d);
    private List<BlockEntity> blocks = new ArrayList<>();

    public ChestESP() {
        super("ChestESP", "", ModuleCategory.Render);
    }

    public static List<BlockEntity> getBlockEntities() {
        List<BlockEntity> list = new ArrayList<>();
        for (WorldChunk chunk : getLoadedChunks()) {
            list.addAll(chunk.getBlockEntities().values());
        }
        return list;
    }

    public static List<WorldChunk> getLoadedChunks() {
        List<WorldChunk> chunks = new ArrayList<>();
        int viewDist = mc.options.getViewDistance().getValue();
        for (int x = -viewDist; x <= viewDist; x++) {
            for (int z = -viewDist; z <= viewDist; z++) {
                WorldChunk chunk = mc.world.getChunkManager().getWorldChunk((((int) mc.player.getX()) / 16) + x, (((int) mc.player.getZ()) / 16) + z);
                if (chunk != null) {
                    chunks.add(chunk);
                }
            }
        }
        return chunks;
    }

    @Override
    public void onGetPacket(GetPacketEvent event) {
        if (mc.player != null) {
            if (mc.player.age % 100 == 0) {
                this.blocks = getBlockEntities();
            }
            super.onGetPacket(event);
        }
    }

    @Override
    public void onPostRender3D(PostRender3DEvent event) {
        if (!(mc.world == null || mc.player == null)) {
            for (BlockEntity blockEntity : this.blocks) {
                Vec3d a = new Vec3d(blockEntity.getPos().getX(), blockEntity.getPos().getY(), blockEntity.getPos().getZ());
                if (blockEntity instanceof ChestBlockEntity) {
                    Renderer3d.renderFilled(event.getMatrices(), Color.YELLOW, a, box);
                } else if (blockEntity instanceof FurnaceBlockEntity) {
                    Renderer3d.renderFilled(event.getMatrices(), Color.darkGray, a, box);
                } else if (blockEntity instanceof HopperBlockEntity) {
                    Renderer3d.renderFilled(event.getMatrices(), Color.darkGray, a, box);
                }
            }
        }
    }
}
