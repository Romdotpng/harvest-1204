package tech.harvest.core.features.module.misc;

import java.util.Arrays;
import tech.harvest.core.features.event.PreUpdateEvent;
import tech.harvest.core.features.setting.BooleanSetting;
import tech.harvest.core.features.setting.DoubleSetting;
import tech.harvest.core.types.module.Module;
import tech.harvest.core.types.module.ModuleCategory;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;

public class Fucker extends Module {
    private final BooleanSetting tp = BooleanSetting.build().name("TP").value(true).end();
    private final BooleanSetting suiren = BooleanSetting.build().name("Suiren no ha").value(true).end();
    private final DoubleSetting range = DoubleSetting.build().name("Range").value(5.0d).range(1.0d, 8.0d).end();

    public Fucker() {
        super("Fucker", "", ModuleCategory.Misc);
        getSettings().addAll(Arrays.asList(this.tp, this.suiren, this.range));
    }

    @Override
    public void onPreUpdate(PreUpdateEvent event) {
        if (mc.player.age % 10 == 0) {
            int range = (int) this.range.getValue();
            int x = -range;
            loop0: while (true) {
                if (x > range) {
                    break;
                }
                for (int y = -range; y <= range; y++) {
                    for (int z = -range; z <= range; z++) {
                        Block block = mc.world.getBlockState(mc.player.getBlockPos().add(x, y, z)).getBlock();
                        if (this.tp.getValue() && block == Blocks.NETHER_QUARTZ_ORE) {
                            mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(mc.player.getBlockPos().toCenterPos(), Direction.UP, mc.player.getBlockPos().add(x, y, z), false));
                            break loop0;
                        }
                        boolean shouldBreak = this.suiren.getValue() && block == Blocks.LILY_PAD;
                        if (shouldBreak) {
                            mc.interactionManager.updateBlockBreakingProgress(mc.player.getBlockPos().add(x, y, z), Direction.UP);
                            mc.player.swingHand(Hand.MAIN_HAND);
                            break loop0;
                        }
                    }
                }
                x++;
            }
            super.onPreUpdate(event);
        }
    }
}
