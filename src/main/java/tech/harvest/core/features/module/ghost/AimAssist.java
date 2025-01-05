package tech.harvest.core.features.module.ghost;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.StreamSupport;
import tech.harvest.core.features.event.RotationEvent;
import tech.harvest.core.features.setting.BooleanSetting;
import tech.harvest.core.features.setting.DoubleSetting;
import tech.harvest.core.types.module.Module;
import tech.harvest.core.types.module.ModuleCategory;
import tech.harvest.core.util.RandomUtil;
import tech.harvest.core.util.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;

public class AimAssist extends Module {
    private final DoubleSetting range = DoubleSetting.build().name("Range").value(3.8).range(3.0, 8.0).increment(0.1).end();
    private final DoubleSetting aimSpeed = DoubleSetting.build().name("Aim Speed").value(0.45).range(0.1, 10.0).increment(0.1).end();
    private final BooleanSetting holdOnly = BooleanSetting.build().name("Hold Only").value(true).end();
    private final BooleanSetting ignoreTeams = BooleanSetting.build().name("Ignore Teammate").value(false).end();
    private final BooleanSetting itemInUse = BooleanSetting.build().name("Item in use").value(false).end();
    private final BooleanSetting targetMonsters = BooleanSetting.build().name("Target Monsters").value(true).end();
    private final BooleanSetting targetAnimals = BooleanSetting.build().name("Target Animals").value(false).end();
    private final List<LivingEntity> validated = new ArrayList<>();
    private LivingEntity primary;
    private int breakTick;

    public AimAssist() {
        super("AimAssist", "Assist in aiming", ModuleCategory.Ghost);
        this.getSettings().addAll(Arrays.asList(this.range, this.aimSpeed, this.holdOnly, this.ignoreTeams, this.itemInUse, this.targetMonsters, this.targetAnimals));
    }

    @Override
    public void onDisable() {
        this.validated.clear();
        this.primary = null;
        this.breakTick = 0;
        super.onDisable();
    }

    @Override
    public void onRotation(RotationEvent event) {
        this.primary = this.findTarget();
        if (this.primary == null || !this.canAssist()) {
            return;
        }
        float diff = this.calculateYawChangeToDst(this.primary);
        float aimSpeed = (float)this.aimSpeed.getValue();
        aimSpeed = (float)MathHelper.clamp(RandomUtil.nextFloat(aimSpeed - 0.2f, aimSpeed + 1.8f), (double)this.aimSpeed.getMin(), (double)this.aimSpeed.getMax());
        aimSpeed = (float)((double)aimSpeed - (double)aimSpeed % this.getSensitivity());
        if (diff < -6.0f) {
            RotationUtil.virtualPrevPitch = RotationUtil.virtualPitch = (float)((double)RotationUtil.virtualPitch + Math.sin(Math.toRadians((RotationUtil.virtualYaw -= (aimSpeed -= diff / 12.0f)) - RotationUtil.virtualPrevYaw) * 0.5) * 1.5);
            RotationUtil.virtualPrevYaw = RotationUtil.virtualYaw;
        } else if (diff > 6.0f) {
            RotationUtil.virtualPrevPitch = RotationUtil.virtualPitch = (float)((double)RotationUtil.virtualPitch + Math.sin(Math.toRadians((RotationUtil.virtualYaw += (aimSpeed += diff / 12.0f)) - RotationUtil.virtualPrevYaw) * 0.5) * 1.5);
            RotationUtil.virtualPrevYaw = RotationUtil.virtualYaw;
        }
        super.onRotation(event);
    }

    public double getSensitivity() {
        double sensitivity = mc.options.getMouseSensitivity().getValue() * 0.6 + 0.2;
        return sensitivity * sensitivity * sensitivity * (double)RandomUtil.nextFloat(3.0f, 4.0f);
    }

    private boolean canAssist() {
        HitResult result;
        if (mc.currentScreen != null) {
            return false;
        }
        if (this.holdOnly.getValue() && !AimAssist.mc.options.attackKey.isPressed()) {
            return false;
        }
        if (mc.player.isUsingItem() && !this.itemInUse.getValue()) {
            return false;
        }
        if (mc.crosshairTarget != null && (result = AimAssist.mc.crosshairTarget) instanceof BlockHitResult) {
            BlockHitResult blockHitResult = (BlockHitResult)result;
            if (mc.world.getBlockState(blockHitResult.getBlockPos()).isAir() || AimAssist.mc.world.getBlockState(blockHitResult.getBlockPos()).isLiquid()) {
                return true;
            }
            if (mc.options.attackKey.isPressed()) {
                if (this.breakTick > 2) {
                    return false;
                }
                ++this.breakTick;
            } else {
                this.breakTick = 0;
            }
        }
        return true;
    }

    private LivingEntity findTarget() {
        this.validated.clear();
        StreamSupport.stream(AimAssist.mc.world.getEntities().spliterator(), false).filter(entity -> entity instanceof LivingEntity && entity != AimAssist.mc.player).map(entity -> (LivingEntity)entity).filter(entity -> !entity.isDead() && entity.isAlive() && entity.age > 10).forEach(entity -> {
            double focusRange;
            double d = focusRange = AimAssist.mc.player.canSee(entity) ? this.range.getValue() : 3.5;
            if ((double)AimAssist.mc.player.distanceTo(entity) > focusRange) {
                return;
            }
            if (entity instanceof PlayerEntity) {
                if (!this.ignoreTeams.getValue() && entity.getTeamColorValue() == AimAssist.mc.player.getTeamColorValue()) {
                    return;
                }
                this.validated.add(entity);
            } else if (entity instanceof MobEntity) {
                if (!this.targetMonsters.getValue()) {
                    return;
                }
                this.validated.add(entity);
            } else if (entity instanceof AnimalEntity) {
                if (!this.targetAnimals.getValue()) {
                    return;
                }
                this.validated.add(entity);
            }
        });
        if (this.validated.isEmpty()) {
            return null;
        }
        this.validated.sort(Comparator.comparingDouble(this::calculateYawChangeToDst));
        return this.validated.get(0);
    }

    public float calculateYawChangeToDst(Entity entity) {
        double diffX = entity.getX() - AimAssist.mc.player.getX();
        double diffZ = entity.getZ() - AimAssist.mc.player.getZ();
        double deg = Math.toDegrees(Math.atan(diffZ / diffX));
        if (diffZ < 0.0 && diffX < 0.0) {
            return (float)MathHelper.wrapDegrees(-((double)RotationUtil.virtualYaw - (90.0 + deg)));
        }
        if (diffZ < 0.0 && diffX > 0.0) {
            return (float)MathHelper.wrapDegrees(-((double)RotationUtil.virtualYaw - (-90.0 + deg)));
        }
        return (float)MathHelper.wrapDegrees(-((double)RotationUtil.virtualYaw - Math.toDegrees(-Math.atan(diffX / diffZ))));
    }
}