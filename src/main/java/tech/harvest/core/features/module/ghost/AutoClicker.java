package tech.harvest.core.features.module.ghost;

import java.util.Arrays;
import tech.harvest.core.features.event.ClickTickEvent;
import tech.harvest.core.features.setting.BooleanSetting;
import tech.harvest.core.types.module.Module;
import tech.harvest.core.types.module.ModuleCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

public class AutoClicker extends Module {
    private final BooleanSetting ignoreTeamsSetting = BooleanSetting.build().name("Ignore Teammate").value(false).end();
    private final BooleanSetting itemInUseSetting = BooleanSetting.build().name("Item in use").value(false).end();
    private final BooleanSetting leftClickSetting = BooleanSetting.build().name("Left Click").value(true).end();
    private final BooleanSetting rightClickSetting = BooleanSetting.build().name("Right Click").value(true).end();
    private boolean attacked;
    private boolean clicked;
    private int breakTick;

    public AutoClicker() {
        super("AutoClicker", "Automatically clicks", ModuleCategory.Ghost);
        this.getSettings().addAll(Arrays.asList(this.ignoreTeamsSetting, this.itemInUseSetting, this.leftClickSetting, this.rightClickSetting));
    }

    @Override
    public void onDisable() {
        this.attacked = false;
        this.clicked = false;
        this.breakTick = 0;
        super.onDisable();
    }

    @Override
    public void onClickTick(ClickTickEvent event) {
        if (this.leftClickSetting.getValue() && AutoClicker.mc.options.attackKey.isPressed() && this.shouldClick(true)) {
            this.doLeftClick();
        }
        if (this.rightClickSetting.getValue() && AutoClicker.mc.options.useKey.isPressed() && this.shouldClick(false)) {
            this.doRightClick();
        }
        super.onClickTick(event);
    }

    private void doLeftClick() {
        if (this.attacked && AutoClicker.mc.player.age % (int)Math.floor(Math.random() * 3.0 + 1.0) == 0) {
            AutoClicker.mc.interactionManager.stopUsingItem((PlayerEntity)AutoClicker.mc.player);
            this.attacked = false;
            return;
        }
        if (AutoClicker.mc.player.isUsingItem()) {
            return;
        }
        AutoClicker.mc.player.swingHand(Hand.MAIN_HAND);
        if (!(AutoClicker.mc.crosshairTarget instanceof EntityHitResult)) {
            return;
        }
        AutoClicker.mc.interactionManager.attackEntity(AutoClicker.mc.player, ((EntityHitResult)AutoClicker.mc.crosshairTarget).getEntity());
        this.attacked = true;
    }

    private void doRightClick() {
        if (this.clicked && AutoClicker.mc.player.age % (int)Math.floor(Math.random() * 3.0 + 1.0) == 0) {
            AutoClicker.mc.interactionManager.stopUsingItem(AutoClicker.mc.player);
            this.clicked = false;
            return;
        }
        if (AutoClicker.mc.player.isUsingItem()) {
            return;
        }
        AutoClicker.mc.interactionManager.interactItem(AutoClicker.mc.player, AutoClicker.mc.player.getActiveHand());
        this.clicked = true;
    }

    public boolean shouldClick(boolean left) {
        if (mc.isPaused() || !mc.isWindowFocused()) {
            return false;
        }
        if (mc.player.getItemUseTimeLeft() > 0 && !this.itemInUseSetting.getValue()) {
            return false;
        }
        if (mc.crosshairTarget != null && AutoClicker.mc.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            if (mc.world.getBlockState(new BlockPos((int) (mc.crosshairTarget).getPos().getX(), (int) (mc.crosshairTarget).getPos().getY(), (int) (mc.crosshairTarget).getPos().getZ())).isAir()) {
                return true;
            }
            if (mc.options.attackKey.isPressed()) {
                if (this.breakTick > 1) {
                    return false;
                }
                ++this.breakTick;
            } else {
                this.breakTick = 0;
            }
        } else {
            this.breakTick = 0;
            if (AutoClicker.mc.crosshairTarget != null && AutoClicker.mc.crosshairTarget.getType() == HitResult.Type.ENTITY && ((EntityHitResult)AutoClicker.mc.crosshairTarget).getEntity() instanceof PlayerEntity) {
                return !this.ignoreTeamsSetting.getValue() || !AutoClicker.mc.player.isTeammate(((EntityHitResult)AutoClicker.mc.crosshairTarget).getEntity());
            }
        }
        return true;
    }
}
