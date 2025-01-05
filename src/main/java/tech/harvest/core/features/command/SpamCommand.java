package tech.harvest.core.features.command;

import tech.harvest.MCHook;
import tech.harvest.core.features.module.player.Spammer;
import tech.harvest.core.types.command.Command;
import net.minecraft.text.Text;

public class SpamCommand extends Command implements MCHook {
    public SpamCommand() {
        super("spam", ".spam <message>");
    }

    @Override
    public boolean execute(String[] args) {
        if (args.length <= 1) {
            return false;
        }
        StringBuilder message = new StringBuilder();
        for (String arg : args) {
            message.append(arg).append(" ");
        }
        Spammer.message = message.toString();
        mc.inGameHud.getChatHud().addMessage(Text.literal("Spam message set to: " + String.valueOf(message)));
        return true;
    }
}
