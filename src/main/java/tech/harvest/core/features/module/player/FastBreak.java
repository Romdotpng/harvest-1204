package tech.harvest.core.features.module.player;

import net.minecraft.util.hit.HitResult;
import tech.harvest.core.features.event.PreUpdateEvent;
import tech.harvest.core.features.setting.DoubleSetting;
import tech.harvest.core.types.module.Module;
import tech.harvest.core.types.module.ModuleCategory;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.hit.BlockHitResult;

public class FastBreak extends Module {
    private final DoubleSetting speed = DoubleSetting.build().name("Speed").value(8.0d).range(0.0d, 10.0d).end();

    public FastBreak() {
        super("FastBreak", "", ModuleCategory.Player);
    }

    @Override
    public void onPreUpdate(PreUpdateEvent event) {
        HitResult hitResult;
        if (mc.crosshairTarget == null || !((hitResult = mc.crosshairTarget) instanceof BlockHitResult)) {
            return;
        }
        BlockHitResult block = (BlockHitResult)hitResult;
        if (mc.world.getBlockState(block.getBlockPos()).isAir() || mc.world.getBlockState(block.getBlockPos()).getBlock() == Blocks.END_STONE) {
            return;
        }
        if (mc.interactionManager.getBlockBreakingProgress() > (int)this.speed.getValue()) {
            mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, block.getBlockPos(), block.getSide()));
            mc.world.setBlockState(block.getBlockPos(), Blocks.AIR.getDefaultState());
        }
        super.onPreUpdate(event);
    }
}
