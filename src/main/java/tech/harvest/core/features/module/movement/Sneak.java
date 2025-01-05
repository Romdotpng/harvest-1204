package tech.harvest.core.features.module.movement;

import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;
import tech.harvest.core.features.event.MotionEvent;
import tech.harvest.core.features.setting.ModeSetting;
import tech.harvest.core.types.module.Module;
import tech.harvest.core.types.module.ModuleCategory;

import java.util.Arrays;

/**
 * @author Hypinohaizin
 * @since 2024/12/12 6:45
 */

public class Sneak extends Module {
    private final ModeSetting mode = ModeSetting.build().name("Mode").value("Normal").option("Normal").end();

    public Sneak() {
        super("Sneak", "Toggles or holds the sneak functionality", ModuleCategory.Movement);
        getSettings().addAll(Arrays.asList(mode));
    }

    @Override
    public void onMotion(MotionEvent event) {
        switch (mode.getValue()) {
            case "Normal": {
                if (mc.player != null && mc.world != null) {
                    mc.player.input.sneaking = mc.world.isChunkLoaded(mc.player.getBlockPos());
                }
                break;
            }
        }
    }
}
