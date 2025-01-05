package tech.harvest.core.features.module.render;

import tech.harvest.core.types.module.Module;
import tech.harvest.core.types.module.ModuleCategory;

public class Chams extends Module {
    public static boolean toggled;

    public Chams() {
        super("Chams", "", ModuleCategory.Render);
    }

    @Override
    public void onEnable() {
        toggled = true;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        toggled = false;
        super.onDisable();
    }
}
