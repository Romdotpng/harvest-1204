package tech.harvest.mixin.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import tech.harvest.HarvestClient;
import tech.harvest.core.features.event.PreRender3DEvent;
import tech.harvest.core.features.module.render.Chams;
import me.x150.renderer.render.MSAAFramebuffer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({WorldRenderer.class})
public class WorldRendererMixin {
    @Unique
    private static final int MAX_SAMPLES = GL30.glGetInteger(36183);
    @Unique
    private boolean isRenderingChams = false;

    @Inject(method = {"render"}, at = {@At("HEAD")}, cancellable = true)
    public void injectRenderPre(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f projectionMatrix, CallbackInfo ci) {
        PreRender3DEvent render3DEvent = new PreRender3DEvent(tickDelta, matrices);
        MSAAFramebuffer.use(Math.min(16, MAX_SAMPLES), () -> {
            HarvestClient.getEventManager().call(render3DEvent);
        });
        if (render3DEvent.isCancelled()) {
            ci.cancel();
        }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    @Inject(method = {"renderEntity"}, at = {@At("HEAD")})
    private void injectChamsForEntity(Entity entity, double cameraX, double cameraY, double cameraZ, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo ci) {
        if (Chams.toggled) {
            GL11.glEnable(32823);
            GL11.glPolygonOffset(1.0f, -1000000.0f);
            this.isRenderingChams = true;
        }
    }

    @Inject(method = {"renderEntity"}, at = {@At("RETURN")})
    private void injectChamsForEntityPost(Entity entity, double cameraX, double cameraY, double cameraZ, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo ci) {
        if (Chams.toggled && this.isRenderingChams) {
            GL11.glPolygonOffset(1.0f, 1000000.0f);
            GL11.glDisable(32823);
            this.isRenderingChams = false;
        }
    }
}
