package tech.harvest.mixin.entity;

import tech.harvest.HarvestClient;
import tech.harvest.core.features.event.UpdateVelocityEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Entity.class})
public class EntityMixin {
    @Unique
    private static Vec3d movementInputToVelocity(Vec3d movementInput, float speed, float yaw) {
        double d = movementInput.lengthSquared();
        if (d < 1.0E-7d) {
            return Vec3d.ZERO;
        }
        Vec3d vec3d = (d > 1.0d ? movementInput.normalize() : movementInput).multiply(speed);
        float f = MathHelper.sin(yaw * 0.017453292f);
        float g = MathHelper.cos(yaw * 0.017453292f);
        return new Vec3d((vec3d.x * g) - (vec3d.z * f), vec3d.y, (vec3d.z * g) + (vec3d.x * f));
    }

    @Inject(method={"updateVelocity"}, at={@At(value="HEAD")}, cancellable=true)
    private void onUpdateVelocity(float speed, Vec3d movementInput, CallbackInfo ci) {
        if (MinecraftClient.getInstance().player == null) {
            return;
        }
        if (!((Object) this instanceof ClientPlayerEntity)) {
            return;
        }
        UpdateVelocityEvent event = new UpdateVelocityEvent(1.0f, MinecraftClient.getInstance().player.getYaw());
        HarvestClient.getEventManager().call(event);
        if (MinecraftClient.getInstance().player.getYaw() != event.yaw) {
            Vec3d vec3d = movementInputToVelocity(new Vec3d(0.0, 0.0, 1.0), speed, event.yaw);
            MinecraftClient.getInstance().player.setVelocity(MinecraftClient.getInstance().player.getVelocity().add(vec3d));
            ci.cancel();
        }
    }
}
