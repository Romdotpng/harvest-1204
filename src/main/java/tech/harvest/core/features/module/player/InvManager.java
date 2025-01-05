package tech.harvest.core.features.module.player;

import java.util.Arrays;
import tech.harvest.core.features.event.PreUpdateEvent;
import tech.harvest.core.features.setting.BooleanSetting;
import tech.harvest.core.features.setting.DoubleSetting;
import tech.harvest.core.types.module.Module;
import tech.harvest.core.types.module.ModuleCategory;
import tech.harvest.core.util.TimerUtil;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShearsItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;
import net.minecraft.screen.slot.SlotActionType;
import org.apache.commons.lang3.tuple.MutablePair;


public class InvManager
        extends Module {
    private static final Item[] GOOD_ITEMS = new Item[]{Items.WATER_BUCKET, Items.LAVA_BUCKET, Items.BUCKET, Items.ENDER_PEARL, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE};
    private final BooleanSetting openInv = BooleanSetting.build().name("Open Inventory").value(true).end();
    private final DoubleSetting delay = DoubleSetting.build().name("Delay").value(250.0).range(0.0, 1000.0).end();
    private final BooleanSetting equipArmor = BooleanSetting.build().name("Equip Armor").value(true).end();
    private final BooleanSetting dropArmor = BooleanSetting.build().name("Drop Armor").value(true).end();
    private final BooleanSetting dropSword = BooleanSetting.build().name("Drop Swap").value(true).end();
    private final BooleanSetting dropTools = BooleanSetting.build().name("Drop Tools").value(true).end();
    private final BooleanSetting dropTrash = BooleanSetting.build().name("Drop Trash").value(true).end();
    private final TimerUtil interactionTimer = new TimerUtil();

    public InvManager() {
        super("InvManager", "", ModuleCategory.Player);
        this.getSettings().addAll(Arrays.asList(this.openInv, this.delay, this.equipArmor, this.dropSword, this.dropArmor, this.dropTools, this.dropTrash));
    }

    private static float getSwordValue(ItemStack stack, SwordItem item) {
        float value = item.getAttackDamage() * 1000.0f;
        value += EnchantmentHelper.getLevel(Enchantments.SHARPNESS, stack) * 1.0f;
        value += EnchantmentHelper.getLevel(Enchantments.FIRE_ASPECT, stack) * 1000.0f;
        return value += EnchantmentHelper.getLevel(Enchantments.KNOCKBACK, stack) * 1.0f;
    }

    private static float getPickaxeValue(ItemStack stack, PickaxeItem item) {
        float value = item.getMaterial().getMiningSpeedMultiplier() * 1000.0f;
        value += EnchantmentHelper.getLevel(Enchantments.EFFICIENCY, stack) * 1.0f;
        value += EnchantmentHelper.getLevel(Enchantments.UNBREAKING, stack) * 1.0f;
        return value += EnchantmentHelper.getLevel(Enchantments.FORTUNE, stack) * 1000.0f;
    }

    private static float getAxeValue(ItemStack stack, AxeItem item) {
        float value = item.getMaterial().getMiningSpeedMultiplier() * 1000.0f;
        value += EnchantmentHelper.getLevel(Enchantments.EFFICIENCY, stack) * 1.0f;
        value += EnchantmentHelper.getLevel(Enchantments.UNBREAKING, stack) * 1.0f;
        return value += EnchantmentHelper.getLevel(Enchantments.FORTUNE, stack) * 1000.0f;
    }

    private static float getShovelValue(ItemStack stack, ShovelItem item) {
        float value = item.getMaterial().getMiningSpeedMultiplier() * 1000.0f;
        value += EnchantmentHelper.getLevel(Enchantments.EFFICIENCY, stack) * 1.0f;
        value += EnchantmentHelper.getLevel(Enchantments.UNBREAKING, stack) * 1.0f;
        return value += EnchantmentHelper.getLevel(Enchantments.FORTUNE, stack) * 1000.0f;
    }

    private static float getShearsValue(ItemStack stack, ShearsItem item) {
        return 1.0f - (float)stack.getDamage() / (float)stack.getMaxDamage();
    }

    private static float getBowValue(ItemStack stack, BowItem item) {
        float value = 0.0f;
        value += EnchantmentHelper.getLevel(Enchantments.POWER, stack);
        value += EnchantmentHelper.getLevel(Enchantments.PUNCH, stack);
        return value += EnchantmentHelper.getLevel(Enchantments.FLAME, stack) * 1000;
    }

    private static float getFoodValue(ItemStack stack, FoodComponent item) {
        return stack.getCount() * item.getHunger();
    }

    private static float getArmorValue(ItemStack stack, ArmorItem item) {
        float value = item.getProtection() * 1000;
        value += EnchantmentHelper.getLevel(Enchantments.PROTECTION, stack);
        value += EnchantmentHelper.getLevel(Enchantments.UNBREAKING, stack);
        return value += EnchantmentHelper.getLevel(Enchantments.BLAST_PROTECTION, stack);
    }

    private static int getIndexOfArmorType(ArmorItem.Type type) {
        return switch (type) {
            case BOOTS -> 0;
            case LEGGINGS -> 1;
            case CHESTPLATE -> 2;
            case HELMET -> 3;
        };
    }

    @Override
    public void onPreUpdate(PreUpdateEvent event) {
        Item item;
        ItemStack stack;
        int i;
        if (this.openInv.getValue() && InvManager.mc.currentScreen == null) {
            return;
        }
        if (InvManager.mc.currentScreen != null && !(InvManager.mc.currentScreen instanceof InventoryScreen)) {
            return;
        }
        MutablePair[] bestTools = new MutablePair[]{new MutablePair<>(-1, 0.0f), new MutablePair<>(-1, 0.0f), new MutablePair<>(-1, 0.0f), new MutablePair<Integer, Float>(-1, Float.valueOf(0.0f)), new MutablePair<Integer, Float>(-1, Float.valueOf(0.0f)), new MutablePair<Integer, Float>(-1, Float.valueOf(0.0f)), new MutablePair<Integer, Float>(-1, Float.valueOf(0.0f)), new MutablePair<Integer, Float>(-1, Float.valueOf(0.0f)), new MutablePair<Integer, Float>(-1, Float.valueOf(0.0f)), new MutablePair<Integer, Float>(-1, Float.valueOf(0.0f)), new MutablePair<Integer, Float>(-1, Float.valueOf(0.0f))};
        for (i = 0; i < 40; ++i) {
            stack = InvManager.mc.player.getInventory().getStack(i);
            item = stack.getItem();
            if (stack.isEmpty()) continue;
            if (item instanceof ArmorItem armorItem) {
                float armorValue = InvManager.getArmorValue(stack, armorItem);
                int armorIndex = InvManager.getIndexOfArmorType(armorItem.getType());
                if (((Float)bestTools[armorIndex].right) < armorValue) {
                    bestTools[armorIndex].left = i;
                    bestTools[armorIndex].right = armorValue;
                }
            }
            if (item instanceof SwordItem swordItem) {
                float swordValue = InvManager.getSwordValue(stack, swordItem);
                if ((Float) bestTools[4].right < swordValue) {
                    bestTools[4].left = i;
                    bestTools[4].right = swordValue;
                }
            }
            if (item instanceof PickaxeItem pickaxeItem) {
                float pickaxeValue = InvManager.getPickaxeValue(stack, pickaxeItem);
                if (((Float)bestTools[5].right) < pickaxeValue) {
                    bestTools[5].left = i;
                    bestTools[5].right = pickaxeValue;
                }
            }
            if (item instanceof AxeItem axeItem) {
                float axeValue = InvManager.getAxeValue(stack, axeItem);
                if ((Float) bestTools[6].right < axeValue) {
                    bestTools[6].left = i;
                    bestTools[6].right = axeValue;
                }
            }
            if (item instanceof ShovelItem shovelItem) {
                float shovelValue = InvManager.getShovelValue(stack, shovelItem);
                if ((Float) bestTools[7].right < shovelValue) {
                    bestTools[7].left = i;
                    bestTools[7].right = shovelValue;
                }
            }
            if (item instanceof ShearsItem shearsItem) {
                float shearsValue = InvManager.getShearsValue(stack, shearsItem);
                if (((Float)bestTools[8].right) < shearsValue) {
                    bestTools[8].left = i;
                    bestTools[8].right = shearsValue;
                }
            }
            if (item instanceof BowItem bowItem) {
                float bowValue = InvManager.getBowValue(stack, bowItem);
                if ((Float) bestTools[9].right < bowValue) {
                    bestTools[9].left = i;
                    bestTools[9].right = bowValue;
                }
            }
            if (!item.isFood()) continue;
            FoodComponent foodComponent = item.getFoodComponent();
            float foodValue = InvManager.getFoodValue(stack, foodComponent);
            if (!(((Float)bestTools[10].right) < foodValue)) continue;
            bestTools[10].left = i;
            bestTools[10].right = foodValue;
        }
        if (this.equipArmor.getValue()) {
            for (i = 0; i < 4; ++i) {
                MutablePair bestArmor = bestTools[i];
                if ((Integer) bestArmor.left == -1 || (Integer)bestArmor.left >= 36) continue;
                if (!InvManager.mc.player.getInventory().armor.get(i).isEmpty()) {
                    this.drop(8 - i);
                    continue;
                }
                this.shiftClick((Integer)bestArmor.left < 9 ? 36 + (Integer)bestArmor.left : (Integer)bestArmor.left);
            }
        }
        for (i = 0; i < 40; ++i) {
            stack = InvManager.mc.player.getInventory().getStack(i);
            item = stack.getItem();
            if (stack.isEmpty()) continue;
            boolean flag = false;
            for (Item goodItem : GOOD_ITEMS) {
                if (item != goodItem) continue;
                flag = true;
                break;
            }
            if (flag) continue;
            if (item instanceof ArmorItem armorItem) {
                if (this.dropArmor.getValue() && (Integer)bestTools[InvManager.getIndexOfArmorType((ArmorItem.Type)armorItem.getType())].left != i) {
                    this.drop(i < 9 ? 36 + i : i);
                }
            }
            if (item instanceof SwordItem) {
                if (this.dropSword.getValue() && (Integer)bestTools[4].left != i) {
                    this.drop(i < 9 ? 36 + i : i);
                }
            }
            if (item instanceof PickaxeItem) {
                if (this.dropTools.getValue() && (Integer)bestTools[5].left != i) {
                    this.drop(i < 9 ? 36 + i : i);
                }
            }
            if (item instanceof AxeItem) {
                if (this.dropTools.getValue() && (Integer)bestTools[6].left != i) {
                    this.drop(i < 9 ? 36 + i : i);
                }
            }
            if (item instanceof ShovelItem) {
                if (this.dropTools.getValue() && (Integer)bestTools[7].left != i) {
                    this.drop(i < 9 ? 36 + i : i);
                }
            }
            if (item instanceof ShearsItem) {
                if (this.dropTools.getValue() && (Integer)bestTools[8].left != i) {
                    this.drop(i < 9 ? 36 + i : i);
                }
            }
            if (item instanceof BowItem) {
                if (this.dropTools.getValue() && (Integer)bestTools[9].left != i) {
                    this.drop(i < 9 ? 36 + i : i);
                }
            }
            if (!item.isFood() || !this.dropTrash.getValue() || (Integer)bestTools[10].left == i) continue;
            this.drop(i < 9 ? 36 + i : i);
        }
        super.onPreUpdate(event);
    }

    private void drop(int slot) {
        if (this.interactionTimer.hasTimeElapsed((long)this.delay.getValue())) {
            this.interactionTimer.reset();
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot, 1, SlotActionType.THROW, mc.player);
        }
    }

    private void shiftClick(int slot) {
        if (this.interactionTimer.hasTimeElapsed((long)this.delay.getValue())) {
            this.interactionTimer.reset();
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot, 0, SlotActionType.QUICK_MOVE, mc.player);
        }
    }
}
