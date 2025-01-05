package tech.harvest.core.features.command;

import tech.harvest.HarvestClient;
import tech.harvest.MCHook;
import tech.harvest.core.types.command.Command;
import me.x150.renderer.render.MSAAFramebuffer;
import net.minecraft.text.Text;

/* loaded from: wtf.jar:jp/sirapixel/core/features/command/ConfigCommand.class */
public class ConfigCommand extends Command implements MCHook {
    public ConfigCommand() {
        super("config", ".config delete <name>, .config load <name>, .config save <name>");
    }

    @Override // tech.harvest-client.core.types.command.Command
    public boolean execute(String[] args) {
        if (args.length != 2) {
            return false;
        }
        String lowerCase = args[0].toLowerCase();
        char c = 65535;
        switch (lowerCase.hashCode()) {
            case -1335458389:
                if (lowerCase.equals("delete")) {
                    c = 2;
                    break;
                }
                break;
            case 3327206:
                if (lowerCase.equals("load")) {
                    c = 0;
                    break;
                }
                break;
            case 3522941:
                if (lowerCase.equals("save")) {
                    c = 1;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                if (HarvestClient.getConfigManager().loadConfig(args[1])) {
                    mc.inGameHud.getChatHud().addMessage(Text.literal(String.format("Successfully loaded config: '%s'", args[1])));
                    return false;
                }
                mc.inGameHud.getChatHud().addMessage(Text.literal(String.format("Failed to load config: '%s'", args[1])));
                return false;
            case 1:
                if (HarvestClient.getConfigManager().saveConfig(args[1])) {
                    mc.inGameHud.getChatHud().addMessage(Text.literal(String.format("Successfully saved config: '%s'", args[1])));
                    return false;
                }
                mc.inGameHud.getChatHud().addMessage(Text.literal(String.format("Failed to save config: '%s'", args[1])));
                return false;
            case MSAAFramebuffer.MIN_SAMPLES /* 2 */:
                if (HarvestClient.getConfigManager().deleteConfig(args[1])) {
                    mc.inGameHud.getChatHud().addMessage(Text.literal(String.format("Successfully deleted config: '%s'", args[1])));
                    return false;
                }
                mc.inGameHud.getChatHud().addMessage(Text.literal(String.format("Failed to delete config: '%s'", args[1])));
                return false;
            default:
                return false;
        }
    }
}
