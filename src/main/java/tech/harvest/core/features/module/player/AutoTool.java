package tech.harvest.core.features.module.player;

import java.util.Objects;

import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import tech.harvest.core.features.event.GetPacketEvent;
import tech.harvest.core.features.event.PreUpdateEvent;
import tech.harvest.core.features.setting.BooleanSetting;
import tech.harvest.core.types.module.Module;
import tech.harvest.core.types.module.ModuleCategory;
import tech.harvest.mixin.entity.IClientPlayerInteractionManagerMixin;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;

public class AutoTool extends Module {
    private final BooleanSetting swapWeapon = BooleanSetting.build().name("Swap Weapon").value(false).end();

    public AutoTool() {
        super("AutoTool", "", ModuleCategory.Player);
        this.getSettings().add(swapWeapon);
    }

    private static void pick(BlockState state) {
        int index = -1;
        int optAirIndex = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = Objects.requireNonNull(mc.player).getInventory().getStack(i);
            if (stack.getItem() == Items.AIR) {
                optAirIndex = i;
            }
            float s = stack.getMiningSpeedMultiplier(state);
            if (s > 1.0f) {
                index = i;
            }
        }
        if (index != -1) {
            mc.player.getInventory().selectedSlot = index;
        } else if (optAirIndex != -1) {
            mc.player.getInventory().selectedSlot = optAirIndex;
        }
    }

    @Override
    public void onPreUpdate(PreUpdateEvent event) {
        if (Objects.requireNonNull(mc.interactionManager).isBreakingBlock()) {
            BlockPos breaking = ((IClientPlayerInteractionManagerMixin)mc.interactionManager).getCurrentBreakingPos();
            BlockState bs = Objects.requireNonNull(mc.world).getBlockState(breaking);
            pick(bs);
        }
        super.onPreUpdate(event);
    }

    @Override
    public void onGetPacket(GetPacketEvent event) {
        if (event.getPacket() instanceof PlayerInteractEntityC2SPacket) {
            if (swapWeapon.getValue()) {
                float index = 0.0f;
                for (int i = 0; i < 9; i++) {
                    ItemStack itemStack = mc.player.getInventory().getStack(i);
                    if (itemStack == null) {
                        continue;
                    }
                    if (itemStack.getItem() instanceof SwordItem swordItem) {
                        float a = swordItem.getMaterial().getAttackDamage();
                        if (a >= index) {
                            index = a;
                            mc.player.getInventory().selectedSlot = i;
                        }
                    }
                }
            }
        }
        super.onGetPacket(event);
    }
}
