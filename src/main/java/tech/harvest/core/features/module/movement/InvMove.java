package tech.harvest.core.features.module.movement;

import tech.harvest.core.features.event.KeyPressEvent;
import tech.harvest.core.features.event.PreUpdateEvent;
import tech.harvest.core.types.module.Module;
import tech.harvest.core.types.module.ModuleCategory;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.InputUtil;

public class InvMove extends Module {
    public InvMove() {
        super("InvMove", "", ModuleCategory.Movement);
    }

    @Override
    public void onKeyPress(KeyPressEvent event) {
        super.onKeyPress(event);
    }

    @Override
    public void onPreUpdate(PreUpdateEvent event) {
        if (mc.currentScreen != null && !(mc.currentScreen instanceof ChatScreen)) {
            mc.options.forwardKey.setPressed(InputUtil.isKeyPressed(mc.getWindow().getHandle(), InputUtil.fromTranslationKey(mc.options.forwardKey.getBoundKeyTranslationKey()).getCode()));
            mc.options.leftKey.setPressed(InputUtil.isKeyPressed(mc.getWindow().getHandle(), InputUtil.fromTranslationKey(mc.options.leftKey.getBoundKeyTranslationKey()).getCode()));
            mc.options.rightKey.setPressed(InputUtil.isKeyPressed(mc.getWindow().getHandle(), InputUtil.fromTranslationKey(mc.options.rightKey.getBoundKeyTranslationKey()).getCode()));
            mc.options.backKey.setPressed(InputUtil.isKeyPressed(mc.getWindow().getHandle(), InputUtil.fromTranslationKey(mc.options.backKey.getBoundKeyTranslationKey()).getCode()));
            mc.options.jumpKey.setPressed(InputUtil.isKeyPressed(mc.getWindow().getHandle(), InputUtil.fromTranslationKey(mc.options.jumpKey.getBoundKeyTranslationKey()).getCode()));
            mc.options.sprintKey.setPressed(InputUtil.isKeyPressed(mc.getWindow().getHandle(), InputUtil.fromTranslationKey(mc.options.sprintKey.getBoundKeyTranslationKey()).getCode()));
        }
    }
}
