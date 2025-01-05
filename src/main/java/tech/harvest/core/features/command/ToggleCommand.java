package tech.harvest.core.features.command;

import tech.harvest.HarvestClient;
import tech.harvest.MCHook;
import tech.harvest.core.types.command.Command;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;

public class ToggleCommand extends Command implements MCHook {
    public ToggleCommand() {
        super("Toggle", new String[]{"t"}, ".toggle <module name>");
    }

    @Override
    public boolean execute(String[] args) {
        if (args.length != 1) {
            return true;
        }
        if (HarvestClient.getModuleManager().getModules().stream().noneMatch(m -> {
            return m.getName().equalsIgnoreCase(args[0]);
        })) {
            mc.inGameHud.getChatHud().addMessage(Text.literal(String.format("Module '%s' not found", args[0])));
            return false;
        }
        HarvestClient.getModuleManager().getModules().stream().filter(m -> {
            return m.getName().equalsIgnoreCase(args[0]);
        }).forEach(m -> {
            m.toggle();
            ChatHud chatHud = mc.inGameHud.getChatHud();
            Object[] objArr = new Object[2];
            objArr[0] = m.getName();
            objArr[1] = m.isEnabled() ? "enabled" : "disabled";
            chatHud.addMessage(Text.literal(String.format("Module '%s' has been %s", objArr)));
        });
        return false;
    }
}
