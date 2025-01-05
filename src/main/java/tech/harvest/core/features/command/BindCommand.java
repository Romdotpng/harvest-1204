package tech.harvest.core.features.command;

import tech.harvest.HarvestClient;
import tech.harvest.MCHook;
import tech.harvest.core.types.command.Command;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

public class BindCommand extends Command implements MCHook {
    public BindCommand() {
        super("Bind", ".bind <module name> <key name>");
    }

    @Override
    public boolean execute(String[] args) {
        if (args.length != 2) {
            return true;
        }
        if (HarvestClient.getModuleManager().getModules().stream().noneMatch(c -> {
            return c.getName().equalsIgnoreCase(args[0]);
        })) {
            mc.inGameHud.getChatHud().addMessage(Text.literal(String.format("Module '%S' not found", args[0])));
            return false;
        }
        HarvestClient.getModuleManager().getModules().stream().filter(m -> {
            return m.getName().equalsIgnoreCase(args[0]);
        }).forEach(m -> {
            InputUtil.Key key = InputUtil.fromKeyCode(256, 0);
            try {
                key = InputUtil.fromTranslationKey(String.format("key.keyboard.%s", args[1].toLowerCase()));
            } catch (IllegalArgumentException e) {
            }
            m.setKeyCode(key.getCode() == 256 ? -1 : key.getCode());
            if (m.getKeyCode() == -1) {
                mc.inGameHud.getChatHud().addMessage(Text.literal(String.format("Module %s is now unbounded", m.getName())));
            } else {
                mc.inGameHud.getChatHud().addMessage(Text.literal(String.format("Module %s is now bound with %s", m.getName(), key.getTranslationKey())));
            }
        });
        return false;
    }
}
