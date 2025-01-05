package tech.harvest.mixin.client;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.harvest.HarvestClient;
import tech.harvest.core.features.event.ClickTickEvent;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({MinecraftClient.class})
public class MinecraftClientMixin {
    @ModifyArgs(method = {"<init>"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/Window;setPhase(Ljava/lang/String;)V"))
    public void onPostSetup(Args args) {
        if (args.get(0).equals("Post startup")) {
            HarvestClient.init();
        }
    }

    @Inject(method = "getWindowTitle", at = @At("RETURN"), cancellable = true)
    public void injectGetWindowTitle(CallbackInfoReturnable<String> cir) {
        String title = cir.getReturnValue();
        if (title.contains(" - ")) {
            title = "  |  " + title.split(" - ")[1];
        } else {
            title = "";
        }
        cir.setReturnValue(HarvestClient.CLIENT_NAME + " Client  |  Version: " + HarvestClient.CLIENT_VERSION + "  |  Developer: " + HarvestClient.CLIENT_DEVELOPER + title);
    }

    @Inject(method = {"close"}, at = {@At("HEAD")})
    public void aVoid(CallbackInfo ci) {
        HarvestClient.shutdown();
    }

    @Inject(method = {"handleInputEvents"}, at = {@At("HEAD")}, cancellable = true)
    public void injectHandleInputEvents(CallbackInfo ci) {
        ClickTickEvent event = new ClickTickEvent();
        HarvestClient.getEventManager().call(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
