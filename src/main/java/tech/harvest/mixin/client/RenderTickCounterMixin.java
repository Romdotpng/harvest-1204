package tech.harvest.mixin.client;

import net.minecraft.client.render.RenderTickCounter;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.harvest.core.util.MCTimerUtil;

@Mixin(RenderTickCounter.class)
public class RenderTickCounterMixin {
    @Shadow
    public float lastFrameDuration;

    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/RenderTickCounter;prevTimeMillis:J", opcode = Opcodes.PUTFIELD, ordinal = 0), method = "beginRenderTick(J)I")
    public void injectRenderTick(long timeMillis, CallbackInfoReturnable<Integer> cir) {
        lastFrameDuration *= MCTimerUtil.getTimer();
    }
}
