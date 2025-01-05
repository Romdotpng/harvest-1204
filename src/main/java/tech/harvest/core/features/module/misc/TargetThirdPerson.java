package tech.harvest.core.features.module.misc;

import net.minecraft.client.option.Perspective;
import tech.harvest.HarvestClient;
import tech.harvest.core.features.event.PreUpdateEvent;
import tech.harvest.core.features.module.combat.KillAura;
import tech.harvest.core.features.setting.BooleanSetting;
import tech.harvest.core.features.setting.DoubleSetting;
import tech.harvest.core.types.module.Module;
import tech.harvest.core.types.module.ModuleCategory;
import tech.harvest.core.util.TimerUtil;

public final class TargetThirdPerson extends Module {
    private final BooleanSetting delayEnabled = BooleanSetting.build().name("Delay before returning").value(true).end();
    private final DoubleSetting delay = DoubleSetting.build().name("Delay").value(0.5d).range(0.0d, 3.0d).unit("sec").visibility(() -> this.delayEnabled.getValue()).end();
    private final TimerUtil timer = new TimerUtil();
    private boolean changed = false;
    private boolean targetSettled = false;

    public TargetThirdPerson() {
        super("TargetThirdPerson", "", ModuleCategory.Player);
        getSettings().add(this.delayEnabled);
        getSettings().add(this.delay);
    }

    @Override
    public void onEnable() {
        this.changed = false;
        this.targetSettled = false;
    }

    @Override
    public void onDisable() {
        if (this.changed) {
            mc.options.setPerspective(Perspective.FIRST_PERSON);
        }
    }

    @Override
    public void onPreUpdate(PreUpdateEvent event) {
        KillAura killAura = HarvestClient.getModuleManager().getModule(KillAura.class);
        boolean targetExists = killAura.isEnabled() && killAura.getTarget() != null;
        if (!this.changed && !this.targetSettled && targetExists) {
            mc.options.setPerspective(Perspective.THIRD_PERSON_BACK);
            this.targetSettled = true;
            this.changed = true;
        }
        if (!targetExists) {
            this.targetSettled = false;
        }
        if (this.changed && mc.options.getPerspective() != Perspective.THIRD_PERSON_BACK) {
            this.changed = false;
        }
        if (this.changed && targetExists) {
            this.timer.reset();
        }
        if (this.changed && !this.targetSettled) {
            if (!this.delayEnabled.getValue() || this.timer.hasTimeElapsed((long) (1000.0d * this.delay.getValue()))) {
                mc.options.setPerspective(Perspective.FIRST_PERSON);
                this.changed = false;
            }
        }
    }
}
