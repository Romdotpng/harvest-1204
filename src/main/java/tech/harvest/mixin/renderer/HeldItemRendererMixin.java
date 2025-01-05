package tech.harvest.mixin.renderer;

import tech.harvest.HarvestClient;
import tech.harvest.core.features.module.combat.KillAura;
import tech.harvest.core.features.module.render.Animations;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicBoolean;

@Mixin({HeldItemRenderer.class})
public abstract class HeldItemRendererMixin {
    @Unique
    private final MinecraftClient mc = MinecraftClient.getInstance();

    @Inject(method = {"renderFirstPersonItem"}, at = {@At(value = "HEAD")}, cancellable = true)
    private void onRenderItemHook(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        HarvestClient.getModuleManager().getModules().stream().filter(m -> m.getName().equalsIgnoreCase("KillAura")).forEach(m -> {
            AtomicBoolean shit = new AtomicBoolean(false);
            HarvestClient.getModuleManager().getModules().stream().filter(ma -> ma instanceof Animations).forEach(module -> shit.set(module.isEnabled()));
            if (m instanceof KillAura killAura) {
                if (!item.isEmpty() && !(item.getItem() instanceof FilledMapItem) && shit.get()) {
                    ci.cancel();
                    this.renderFirstPersonItemCustom(killAura, player, tickDelta, pitch, hand, swingProgress, item, equipProgress, matrices, vertexConsumers, light);
                }
            }
        });
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getAttackCooldownProgress(F)F"), method = {"updateHeldItems"})
    public float getAttackCooldownProgress(ClientPlayerEntity entity, float baseTime) {
        AtomicBoolean shit = new AtomicBoolean(false);
        HarvestClient.getModuleManager().getModules().stream().filter(m -> m instanceof Animations).forEach(module -> {
            shit.set(module.isEnabled());
        });
        if (shit.get()) {
            return 1.0f;
        }
        return entity.getAttackCooldownProgress(baseTime);
    }


    @Unique
    public void renderFirstPersonItemCustom(KillAura killAura, AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (player.getMainHandStack().getItem() instanceof SwordItem && (player.isUsingItem() && player.getOffHandStack().getItem() instanceof ShieldItem || KillAura.getTarget() != null && killAura.isEnabled()) && hand == Hand.MAIN_HAND) {
            matrices.push();
            Arm arm = player.getMainArm();
            matrices.translate(-0.1f, 0.05f, 0.0f);
            this.applyEquipOffset(matrices, arm, equipProgress);
            matrices.translate(0.0, 0.1 * (double) MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI), 0.0);
            this.applySwingOffset(matrices, arm, swingProgress);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-96.0f));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(16.0f));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(68.0f));
            boolean bl3 = arm == Arm.RIGHT;
            this.renderItem(player, item, bl3 ? ModelTransformationMode.FIRST_PERSON_RIGHT_HAND : ModelTransformationMode.FIRST_PERSON_LEFT_HAND, !bl3, matrices, vertexConsumers, light);
            matrices.pop();
            return;
        }
        if (!player.isUsingSpyglass()) {
            boolean bl = hand == Hand.MAIN_HAND;
            Arm arm = bl ? player.getMainArm() : player.getMainArm().getOpposite();
            matrices.push();
            if (item.isOf(Items.CROSSBOW)) {
                int i;
                boolean bl2 = CrossbowItem.isCharged(item);
                boolean bl3 = arm == Arm.RIGHT;
                int n = i = bl3 ? 1 : -1;
                if (player.isUsingItem() && player.getItemUseTimeLeft() > 0 && player.getActiveHand() == hand) {
                    this.applyEquipOffset(matrices, arm, equipProgress);
                    matrices.translate((float) i * -0.4785682f, -0.094387f, 0.05731531f);
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-11.935f));
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) i * 65.3f));
                    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) i * -9.785f));
                    float f = (float) item.getMaxUseTime() - ((float) this.mc.player.getItemUseTimeLeft() - tickDelta + 1.0f);
                    float g = f / (float) CrossbowItem.getPullTime(item);
                    if (g > 1.0f) {
                        g = 1.0f;
                    }
                    if (g > 0.1f) {
                        float h = MathHelper.sin((f - 0.1f) * 1.3f);
                        float j = g - 0.1f;
                        float k = h * j;
                        matrices.translate(k * 0.0f, k * 0.004f, k * 0.0f);
                    }
                    matrices.translate(g * 0.0f, g * 0.0f, g * 0.04f);
                    matrices.scale(1.0f, 1.0f, 1.0f + g * 0.2f);
                    matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees((float) i * 45.0f));
                } else {
                    float f = -0.4f * MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI);
                    float g = 0.2f * MathHelper.sin(MathHelper.sqrt(swingProgress) * ((float) Math.PI * 2));
                    float h = -0.2f * MathHelper.sin(swingProgress * (float) Math.PI);
                    matrices.translate((float) i * f, g, h);
                    this.applyEquipOffset(matrices, arm, equipProgress);
                    this.applySwingOffset(matrices, arm, swingProgress);
                    if (bl2 && swingProgress < 0.001f && bl) {
                        matrices.translate((float) i * -0.641864f, 0.0f, 0.0f);
                        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) i * 10.0f));
                    }
                }
                this.renderItem(player, item, bl3 ? ModelTransformationMode.FIRST_PERSON_RIGHT_HAND : ModelTransformationMode.FIRST_PERSON_LEFT_HAND, !bl3, matrices, vertexConsumers, light);
            } else {
                if (item.getItem() instanceof ShieldItem) {
                    return;
                }
                boolean bl2 = arm == Arm.RIGHT;
                float m = 0.0f;
                if (player.isUsingItem() && player.getItemUseTimeLeft() > 0 && player.getActiveHand() == hand) {
                    int l = bl2 ? 1 : -1;
                    switch (item.getUseAction()) {
                        case NONE:
                        case BLOCK: {
                            this.applyEquipOffset(matrices, arm, equipProgress);
                            break;
                        }
                        case EAT:
                        case DRINK: {
                            this.applyEatOrDrinkTransformationCustom(matrices, tickDelta, arm, item);
                            this.applyEquipOffset(matrices, arm, equipProgress);
                            this.applySwingOffset(matrices, arm, swingProgress);
                            break;
                        }
                        case BOW: {
                            this.applyEquipOffset(matrices, arm, equipProgress);
                            matrices.translate((float) l * -0.2785682f, 0.18344387f, 0.15731531f);
                            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-13.935f));
                            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) l * 35.3f));
                            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) l * -9.785f));
                            m = (float) item.getMaxUseTime() - ((float) this.mc.player.getItemUseTimeLeft() - tickDelta + 1.0f);
                            float f = m / 20.0f;
                            f = (f * f + f * 2.0f) / 3.0f;
                            if (f > 1.0f) {
                                f = 1.0f;
                            }
                            if (f > 0.1f) {
                                float g = MathHelper.sin((m - 0.1f) * 1.3f);
                                float h = f - 0.1f;
                                float j = g * h;
                                matrices.translate(j * 0.0f, j * 0.004f, j * 0.0f);
                            }
                            matrices.translate(f * 0.0f, f * 0.0f, f * 0.04f);
                            matrices.scale(1.0f, 1.0f, 1.0f + f * 0.2f);
                            matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees((float) l * 45.0f));
                            break;
                        }
                        case SPEAR: {
                            this.applyEquipOffset(matrices, arm, equipProgress);
                            matrices.translate((float) l * -0.5f, 0.7f, 0.1f);
                            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-55.0f));
                            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) l * 35.3f));
                            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) l * -9.785f));
                            m = (float) item.getMaxUseTime() - ((float) this.mc.player.getItemUseTimeLeft() - tickDelta + 1.0f);
                            float f = m / 10.0f;
                            if (f > 1.0f) {
                                f = 1.0f;
                            }
                            if (f > 0.1f) {
                                float g = MathHelper.sin((m - 0.1f) * 1.3f);
                                float h = f - 0.1f;
                                float j = g * h;
                                matrices.translate(j * 0.0f, j * 0.004f, j * 0.0f);
                            }
                            matrices.translate(0.0f, 0.0f, f * 0.2f);
                            matrices.scale(1.0f, 1.0f, 1.0f + f * 0.2f);
                            matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees((float) l * 45.0f));
                            break;
                        }
                        case BRUSH: {
                            this.applyBrushTransformation(matrices, tickDelta, arm, item, equipProgress);
                        }
                    }
                } else if (player.isUsingRiptide()) {
                    this.applyEquipOffset(matrices, arm, equipProgress);
                    int l = bl2 ? 1 : -1;
                    matrices.translate((float) l * -0.4f, 0.8f, 0.3f);
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) l * 65.0f));
                    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) l * -85.0f));
                } else if (item.getItem() instanceof BlockItem) {
                    this.applyEquipOffset(matrices, arm, equipProgress);
                    this.applySwingOffset(matrices, arm, swingProgress);
                    matrices.scale(0.5f, 0.5f, 0.5f);
                } else {
                    float n = -0.4f * MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI);
                    float u = 0.2f * MathHelper.sin(MathHelper.sqrt(swingProgress) * ((float) Math.PI * 2));
                    float b = -0.2f * MathHelper.sin(swingProgress * (float) Math.PI);
                    int o = arm == Arm.RIGHT ? 1 : -1;
                    matrices.translate((float) o * n, u, b);
                    this.applyEquipOffset(matrices, arm, equipProgress);
                    this.applySwingOffset(matrices, arm, swingProgress);
                }
                this.renderItem(player, item, bl2 ? ModelTransformationMode.FIRST_PERSON_RIGHT_HAND : ModelTransformationMode.FIRST_PERSON_LEFT_HAND, !bl2, matrices, vertexConsumers, light);
            }
            matrices.pop();
        }
    }

    @Unique
    private void applyBrushTransformation(MatrixStack matrices, float tickDelta, Arm arm, @NotNull ItemStack stack, float equipProgress) {
        applyEquipOffset(matrices, arm, equipProgress);
        float f = (this.mc.player.getItemUseTimeLeft() - tickDelta) + 1.0f;
        float g = 1.0f - (f / stack.getMaxUseTime());
        float m = (-15.0f) + (75.0f * MathHelper.cos(g * 45.0f * 3.1415927f));
        if (arm != Arm.RIGHT) {
            matrices.translate(0.1d, 0.83d, 0.35d);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-80.0f));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0f));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(m));
            matrices.translate(-0.3d, 0.22d, 0.35d);
            return;
        }
        matrices.translate(-0.25d, 0.22d, 0.35d);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-80.0f));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0f));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(0.0f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(m));
    }

    @Unique
    private void applyEquipOffset(@NotNull MatrixStack matrices, Arm arm, float equipProgress) {
        int i = arm == Arm.RIGHT ? 1 : -1;
        matrices.translate(i * 0.56f, (-0.52f) + (equipProgress * (-0.6f)), -0.72f);
    }

    @Unique
    private void applySwingOffset(@NotNull MatrixStack matrices, Arm arm, float swingProgress) {
        int i = arm == Arm.RIGHT ? 1 : -1;
        float f = MathHelper.sin(swingProgress * swingProgress * 3.1415927f);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i * (45.0f + (f * (-20.0f)))));
        float g = MathHelper.sin(MathHelper.sqrt(swingProgress) * 3.1415927f);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(i * g * (-20.0f)));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(g * (-80.0f)));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i * (-45.0f)));
    }

    @Unique
    public void renderItem(LivingEntity entity, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (!stack.isEmpty()) {
            this.mc.getItemRenderer().renderItem(entity, stack, renderMode, leftHanded, matrices, vertexConsumers, entity.getWorld(), light, OverlayTexture.DEFAULT_UV, entity.getId() + renderMode.ordinal());
        }
    }

    @Unique
    private void applyEatOrDrinkTransformationCustom(MatrixStack matrices, float tickDelta, Arm arm, @NotNull ItemStack stack) {
        float f = (this.mc.player.getItemUseTimeLeft() - tickDelta) + 1.0f;
        float g = f / stack.getMaxUseTime();
        if (g < 0.8f) {
            matrices.translate(0.0f, MathHelper.abs(MathHelper.cos((f / 4.0f) * 3.1415927f) * 0.005f), 0.0f);
        }
        float h = 1.0f - ((float) Math.pow(g, 27.0d));
        int i = arm == Arm.RIGHT ? 1 : -1;
        matrices.translate(h * 0.6f * i, h * (-0.5f), h * 0.0f);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i * h * 90.0f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(h * 10.0f));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(i * h * 30.0f));
    }
}
