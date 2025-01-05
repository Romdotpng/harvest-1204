package tech.harvest.core.features.module.misc;

import tech.harvest.core.features.event.SendPacketEvent;
import tech.harvest.core.features.setting.ModeSetting;
import tech.harvest.core.types.module.Module;
import tech.harvest.core.types.module.ModuleCategory;
import net.minecraft.network.packet.c2s.common.CommonPongC2SPacket;

public class Disabler extends Module {
    private final ModeSetting mode = ModeSetting.build().name("Mode").option("Verus combat").end();

    public Disabler() {
        super("Disabler", "", ModuleCategory.Misc);
        getSettings().add(this.mode);
    }

    @Override
    public void onSendPacket(SendPacketEvent event) {
        if (this.mode.getValue().equals("Verus combat") && (event.getPacket() instanceof CommonPongC2SPacket)) {
            event.cancel();
        }
        super.onSendPacket(event);
    }
}
