package tech.harvest.core.features.module.misc;

import meteordevelopment.discordipc.DiscordIPC;
import meteordevelopment.discordipc.RichPresence;
import tech.harvest.HarvestClient;
import tech.harvest.core.types.module.Module;
import tech.harvest.core.types.module.ModuleCategory;

/**
 * @author Hypinohaizin
 * @since 2024/12/08 6:33
 */

public class RPC extends Module {

    private static final RichPresence RPC = new RichPresence();

    public RPC() {
        super("RPC", "DiscordRpc", ModuleCategory.Misc);
    }

    @Override
    public void onEnable() {
        DiscordIPC.start(1304019508612042803L, null);
        RPC.setStart(System.currentTimeMillis() / 1000L);
        RPC.setLargeImage("logo", HarvestClient.CLIENT_NAME);
        RPC.setState(HarvestClient.CLIENT_NAME +HarvestClient.CLIENT_VERSION);

        DiscordIPC.setActivity(RPC);
    }

    @Override
    public void onDisable() {
        DiscordIPC.stop();
        super.onDisable();
    }
}
