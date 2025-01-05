package tech.harvest.core.features.module.movement;

import tech.harvest.core.features.event.MotionEvent;
import tech.harvest.core.features.setting.ModeSetting;
import tech.harvest.core.types.module.Module;
import tech.harvest.core.types.module.ModuleCategory;
import tech.harvest.core.util.PlayerUtil;

import java.util.Arrays;

/**
 * @author Hypinohaizin
 * @since 2024/12/12 6:45
 */

public class NoFall extends Module {
    private final ModeSetting mode = ModeSetting.build().name("Mode").value("Matrix").option("Matrix").end();

    public NoFall() {
        super("NoFall", "test", ModuleCategory.Movement);
        getSettings().addAll(Arrays.asList(mode));
    }

    @Override
    public void onMotion(MotionEvent event) {
        if (PlayerUtil.isBlockUnder()) {
            if (mc.player.fallDistance > 2) {
                mc.player.setVelocity(mc.player.getVelocity().x * 0.12, mc.player.getVelocity().y, mc.player.getVelocity().z * 0.12);
            }
            if (mc.player.fallDistance > 3) {
                event.onGround = true;
                mc.player.setOnGround(true);

                mc.player.fallDistance = 0;
            }
        }
    }
}
