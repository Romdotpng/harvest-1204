package tech.harvest.mixin.renderer;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.harvest.HarvestClient;
import tech.harvest.core.features.event.NametagEvent;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {
    @Inject(method = {"render"}, at = @At("HEAD"), cancellable = true)
    public void injectRender(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (!(entity instanceof PlayerEntity)) {
            return;
        }
        NametagEvent event = new NametagEvent((LivingEntity) entity);
        HarvestClient.getEventManager().call(event);

        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
