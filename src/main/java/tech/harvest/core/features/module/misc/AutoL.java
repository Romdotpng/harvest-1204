package tech.harvest.core.features.module.misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import tech.harvest.core.features.event.GetPacketEvent;
import tech.harvest.core.features.setting.BooleanSetting;
import tech.harvest.core.types.module.Module;
import tech.harvest.core.types.module.ModuleCategory;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySetHeadYawS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;

public class AutoL extends Module {
    private static final Pattern COLOR_PATTERN = Pattern.compile("§.");
    private final List<String> insults = new ArrayList<>();
    private final BooleanSetting global = BooleanSetting.build().name("Global").value(false).end();
    private int index;

    public AutoL() {
        super("AutoL", "", ModuleCategory.Misc);
        this.getSettings().add(this.global);
        String assetDir = "assets/harvest-client/autol.txt";
        try {
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(AutoL.class.getClassLoader().getResourceAsStream(assetDir))));
            while ((line = reader.readLine()) != null) {
                this.insults.add(line);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGetPacket(GetPacketEvent event) {
        if (AutoL.mc.player == null || AutoL.mc.world == null) {
            return;
        }
        if (event.getPacket() instanceof EntityS2CPacket) {
            return;
        }
        if (event.getPacket() instanceof EntityVelocityUpdateS2CPacket) {
            return;
        }
        if (event.getPacket() instanceof TeamS2CPacket) {
            return;
        }
        if (event.getPacket() instanceof ChunkDeltaUpdateS2CPacket) {
            return;
        }
        if (event.getPacket() instanceof BlockUpdateS2CPacket) {
            return;
        }
        if (event.getPacket() instanceof EntitySetHeadYawS2CPacket) {
            return;
        }
        if (event.getPacket() instanceof CommonPingS2CPacket) {
            return;
        }
        if (event.getPacket() instanceof BlockEntityUpdateS2CPacket) {
            return;
        }
        Packet<?> packet = event.getPacket();
        if (packet instanceof EntityPositionS2CPacket) {
            EntityPositionS2CPacket p = (EntityPositionS2CPacket)packet;
            return;
        }
        packet = event.getPacket();
        if (packet instanceof GameMessageS2CPacket packet2) {
            String string = packet2.content().getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n");
            Matcher matcher = COLOR_PATTERN.matcher(string);
            String text = matcher.replaceAll("");
            String[] splitedString = text.split(" ");
            if (splitedString.length < 3) {
                return;
            }
            if (!splitedString[0].startsWith(AutoL.mc.player.getName().getString())) {
                return;
            }
            switch (splitedString[1]) {
                case "killed":
                case "shot": {
                    String user = splitedString[2].split("\\(")[0];
                    if (this.global.getValue()) {
                        mc.getNetworkHandler().sendChatMessage(String.format("!%s %s", user, this.insults.get(this.index)));
                    } else {
                        mc.getNetworkHandler().sendChatMessage(String.format("@%s %s", user, this.insults.get(this.index)));
                    }
                    ++this.index;
                    if (this.index < this.insults.size()) break;
                    this.index = 0;
                }
            }
        }
        super.onGetPacket(event);
    }
}
