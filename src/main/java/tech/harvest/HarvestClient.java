package tech.harvest;

import tech.harvest.core.config.ConfigManager;
import tech.harvest.core.config.FileFactory;
import tech.harvest.core.config.ModulesFile;
import tech.harvest.core.types.command.CommandManager;
import tech.harvest.core.types.event.EventManager;
import tech.harvest.core.types.module.ModuleManager;
import tech.harvest.core.util.LoginApi;
import tech.harvest.ui.click.ClickGui;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HarvestClient {
    public static final String CLIENT_NAME = "Harvest";
    public static final String CLIENT_VERSION = "b2";
    public static final Object CLIENT_DEVELOPER = "Harvest Team";

    private EventManager eventManager;
    private ModuleManager moduleManager;
    private CommandManager commandManager;
    private ClickGui clickGui;
    private FileFactory fileFactory;
    private ConfigManager configManager;
    public static final Logger LOGGER = LoggerFactory.getLogger("Harvest-Client");
    private static final HarvestClient instance = new HarvestClient();

    public static void init() {
        LOGGER.info("your hwid: " + LoginApi.getHwid());
        LOGGER.info("Starting Harvest client");
        LoginApi.checkLogin();
        instance.eventManager = new EventManager();
        instance.moduleManager = new ModuleManager();
        instance.commandManager = new CommandManager();
        instance.fileFactory = new FileFactory();
        instance.clickGui = new ClickGui();
        instance.configManager = new ConfigManager();
        instance.fileFactory.load();
        instance.fileFactory.setupRoot("harvest-client");
        instance.fileFactory.add(new ModulesFile());
        LOGGER.info("Startup finished");
    }

    public static void shutdown() {
        instance.fileFactory.save();
    }

    public static FileFactory getFileFactory() {
        return instance.fileFactory;
    }

    public static ConfigManager getConfigManager() {
        return instance.configManager;
    }

    public static ClickGui getClickGui() {
        return instance.clickGui;
    }

    public static EventManager getEventManager() {
        return instance.eventManager;
    }

    public static ModuleManager getModuleManager() {
        return instance.moduleManager;
    }

    public static CommandManager getCommandManager() {
        return instance.commandManager;
    }
}
