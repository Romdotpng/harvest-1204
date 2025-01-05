package tech.harvest.mixin.client;

import tech.harvest.MCHook;
import tech.harvest.HarvestClient;
import tech.harvest.core.features.event.KeyPressEvent;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Keyboard.class})
public class KeyboardMixin implements MCHook {
    @Inject(method = {"onKey"}, at = {@At("HEAD")}, cancellable = true)
    public void injectOnKe(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (key != -1) {
            if (mc.currentScreen == null && action == 1) {
                if (key == 344) {
                    mc.setScreen(HarvestClient.getClickGui());
                }
                HarvestClient.getModuleManager().getModules().stream().filter(m -> {
                    return m.getKeyCode() == key;
                }).forEach((v0) -> {
                    v0.toggle();
                });
            }
            KeyPressEvent event = new KeyPressEvent(key, action);
            HarvestClient.getEventManager().call(event);
            if (event.isCancelled()) {
                ci.cancel();
            }
        }
    }
}
