package tech.harvest.mixin.entity;

import org.spongepowered.asm.mixin.Shadow;
import tech.harvest.HarvestClient;
import tech.harvest.core.features.event.UpdateVelocityEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.harvest.core.features.module.player.NoJumpDelay;

@Mixin({LivingEntity.class})
public class LivingEntityMixin {
    @Shadow
    private int jumpingCooldown;
    @Unique
    private static float lastYaw = 0.0f;

    @Inject(method = {"jump"}, at = {@At("HEAD")})
    private void keyCodec(CallbackInfo ci) {
        if (MinecraftClient.getInstance().player != null && ((Object) this instanceof ClientPlayerEntity)) {
            UpdateVelocityEvent event = new UpdateVelocityEvent(0.0f, MinecraftClient.getInstance().player.getYaw());
            HarvestClient.getEventManager().call(event);
            lastYaw = MinecraftClient.getInstance().player.getYaw();
            if (MinecraftClient.getInstance().player.getYaw() != event.yaw) {
                MinecraftClient.getInstance().player.setYaw(event.yaw);
            }
        }
    }

    @Inject(method = {"jump"}, at = {@At("TAIL")}, cancellable = true)
    private void elementCodec(CallbackInfo ci) {
        if (MinecraftClient.getInstance().player != null && ((Object) this instanceof ClientPlayerEntity)) {
            MinecraftClient.getInstance().player.setYaw(lastYaw);
        }
    }

    @Inject(method = { "tickMovement" }, at = { @At("HEAD") })
    private void hookTickMovement(final CallbackInfo callbackInfo) {
        if (HarvestClient.getModuleManager().getModule(NoJumpDelay.class).isEnabled()) {
            this.jumpingCooldown = 0;
        }
    }
}
