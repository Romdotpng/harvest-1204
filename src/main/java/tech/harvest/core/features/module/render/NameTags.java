package tech.harvest.core.features.module.render;

import com.mojang.blaze3d.systems.RenderSystem;
import tech.harvest.core.features.event.NametagEvent;
import tech.harvest.core.features.event.PostRender3DEvent;
import tech.harvest.core.features.event.PreRender3DEvent;
import tech.harvest.core.features.event.Render2DEvent;
import tech.harvest.core.features.setting.DoubleSetting;
import tech.harvest.core.features.setting.ModeSetting;
import tech.harvest.core.types.module.Module;
import tech.harvest.core.types.module.ModuleCategory;
import tech.harvest.core.util.PlayerUtil;
import tech.harvest.core.util.TickManager;
import tech.harvest.ui.font.TTFFontRenderer;
import me.x150.renderer.render.MSAAFramebuffer;
import me.x150.renderer.render.Renderer2d;
import me.x150.renderer.util.RendererUtils;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.awt.*;
import java.util.Comparator;

public class NameTags extends Module {
    private static final int MAX_SAMPLES = GL30.glGetInteger(36183);
    private final TTFFontRenderer font = TTFFontRenderer.of("Roboto-Light", 8);
    private final TTFFontRenderer nameDrawer = TTFFontRenderer.of("Roboto-Light", 14);
    private final ModeSetting mode = ModeSetting.build().name("Mode").value("Sigma").option("Sigma", "Harvest").end();
    private final DoubleSetting scale = DoubleSetting.build().name("Scale").value(1.0).range(1.0, 10.0).end();

    public NameTags() {
        super("NameTags", "", ModuleCategory.Render);
        this.getSettings().add(this.scale);
    }

    public static Vec3d getInterpolatedEntityPosition(Entity entity) {
        Vec3d a = entity.getPos();
        Vec3d b = new Vec3d(entity.prevX, entity.prevY, entity.prevZ);
        float p = mc.getTickDelta();
        return new Vec3d(MathHelper.lerp(p, b.x, a.x), MathHelper.lerp(p, b.y, a.y), MathHelper.lerp(p, b.z, a.z));
    }

    public static Vec3d getScreenSpaceCoordinate(Vec3d pos, MatrixStack stack) {
        Camera camera = NameTags.mc.getEntityRenderDispatcher().camera;
        if (camera == null) {
            return null;
        }
        Matrix4f matrix = stack.peek().getPositionMatrix();
        int displayHeight = mc.getWindow().getHeight();
        int[] viewport = new int[4];
        Vector3f target = new Vector3f();
        double deltaX = pos.x - camera.getPos().x;
        double deltaY = pos.y - camera.getPos().y;
        double deltaZ = pos.z - camera.getPos().z;
        GL11.glGetIntegerv(2978, viewport);
        Vector4f transformedCoordinates = new Vector4f((float) deltaX, (float) deltaY, (float) deltaZ, 1.0f).mul(matrix);
        Matrix4f matrixProj = new Matrix4f(RenderSystem.getProjectionMatrix());
        Matrix4f matrixModel = new Matrix4f(RenderSystem.getModelViewMatrix());
        matrixProj.mul(matrixModel).project(transformedCoordinates.x(), transformedCoordinates.y(), transformedCoordinates.z(), viewport, target);
        return new Vec3d((double) target.x / mc.getWindow().getScaleFactor(), (double) ((float) displayHeight - target.y) / mc.getWindow().getScaleFactor(), target.z);
    }

    public static boolean isOnScreen(Vec3d pos) {
        return pos != null && pos.z > -1.0 && pos.z < 1.0;
    }

    @Override
    public void onPreRender3D(PreRender3DEvent event) {
        if (NameTags.mc.player == null || NameTags.mc.world == null) {
            return;
        }
        if (NameTags.mc.gameRenderer.getCamera() == null) {
            return;
        }
        MSAAFramebuffer.use(Math.min(16, MAX_SAMPLES), () -> {
            for (AbstractClientPlayerEntity player : NameTags.mc.world.getPlayers().stream().sorted(Comparator.comparingDouble(value -> -value.getPos().distanceTo(NameTags.mc.gameRenderer.getCamera().getPos()))).filter(abstractClientPlayerEntity -> !abstractClientPlayerEntity.equals(NameTags.mc.player)).toList()) {
                this.render(event.getMatrices(), player, player.getName());
            }
        });
        super.onPreRender3D(event);
    }

