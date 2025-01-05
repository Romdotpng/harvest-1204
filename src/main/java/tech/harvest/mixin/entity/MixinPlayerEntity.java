package tech.harvest.mixin.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({PlayerEntity.class})
public class MixinPlayerEntity {
    @Inject(method = {"attack"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V")}, cancellable = true)
    public void setVelocity(Entity target, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = {"attack"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setSprinting(Z)V")}, cancellable = true)
    public void setSprinting(Entity target, CallbackInfo ci) {
        ci.cancel();
    }
}
