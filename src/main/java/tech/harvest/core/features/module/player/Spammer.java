package tech.harvest.core.features.module.player;

import java.util.Arrays;

import tech.harvest.HarvestClient;
import tech.harvest.core.features.event.PreUpdateEvent;
import tech.harvest.core.features.setting.BooleanSetting;
import tech.harvest.core.features.setting.DoubleSetting;
import tech.harvest.core.types.module.Module;
import tech.harvest.core.types.module.ModuleCategory;
import net.minecraft.text.Text;

public class Spammer extends Module {
    public static String message = "I am using " + HarvestClient.CLIENT_NAME + " Client!";
    private final DoubleSetting delay = DoubleSetting.build().name("Delay").value(10.0d).range(1.0d, 100.0d).end();
    private final BooleanSetting randomNumber = BooleanSetting.build().name("Random Number").value(false).end();

    public Spammer() {
        super("Spammer", "", ModuleCategory.Player);
        getSettings().addAll(Arrays.asList(this.delay, this.randomNumber));
    }

    @Override
    public void onEnable() {
        mc.inGameHud.getChatHud().addMessage(Text.literal("Use command .spam <message> to spam a message"));
        super.onEnable();
    }

    @Override
    public void onPreUpdate(PreUpdateEvent event) {
        String spamMessage = message;
        if (this.randomNumber.getValue()) {
            spamMessage = spamMessage + " " + ((int) (Math.random() * 1000.0d));
        }
        if (!(mc.player == null || mc.world == null || mc.player.age % ((int) this.delay.getValue()) != 0)) {
            mc.getNetworkHandler().sendChatMessage(spamMessage);
        }
        super.onPreUpdate(event);
    }
}
