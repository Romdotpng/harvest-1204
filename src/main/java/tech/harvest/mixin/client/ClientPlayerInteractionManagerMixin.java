package tech.harvest.mixin.client;

import tech.harvest.HarvestClient;
import tech.harvest.core.features.event.AttackEvent;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.harvest.core.features.module.ghost.Reach;

@Mixin({ClientPlayerInteractionManager.class})
public class ClientPlayerInteractionManagerMixin {
    @Inject(method = {"attackEntity"}, at = {@At("HEAD")}, cancellable = true)
    public void injectAttackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
        AttackEvent event = new AttackEvent(target);
        HarvestClient.getEventManager().call(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = {"getReachDistance"}, at = {@At("HEAD")}, cancellable = true)
    private void getReachDistanceHook(CallbackInfoReturnable<Float> cir) {
        if (Reach.isToggled()) {
            cir.setReturnValue((float) Reach.getReach().getValue());
        }
    }

    @Inject(method = {"hasExtendedReach"}, at = {@At("HEAD")}, cancellable = true)
    private void hasExtendedReachHook(CallbackInfoReturnable<Boolean> cir) {
        if (Reach.isToggled()) {
            cir.setReturnValue(true);
        }
    }
}
