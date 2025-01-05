package tech.harvest.mixin.client;

import tech.harvest.HarvestClient;
import tech.harvest.MCHook;
import tech.harvest.core.features.event.InputEvent;
import tech.harvest.core.util.RotationUtil;
import net.minecraft.client.input.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({KeyboardInput.class})
public class KeyboardInputMixin implements MCHook {
    @Unique
    private void fixStrafe(InputEvent event) {
        float diff = RotationUtil.virtualYaw - mc.player.getYaw();
        float f = (float)Math.sin(diff * ((float)Math.PI / 180));
        float f1 = (float)Math.cos(diff * ((float)Math.PI / 180));
        float multiplier = 1.0f;
        if (KeyboardInputMixin.mc.player.isSneaking() || mc.player.isUsingItem()) {
            multiplier = 10.0f;
        }
        float forward = (float)Math.round(((double)event.getInput().movementForward * (double)f1 + (double)event.getInput().movementSideways * (double)f) * (double)multiplier) / multiplier;
        float strafe = (float)Math.round(((double)event.getInput().movementSideways * (double)f1 - (double)event.getInput().movementForward * (double)f) * (double)multiplier) / multiplier;
        event.getInput().movementForward = forward;
        event.getInput().movementSideways = strafe;
    }

    @Inject(method={"tick"}, at={@At(value="TAIL")})
    public void injectTick(boolean slowDown, float slowDownFactor, CallbackInfo ci) {
        InputEvent event = new InputEvent(mc.player.input,  slowDownFactor);
        HarvestClient.getEventManager().call(event);
        if (event.moveFix) {
            this.fixStrafe(event);
        }
        if (slowDownFactor == 0.2f) {
            if (Math.abs(event.getInput().movementForward) > slowDownFactor) {
                event.getInput().movementForward *= slowDownFactor;
            }
            if (Math.abs(event.getInput().movementSideways) > slowDownFactor) {
                event.getInput().movementSideways *= slowDownFactor;
            }
        }
    }
}
