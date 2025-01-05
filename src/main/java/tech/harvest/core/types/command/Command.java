package tech.harvest.core.types.command;

public abstract class Command {
    private final String name;
    private final String hint;
    private final String[] aliases;

    public abstract boolean execute(String[] strArr);

    public String getName() {
        return this.name;
    }

    public String getHint() {
        return this.hint;
    }

    public String[] getAliases() {
        return this.aliases;
    }

    public Command(String name, String hint) {
        this.hint = hint;
        this.aliases = new String[0];
        this.name = name;
    }

    public Command(String name, String[] aliases, String hint) {
        this.hint = hint;
        this.aliases = aliases;
        this.name = name;
    }
}
