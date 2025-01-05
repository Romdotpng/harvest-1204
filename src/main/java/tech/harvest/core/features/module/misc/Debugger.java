package tech.harvest.core.features.module.misc;

import me.x150.renderer.render.Renderer3d;
import tech.harvest.core.features.event.PreRender3DEvent;
import tech.harvest.core.features.event.PreUpdateEvent;
import tech.harvest.core.features.event.SendPacketEvent;
import tech.harvest.core.types.module.Module;
import tech.harvest.core.types.module.ModuleCategory;
import tech.harvest.core.util.MCTimerUtil;
import tech.harvest.core.util.TimerUtil;

public class Debugger extends Module {
    public static boolean isToggled;
    private final TimerUtil timer = new TimerUtil();
    private int packets;
    private boolean flag;

    public Debugger() {
        super("Debugger", "For debug", ModuleCategory.Misc);
    }

    @Override
    public void onEnable() {
        isToggled = true;
    }

    @Override
    public void onDisable() {
        isToggled = false;
    }

    @Override
    public void onPreRender3D(PreRender3DEvent event) {
        super.onPreRender3D(event);
    }

    @Override
    public void onPreUpdate(PreUpdateEvent event) {
        super.onPreUpdate(event);
    }

    @Override
    public void onSendPacket(SendPacketEvent event) {
        super.onSendPacket(event);
    }
}
