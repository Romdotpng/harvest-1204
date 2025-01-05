package tech.harvest.mixin.gui;

import tech.harvest.HarvestClient;
import tech.harvest.core.features.event.Render2DEvent;
import tech.harvest.core.util.TickManager;
import me.x150.renderer.render.MSAAFramebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({InGameHud.class})
public class InGameHudMixin {
    @Unique
    private static final int MAX_SAMPLES = GL30.glGetInteger(36183);

    @Inject(method = {"render"}, at = {@At("TAIL")})
    public void onRender(DrawContext context, float delta, CallbackInfo ci) {
        Render2DEvent render2DEvent = new Render2DEvent(delta, context);
        MSAAFramebuffer.use(Math.min(16, MAX_SAMPLES), () -> {
            TickManager.render(render2DEvent);
        });
        HarvestClient.getEventManager().call(render2DEvent);
    }
}
