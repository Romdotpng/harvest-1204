package tech.harvest.core.features.module.misc;

import java.util.Collections;
import java.util.Iterator;
import tech.harvest.core.features.event.GetPacketEvent;
import tech.harvest.core.features.event.PreUpdateEvent;
import tech.harvest.core.features.setting.BooleanSetting;
import tech.harvest.core.types.module.Module;
import tech.harvest.core.types.module.ModuleCategory;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.ScoreboardScoreUpdateS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

public class AutoSuccubus extends Module {
    private final BooleanSetting notInMaxHealth = BooleanSetting.build().name("Not In Max Health").end();
    private int slot = -1;

    public AutoSuccubus() {
        super("AutoSuccubus", "Automatically search blocks and break", ModuleCategory.Player);
        getSettings().addAll(Collections.singletonList(this.notInMaxHealth));
    }

    @Override
    public void onPreUpdate(PreUpdateEvent event) {
        int i = 0;
        while (true) {
            if (i >= 10) {
                break;
            }
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == Items.RED_DYE && stack.getName().toString().contains("Life Drain")) {
                boolean flag = false;
                Iterator<Text> it = stack.getName().getSiblings().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    Text t = it.next();
                    if (t.getString().equals("READY")) {
                        flag = true;
                        break;
                    }
                }
                if (flag) {
                    this.slot = i;
                    break;
                }
            }
            i++;
        }
        super.onPreUpdate(event);
    }

    @Override
    public void onGetPacket(GetPacketEvent event) {
        if (mc.world != null) {
            if ((!this.notInMaxHealth.getValue() || mc.player.getHealth() != mc.player.getMaxHealth()) && this.slot != -1) {
                ScoreboardScoreUpdateS2CPacket packet = (ScoreboardScoreUpdateS2CPacket) event.getPacket();
                if (packet instanceof ScoreboardScoreUpdateS2CPacket) {
                    if (packet.score() != 0 && packet.score() < 6) {
                        Entity entity = null;
                        Iterator<AbstractClientPlayerEntity> it = mc.world.getPlayers().iterator();
                        while (true) {
                            if (!it.hasNext()) {
                                break;
                            }
                            Entity entity2 = it.next();
                            if (packet.scoreHolderName().contains(entity2.getName().getString())) {
                                entity = entity2;
                                break;
                            }
                        }
                        if (entity != null) {
                            int oldSlot = mc.player.getInventory().selectedSlot;
                            mc.player.getInventory().selectedSlot = this.slot;
                            mc.interactionManager.interactEntity(mc.player, entity, Hand.OFF_HAND);
                            mc.player.getInventory().selectedSlot = oldSlot;
                        } else {
                            return;
                        }
                    }
                }
                super.onGetPacket(event);
            }
        }
    }
}
