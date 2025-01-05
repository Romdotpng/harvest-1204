package tech.harvest.mixin.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import tech.harvest.HarvestClient;
import tech.harvest.core.features.event.PostRender3DEvent;
import me.x150.renderer.render.MSAAFramebuffer;
import me.x150.renderer.render.Renderer3d;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GameRenderer.class})
public class GameRendererMixin {
    @Unique
    private static final int MAX_SAMPLES = GL30.glGetInteger(36183);
    @Shadow
    @Final
    private Camera camera;

    @Inject(at = {@At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;pop()V", shift = At.Shift.BEFORE, ordinal = 0)}, method = {"render"})
    void postHudRenderNoCheck(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
    }

    @Inject(at = {@At(value = "FIELD", target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z", opcode = 180, ordinal = 0)}, method = {"renderWorld"}, cancellable = true)
    void dispatchWorldRender(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo ci) {
        RenderSystem.backupProjectionMatrix();
        MatrixStack ms = new MatrixStack();
        ms.push();
        ms.multiply(RotationAxis.POSITIVE_X.rotationDegrees(this.camera.getPitch()));
        ms.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(this.camera.getYaw() + 180.0f));
        MSAAFramebuffer.use(Math.min(16, MAX_SAMPLES), () -> {
            PostRender3DEvent render3DEvent = new PostRender3DEvent(tickDelta, matrix);
            HarvestClient.getEventManager().call(render3DEvent);
            if (render3DEvent.isCancelled()) {
                ci.cancel();
            }
            Renderer3d.renderFadingBlocks(ms);
        });
        ms.pop();
        RenderSystem.restoreProjectionMatrix();
    }
}
