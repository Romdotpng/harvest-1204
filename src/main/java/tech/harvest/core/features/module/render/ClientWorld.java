package tech.harvest.core.features.module.render;

import tech.harvest.core.features.event.PreUpdateEvent;
import tech.harvest.core.features.setting.DoubleSetting;
import tech.harvest.core.types.module.Module;
import tech.harvest.core.types.module.ModuleCategory;

public class ClientWorld extends Module {
    private final DoubleSetting time = DoubleSetting.build().name("Time").value(8.0).range(0.0, 24.0).end();
    private final DoubleSetting timeCycle = DoubleSetting.build().name("Time Cycle").value(0.0).range(0.0, 24.0).end();

    public ClientWorld() {
        super("ClientWorld", "", ModuleCategory.Render);
        this.getSettings().add(this.time);
        this.getSettings().add(this.timeCycle);
    }

    @Override
    public void onPreUpdate(PreUpdateEvent event) {
        this.time.setValue(this.time.getValue() + this.timeCycle.getValue());
    }

    public DoubleSetting getTime() {
        return this.time;
    }

    public DoubleSetting getTimeCycle() {
        return this.timeCycle;
    }
}