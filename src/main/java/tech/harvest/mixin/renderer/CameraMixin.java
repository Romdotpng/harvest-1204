package tech.harvest.mixin.renderer;

import tech.harvest.MCHook;
import tech.harvest.core.util.RotationUtil;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({Camera.class})

public class CameraMixin implements MCHook {
    @Unique
    float tickDelta;

    @ModifyArgs(method = {"update"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V"))
    public void injectSetRotation(Args args) {
        float yaw = this.tickDelta == 1.0f ? RotationUtil.virtualYaw : MathHelper.lerp(this.tickDelta, RotationUtil.virtualPrevYaw, RotationUtil.virtualYaw);
        float pitch = this.tickDelta == 1.0f ? RotationUtil.virtualPitch : MathHelper.lerp(this.tickDelta, RotationUtil.virtualPrevPitch, RotationUtil.virtualPitch);
        if (mc.options.getPerspective().isFrontView()) {
            yaw += 180.0f;
            pitch = -pitch;
        }
        args.set(0, yaw);
        args.set(1, pitch);
    }

    @Inject(method = {"update"}, at = {@At("HEAD")})
    public void injectUpdate(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        this.tickDelta = tickDelta;
    }
}
