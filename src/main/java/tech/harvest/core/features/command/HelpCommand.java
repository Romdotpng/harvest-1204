package tech.harvest.core.features.command;

import tech.harvest.HarvestClient;
import tech.harvest.MCHook;
import tech.harvest.core.types.command.Command;
import net.minecraft.text.Text;

public class HelpCommand extends Command implements MCHook {
    public HelpCommand() {
        super("help", ".help");
    }

    @Override
    public boolean execute(String[] args) {
        HarvestClient.getCommandManager().getCommands().forEach(c -> {
            mc.inGameHud.getChatHud().addMessage(Text.literal(c.getHint()));
        });
        return false;
    }
}
