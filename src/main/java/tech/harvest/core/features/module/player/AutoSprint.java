package tech.harvest.core.features.module.player;

import tech.harvest.core.features.event.PreUpdateEvent;
import tech.harvest.core.types.module.Module;
import tech.harvest.core.types.module.ModuleCategory;

public class AutoSprint extends Module {
    public AutoSprint() {
        super("AutoSprint", "Makes you sprinting", ModuleCategory.Player);
        setKeyCode(79);
    }

    @Override
    public void onPreUpdate(PreUpdateEvent event) {
        mc.options.sprintKey.setPressed(true);
        super.onPreUpdate(event);
    }
}
