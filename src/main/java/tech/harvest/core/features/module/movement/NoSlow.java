package tech.harvest.core.features.module.movement;

import java.util.Arrays;

import tech.harvest.core.features.setting.ModeSetting;
import tech.harvest.core.types.module.Module;
import tech.harvest.core.types.module.ModuleCategory;

public class NoSlow extends Module {
    public static boolean isToggled;

    private final ModeSetting mode = ModeSetting.build().name("Mode").option("Vanilla").value("Vanilla").end();

    public NoSlow() {
        super("NoSlow", "Prevents you from getting slowed down by certain actions", ModuleCategory.Movement);
        getSettings().addAll(Arrays.asList(this.mode));
    }

    @Override
    public void onEnable() {
        isToggled = true;
    }

    @Override
    public void onDisable() {
        isToggled = false;
    }
}
