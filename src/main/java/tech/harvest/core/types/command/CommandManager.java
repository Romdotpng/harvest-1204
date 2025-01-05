package tech.harvest.core.types.command;

import java.util.Arrays;
import java.util.List;
import tech.harvest.MCHook;
import tech.harvest.core.features.command.BindCommand;
import tech.harvest.core.features.command.ConfigCommand;
import tech.harvest.core.features.command.HelpCommand;
import tech.harvest.core.features.command.SpamCommand;
import tech.harvest.core.features.command.ToggleCommand;
import tech.harvest.core.util.LoginApi;
import net.minecraft.text.Text;

public class CommandManager implements MCHook {
    private static final String PREFIX = ".";
    private final List<Command> commands = Arrays.asList(new BindCommand(), new HelpCommand(), new ToggleCommand(), new ConfigCommand(), new SpamCommand());

    public List<Command> getCommands() {
        return this.commands;
    }

    public boolean execute(String message) {
        if (!LoginApi.logged || !message.startsWith(PREFIX)) {
            return false;
        }
        String[] sp = message.substring(PREFIX.length()).split(" ");
        Command cmd = this.commands.stream().filter(c ->
                c.getName().equalsIgnoreCase(sp[0]) ||
                        Arrays.stream(c.getAliases()).anyMatch(
                                alias -> alias.equalsIgnoreCase(sp[0]))).findFirst().orElse(null);
        if (cmd == null) {
            mc.inGameHud.getChatHud().addMessage(Text.literal(String.format("Command '%s' not found", sp[0])));
            return true;
        }
        String[] args = new String[sp.length - 1];
        System.arraycopy(sp, 1, args, 0, sp.length - 1);
        if (!cmd.execute(args)) {
            return true;
        }
        mc.inGameHud.getChatHud().addMessage(Text.literal(cmd.getHint()));
        return true;
    }
}
