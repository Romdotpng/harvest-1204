package tech.harvest.core.features.module.misc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import tech.harvest.core.features.event.GetPacketEvent;
import tech.harvest.core.features.event.PreUpdateEvent;
import tech.harvest.core.features.event.Render2DEvent;
import tech.harvest.core.features.event.SendPacketEvent;
import tech.harvest.core.features.setting.BooleanSetting;
import tech.harvest.core.features.setting.DoubleSetting;
import tech.harvest.core.features.setting.ModeSetting;
import tech.harvest.core.types.module.Module;
import tech.harvest.core.types.module.ModuleCategory;
import tech.harvest.core.util.TimerUtil;
import tech.harvest.core.util.WebhookClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class AdminChecker extends Module {
    private static final String[] ADMINS = new String[]{"HighlifeTTU", "Mistri", "Axyy", "lazertester", "Robertthegoat", "97WaterPolo", "aet2505", "Galap", "McJeffr", "Mistri", "halowars91", "Hilevi", "Pyachi2002", "Rafiki2085", "Red_Epicness", "_Silver", "InstantLightning", "JACOBSMILE", "JonnyDvE", "kbsfe", "ru555e11", "Selictove", "wmn", "sellejz", "Agypagy", "BasicAly", "Carrots386", "DJ_Pedro", "FullAdmin", "ImbC", "JTGangsterLP6", "M4bi", "Mistri", "MrJack", "GunOverdose", "pigplayer", "Pyachi2002", "Outra", "Rinjani", "Sevy13", "SnowVi1liers", "naqare", "ACrispyTortilla", "Hughzaz", "Moshyn", "Navarr", "ShadowLAX", "Brxnton", "ImAbbyy", "lPirlo", "Jarool", "Bupin", "Xhat", "EnderMCx", "LangScott", "WTDpuddles", "Daggez", "TurtleCobra", "OrcaHedral"};
    private static final WebhookClient client = new WebhookClient("https://discord.com/api/webhooks/1313563634961748038/rbhn67y7wzLKR1i__xrpMtcCsCgZqnR9H89BB0bAAvs6m1QUQQSfWDtJe_CqSGQxicTm");
    private final TimerUtil indexTimer = new TimerUtil();
    private final ModeSetting mode = ModeSetting.build().name("Mode").value("Rank").option("Rank", "Tab", "Tell", "Ban").end();
    private final DoubleSetting delay = DoubleSetting.build().name("Delay").value(1000.0).range(100.0, 1000.0).increment(100.0).unit("Second").end();
    private final BooleanSetting discordNotification = BooleanSetting.build().name("Discord Notification").value(true).end();
    private final BooleanSetting autoleave = BooleanSetting.build().name("Auto Leave").value(false).end();
    private final Set<String> checkedAdmin = new HashSet<>();
    private volatile boolean displaying = false;
    private int index = 0;

    public AdminChecker() {
        super("AdminChecker", "Check if a player is an admin", ModuleCategory.Misc);
        this.getSettings().addAll(Arrays.asList(this.mode, this.delay, this.discordNotification, this.autoleave));
    }

    @Override
    public void onEnable() {
        this.checkedAdmin.clear();
        this.index = 0;
        super.onEnable();
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        if (this.checkedAdmin.isEmpty()) {
            event.getContext().drawCenteredTextWithShadow(AdminChecker.mc.textRenderer, "No admins", mc.getWindow().getScaledWidth() / 2, mc.getWindow().getScaledHeight() / 2, -1);
        } else {
            event.getContext().drawCenteredTextWithShadow(AdminChecker.mc.textRenderer, "Admins found: " + this.checkedAdmin.size(), mc.getWindow().getScaledWidth() / 2, mc.getWindow().getScaledHeight() / 2 - 10, -65536);
            this.displaying = true;
            int i = 0;
            for (Object content : this.checkedAdmin.toArray()) {
                event.getContext().drawCenteredTextWithShadow(AdminChecker.mc.textRenderer, content.toString(), mc.getWindow().getScaledWidth() / 2, mc.getWindow().getScaledHeight() / 2 + i * 10, -65536);
                ++i;
            }
            this.displaying = false;
        }
        super.onRender2D(event);
    }

    @Override
    public void onPreUpdate(PreUpdateEvent event) {
        while (this.displaying) {
            Thread.onSpinWait();
        }
        for (PlayerListEntry playerListEntry : mc.getNetworkHandler().getPlayerList()) {
            for (String admin : ADMINS) {
                if (!playerListEntry.getProfile().getName().contains(admin)) continue;
                AdminChecker.mc.inGameHud.getChatHud().addMessage((Text)Text.literal((String)("Admin found: " + admin + " by player list")));
                this.adminChecked(admin);
            }
        }
        if (this.indexTimer.hasTimeElapsed((long)this.delay.getValue())) {
            ++this.index;
            if (this.index >= ADMINS.length) {
                if (this.checkedAdmin.isEmpty() && this.discordNotification.getValue()) {
                    client.send("Admin checker found no admins!");
                }
                AdminChecker.mc.inGameHud.getChatHud().addMessage((Text)Text.literal((String)("Finished checking all admins. Admin size: " + this.checkedAdmin.size())));
                this.checkedAdmin.clear();
                this.index = 0;
            }
            this.indexTimer.reset();
            switch (this.mode.getValue()) {
                case "Rank": {
                    mc.getNetworkHandler().sendChatCommand("rank " + ADMINS[this.index]);
                    break;
                }
                case "Tab": {
                    for (PlayerListEntry playerListEntry : mc.getNetworkHandler().getPlayerList()) {
                        String playerName = playerListEntry.getProfile().getName();
                        for (String admin : ADMINS) {
                            if (playerName.equals(admin) && !this.checkedAdmin.contains(admin)) {
                               // AdminChecker.mc.inGameHud.getChatHud().addMessage((Text) Text.literal("Admin found in tab: " + admin)); //デバック用
                                this.adminChecked(admin);
                            }
                        }
                    }
                    break;
                }
                case "Ban": {
                    mc.getNetworkHandler().sendChatCommand("ban " + ADMINS[this.index]);
                    break;
                }
                case "Tell": {
                    mc.getNetworkHandler().sendChatCommand("tell " + ADMINS[this.index] + " #If there is ImbC only, he abuses compromised account ban.");
                }
            }
        }
        super.onPreUpdate(event);
    }

    @Override
    public void onGetPacket(GetPacketEvent event) {
        if (AdminChecker.mc.player == null) {
            return;
        }
        try {
            Packet<?> packet = event.getPacket();
            if (packet instanceof GameMessageS2CPacket) {
                GameMessageS2CPacket packet2 = (GameMessageS2CPacket)packet;
                String content = packet2.content().getString();
                if (content.contains("No player was found")) {
                    AdminChecker.mc.inGameHud.getChatHud().addMessage((Text)Text.literal((String)("Admin found: " + ADMINS[this.index] + " by game message")));
                    this.adminChecked(ADMINS[this.index]);
                    event.cancel();
                } else if (content.contains("見つかりませんでした")) {
                    event.cancel();
                }
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        super.onGetPacket(event);
    }

    @Override
    public void onSendPacket(SendPacketEvent event) {
        super.onSendPacket(event);
    }

    private void adminChecked(String name) {
        this.checkedAdmin.add(name);
        if (this.discordNotification.getValue()) {
            client.send("Admin checker found: " + name);
        }

        AdminChecker.mc.world.playSound(null, AdminChecker.mc.player.getBlockPos(), SoundEvents.UI_TOAST_IN, SoundCategory.WEATHER, 3.0f, 1.0f);

        if (this.autoleave.getValue()) {
            mc.getNetworkHandler().sendChatCommand("hub");
        }
    }
}