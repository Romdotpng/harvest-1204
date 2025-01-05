package tech.harvest.mixin.gui;

import tech.harvest.HarvestClient;
import tech.harvest.MCHook;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ChatScreen.class})
public class ChatScreenMixin implements MCHook {
    @Inject(method = {"sendMessage"}, at = {@At("HEAD")}, cancellable = true)
    public void injectSendMessage(String chatText, boolean addToHistory, CallbackInfoReturnable<Boolean> cir) {
        boolean cancel = HarvestClient.getCommandManager().execute(chatText);
        if (cancel) {
            mc.inGameHud.getChatHud().addToMessageHistory(chatText);
            mc.setScreen(null);
            cir.cancel();
        }
    }
}
