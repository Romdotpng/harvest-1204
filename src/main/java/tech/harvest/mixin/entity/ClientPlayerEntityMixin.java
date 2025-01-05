package tech.harvest.mixin.entity;

import tech.harvest.HarvestClient;
import tech.harvest.core.features.event.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import tech.harvest.core.features.module.movement.NoSlow;

@Mixin({ClientPlayerEntity.class})
public abstract class ClientPlayerEntityMixin {
    @Shadow
    public abstract void move(MovementType movementType, Vec3d vec3d);

    @Redirect(method = {"canStartSprinting"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"))
    private boolean hookSprintAffectStart(ClientPlayerEntity playerEntity) {
        SlowdownEvent event = new SlowdownEvent();
        HarvestClient.getEventManager().call(event);
        if (!event.isSlowdown()) {
            return false;
        }
        return playerEntity.isUsingItem();
    }

    @Redirect(method = {"tickMovement"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"))
    public boolean redirectTickMovement(ClientPlayerEntity instance) {
        if (NoSlow.isToggled) return false;
        return instance.isUsingItem();
    }

    @ModifyArgs(method = {"move"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V"))
    public void injectMove(Args args) {
        Vec3d movement = args.get(1);
        MoveEvent event = new MoveEvent(movement.x, movement.y, movement.z);
        HarvestClient.getEventManager().call(event);
        args.set(1, new Vec3d(event.x, event.y, event.z));
    }

    @Inject(method = {"sendMovementPackets"}, at = {@At("HEAD")}, cancellable = true)
    public void injectSendMovementPacketsPre(CallbackInfo ci) {
        PreUpdateEvent event = new PreUpdateEvent();
        HarvestClient.getEventManager().call(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = {"sendMovementPackets"}, at = {@At("TAIL")})
    public void injectSendMovementPacketsPost(CallbackInfo ci) {
        PostUpdateEvent event = new PostUpdateEvent();
        HarvestClient.getEventManager().call(event);
    }

    @ModifyArgs(method = {"tickMovement"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;setSprinting(Z)V"))
    private void injected(Args args) {
        UpdateVelocityEvent event = new UpdateVelocityEvent(1.0f, MinecraftClient.getInstance().player.getYaw());
        HarvestClient.getEventManager().call(event);
        if (event.yaw != MinecraftClient.getInstance().player.getYaw()) {
            args.set(0, true);
        }
    }

    @Redirect(method = {"sendMovementPackets"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getPitch()F"))
    public float RedirectReplaceMovementPacketPitch(ClientPlayerEntity instance) {
        MotionEvent event = new MotionEvent(instance.getX(), instance.getY(), instance.getZ(), instance.getYaw(), instance.getPitch(), instance.isOnGround());
        HarvestClient.getEventManager().call(event);
        return event.pitch;
    }
}
