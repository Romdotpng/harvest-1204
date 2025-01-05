package tech.harvest.core.features.module.ghost;

import java.util.Collections;
import tech.harvest.core.features.setting.DoubleSetting;
import tech.harvest.core.types.module.Module;
import tech.harvest.core.types.module.ModuleCategory;

public class Reach extends Module {
    private static final DoubleSetting reach = DoubleSetting.build().name("Reach").value(3.0d).range(3.0d, 8.0d).increment(0.1d).end();
    private static boolean toggled;

    public static DoubleSetting getReach() {
        return reach;
    }

    public static boolean isToggled() {
        return toggled;
    }

    public Reach() {
        super("Reach", "Increase reach distance", ModuleCategory.Ghost);
        getSettings().addAll(Collections.singletonList(reach));
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
