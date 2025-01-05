package tech.harvest.core.features.module.render;

import tech.harvest.core.features.event.PreUpdateEvent;
import tech.harvest.core.types.module.Module;
import tech.harvest.core.types.module.ModuleCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class FullBright extends Module {
    private final double brightness = mc.options.getGamma().getValue();
    private final double full_bright = 1.0d;

    public FullBright() {
        super("FullBright", "", ModuleCategory.Render);
    }

    @Override
    public void onDisable() {
        mc.options.getGamma().setValue(this.brightness);
        super.onDisable();
    }

    @Override
    public void onPreUpdate(PreUpdateEvent event) {
        mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 114514));
        super.onPreUpdate(event);
    }
}