    public void render(MatrixStack stack, AbstractClientPlayerEntity entity, Text text) {
        switch (this.mode.getValue()) {
            case("Sigma"): {
                String t = text.getString();
                Vec3d headPos = NameTags.getInterpolatedEntityPosition(entity).add(0.0, (double) entity.getHeight() + 0.3, 0.0);
                Vec3d a = NameTags.getScreenSpaceCoordinate(headPos, stack);
                if (NameTags.isOnScreen(a)) {
                    TickManager.runOnNextRender(event -> this.drawInternal(event, a, t, entity));
                }
                break;
            }
            case("Harvest"): {
                //オリジナルNametagのアイデア待ってます
                break;
            }
        }
    }

    void drawInternal(Render2DEvent eve, Vec3d screenPos, String text, AbstractClientPlayerEntity entity) {
        double healthHeight = 2.0;
        double labelHeight = (double) (2.0f + this.nameDrawer.getFontHeight() + this.font.getFontHeight() + 2.0f) + 2.0 + 2.0;
        GameMode gamemode = null;
        PlayerListEntry ple = mc.getNetworkHandler().getPlayerListEntry(entity.getUuid());
        if (ple != null) {
            gamemode = ple.getGameMode();
        }
        String pingStr = (PlayerUtil.getPing(entity) == 0 ? "?" : Integer.valueOf(PlayerUtil.getPing(entity))) + " ms";
        String gmString = "§cBot";
        if (gamemode != null) {
            switch (gamemode) {
                case ADVENTURE: {
                    gmString = "Adventure";
                    break;
                }
                case CREATIVE: {
                    gmString = "Creative";
                    break;
                }
                case SURVIVAL: {
                    gmString = "Survival";
                    break;
                }
                case SPECTATOR: {
                    gmString = "Spectator";
                }
            }
        }
        MatrixStack stack1 = new MatrixStack();
        Vec3d actual = new Vec3d(screenPos.x, screenPos.y - labelHeight, screenPos.z);
        float width = this.nameDrawer.getStringWidth(text) + 4.0f;
        width = Math.max(width, 60.0f);
        Renderer2d.renderRoundedQuad(stack1, new Color(0, 0, 5, 100), actual.x - (double) width / 2.0, actual.y, actual.x + (double) width / 2.0, actual.y + labelHeight, 3.0f, 20.0f);
        this.nameDrawer.drawString(stack1, text, actual.x + (double) width / 2.0 - (double) this.nameDrawer.getStringWidth(text) - 2.0, actual.y + 2.0, entity.getTeamColorValue());
        this.font.drawString(stack1, gmString, actual.x + (double) width / 2.0 - (double) this.font.getStringWidth(gmString) - 2.0, actual.y + 2.0 + (double) this.nameDrawer.getFontHeight(), 0xAAAAAA);
        if (PlayerUtil.getPing(entity) != -1) {
            this.font.drawString(stack1, pingStr, actual.x - (double) width / 2.0 + 2.0, actual.y + 2.0 + (double) this.nameDrawer.getFontHeight(), 0xAAAAAA);
        }
        Renderer2d.renderRoundedQuad(stack1, new Color(60, 60, 60, 255), actual.x - (double) width / 2.0 + 2.0, actual.y + labelHeight - 2.0 - 2.0, actual.x + (double) width / 2.0 - 2.0, actual.y + labelHeight - 2.0, 1.0f, 10.0f);
        float health = entity.getHealth();
        float maxHealth = entity.getMaxHealth();
        float healthPer = health / maxHealth;
        healthPer = MathHelper.clamp(healthPer, 0.0f, 1.0f);
        double drawTo = MathHelper.lerp(healthPer, actual.x - (double) width / 2.0 + 2.0 + 2.0, actual.x + (double) width / 2.0 - 2.0);
        Color MID_END = RendererUtils.lerp(Color.GREEN, Color.RED, healthPer);
        Renderer2d.renderRoundedQuad(stack1, MID_END, actual.x - (double) width / 2.0 + 2.0, actual.y + labelHeight - 2.0 - 2.0, drawTo, actual.y + labelHeight - 2.0, 1.0f, 10.0f);
        float xOffset = 0.0f;
        for (ItemStack stack : mc.player.getInventory().armor) {
            xOffset += 20.0f;
        }
        try {
            eve.getContext().drawItem(entity.getHandItems().iterator().next(), (int) (actual.x - (double) (width / 2.0f) + 4.0), (int) (actual.y + (double) this.font.getFontHeight() + 4.0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPostRender3D(PostRender3DEvent event) {
        super.onPostRender3D(event);
    }

    @Override
    public void onNametag(NametagEvent event) {
        event.cancel();
    }
}