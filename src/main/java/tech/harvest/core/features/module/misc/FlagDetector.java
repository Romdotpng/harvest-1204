package tech.harvest.core.features.module.misc;

import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.text.Text;
import tech.harvest.HarvestClient;
import tech.harvest.core.features.event.GetPacketEvent;
import tech.harvest.core.features.module.movement.DebugSpeed;
import tech.harvest.core.features.module.movement.Fly;
import tech.harvest.core.features.setting.BooleanSetting;
import tech.harvest.core.types.module.Module;
import tech.harvest.core.types.module.ModuleCategory;

import java.util.Arrays;


public class FlagDetector extends Module {
    private final BooleanSetting autoDisableFly = BooleanSetting.build().name("AutoDisable Fly").value(true).end();
    private final BooleanSetting autoDisableSpeed = BooleanSetting.build().name("AutoDisable Speed").value(true).end();

    public FlagDetector() {
        super("FlagDetector", "niga", ModuleCategory.Misc);
        getSettings().addAll(Arrays.asList(autoDisableFly, autoDisableSpeed));
    }

    @Override
    public void onGetPacket(GetPacketEvent event) {
        if (event.getPacket() instanceof PlayerPositionLookS2CPacket) {
            if (mc.player != null) {
            double x = mc.player.getX();
            double y = mc.player.getY();
            double z = mc.player.getZ();

            mc.inGameHud.getChatHud().addMessage(Text.of(String.format("ろるばどりゃー %.2f %.2f %.2f", x,y,z)));
            if (this.autoDisableFly.getValue()) {
                Fly fly = HarvestClient.getModuleManager().getModule(Fly.class);
                if (fly.isEnabled()) {
                    fly.setEnabled(false);
                }
            }
            if (this.autoDisableSpeed.getValue()) {
                DebugSpeed speed = HarvestClient.getModuleManager().getModule(DebugSpeed.class);
                if (speed.isEnabled()) {
                    speed.setEnabled(false);
                }
            }
        }
      }
    }
}
