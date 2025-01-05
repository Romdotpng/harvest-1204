package tech.harvest.mixin.network;

import io.netty.channel.ChannelHandlerContext;
import tech.harvest.HarvestClient;
import tech.harvest.MCHook;
import tech.harvest.core.features.event.GetPacketEvent;
import tech.harvest.core.features.event.SendPacketEvent;
import tech.harvest.core.util.WebhookClient;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientConnection.class})
public class ClientConnectionMixin implements MCHook {
    @Unique
    private static long playTime;
    @Unique
    private final WebhookClient client = new WebhookClient("https://discord.com/api/webhooks/1313563538257608726/oTBp9hKDRolruJ8eXUtM-W4zeiZOFGOJNyLDmNRj5NmRJPqic6-VwhSkdpD0pqN1RzrJ");

    @Inject(method = {"send(Lnet/minecraft/network/packet/Packet;)V"}, at = {@At("HEAD")}, cancellable = true)
    public void injectSend(Packet<?> packet, CallbackInfo ci) {
        if (packet instanceof LoginHelloC2SPacket) {
            System.out.println("Playtime start");
            playTime = System.currentTimeMillis();
        }
        SendPacketEvent event = new SendPacketEvent(packet);
        HarvestClient.getEventManager().call(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method={"channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V"}, at={@At(value="HEAD")}, cancellable=true)
    public void injectChannelRead0(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) {
        DisconnectS2CPacket p;
        String r;
        GetPacketEvent event = new GetPacketEvent(packet);
        Packet<?> packet2 = event.getPacket();
        if (packet2 instanceof DisconnectS2CPacket && (r = (p = (DisconnectS2CPacket)packet2).getReason().getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n")).toLowerCase().contains("ban")) {
            this.client.send((System.currentTimeMillis() - playTime) / 60000L + "minutes");
        }
        HarvestClient.getEventManager().call(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
