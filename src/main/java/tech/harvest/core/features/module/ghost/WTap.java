package tech.harvest.core.features.module.ghost;

import java.util.Collections;
import tech.harvest.core.features.event.AttackEvent;
import tech.harvest.core.features.event.PreUpdateEvent;
import tech.harvest.core.features.event.SendPacketEvent;
import tech.harvest.core.features.setting.BooleanSetting;
import tech.harvest.core.types.module.Module;
import tech.harvest.core.types.module.ModuleCategory;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.text.Text;

public class WTap extends Module {
    private final BooleanSetting debugPrint = BooleanSetting.build().name("Debug Print").value(false).end();
    private boolean tapping;
    private int tick;

    public WTap() {
        super("WTap", "WTap", ModuleCategory.Ghost);
        getSettings().addAll(Collections.singletonList(this.debugPrint));
    }

    @Override
    public void onPreUpdate(PreUpdateEvent event) {
        if (this.tapping) {
            if (this.tick == 2) {
                mc.player.setSprinting(true);
                this.tapping = false;
            }
            this.tick++;
        }
        super.onPreUpdate(event);
    }

    @Override
    public void onAttack(AttackEvent event) {
        if (mc.player.isSprinting() && !this.tapping) {
            mc.player.setSprinting(false);
            this.tapping = true;
            this.tick = 0;
        }
        super.onAttack(event);
    }

    @Override
    public void onSendPacket(SendPacketEvent event) {
        if (this.debugPrint.getValue()) {
            ClientCommandC2SPacket packet = (ClientCommandC2SPacket) event.getPacket();
            if (packet instanceof ClientCommandC2SPacket) {
                if (packet.getMode() == ClientCommandC2SPacket.Mode.START_SPRINTING) {
                    mc.inGameHud.getChatHud().addMessage(Text.literal("Start sprinting"));
                } else if (packet.getMode() == ClientCommandC2SPacket.Mode.STOP_SPRINTING) {
                    mc.inGameHud.getChatHud().addMessage(Text.literal("Stop sprinting"));
                }
            }
            super.onSendPacket(event);
        }
    }
}
