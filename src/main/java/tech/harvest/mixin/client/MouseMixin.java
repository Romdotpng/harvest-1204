package tech.harvest.mixin.client;

import tech.harvest.HarvestClient;
import tech.harvest.core.features.event.RotationEvent;
import tech.harvest.core.util.RotationUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({Mouse.class})
public class MouseMixin {
    @ModifyArgs(method={"updateMouse"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"))
    public void injectUpdateMouse(Args args) {
        float f = RotationUtil.virtualPitch;
        float f1 = RotationUtil.virtualYaw;
        RotationUtil.virtualYaw = (float)((double)RotationUtil.virtualYaw + (Double)args.get(0) * 0.15);
        RotationUtil.virtualPitch = (float)((double)RotationUtil.virtualPitch + (Double)args.get(1) * 0.15);
        RotationUtil.virtualPitch = MathHelper.clamp(RotationUtil.virtualPitch, -90.0f, 90.0f);
        RotationUtil.virtualPrevPitch += RotationUtil.virtualPitch - f;
        RotationUtil.virtualPrevYaw += RotationUtil.virtualYaw - f1;
        args.set(0, (Object)0.0);
        args.set(1, (Object)0.0);
        RotationEvent rotationEvent = new RotationEvent(RotationUtil.virtualYaw, RotationUtil.virtualPitch);
        HarvestClient.getEventManager().call(rotationEvent);
        ClientPlayerEntity thePlayer = MinecraftClient.getInstance().player;
        float[] x = new float[]{rotationEvent.getYaw(), rotationEvent.getPitch()};
        thePlayer.setYaw(RotationUtil.getFixedSensitivityAngle(x[0], thePlayer.getYaw()));
        thePlayer.setPitch(RotationUtil.getFixedSensitivityAngle(x[1], thePlayer.getPitch()));
        thePlayer.setPitch(MathHelper.clamp(thePlayer.getPitch(), -90.0f, 90.0f));
    }
}
