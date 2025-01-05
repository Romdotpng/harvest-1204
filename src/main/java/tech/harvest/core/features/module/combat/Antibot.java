package tech.harvest.core.features.module.combat;

import tech.harvest.core.features.event.GetPacketEvent;
import tech.harvest.core.features.setting.ModeSetting;
import tech.harvest.core.types.module.Module;
import tech.harvest.core.types.module.ModuleCategory;
import tech.harvest.core.util.BotCheckPlayer;
import net.minecraft.entity.Entity;

import java.util.ArrayDeque;

public class Antibot extends Module {
    private final ModeSetting mode = ModeSetting.build().name("Mode").option("Matrix", "Matrix").end();

    private static boolean toggledStatic;
    ArrayDeque<BotCheckPlayer> neeedcheck = new ArrayDeque<>();

    public static boolean isToggledStatic() {
        return toggledStatic;
    }

    public Antibot() {
        super("Antibot", "Prevent this client from attacking bots", ModuleCategory.Combat);
    }

    @Override
    public void onEnable() {
        neeedcheck.clear();
        super.onEnable();
    }

    @Override
    public void onGetPacket(GetPacketEvent event) {
        if (mc.player != null) {
            switch (this.mode.getValue()) {
                case "Matrix": {

                }
            }
        }
    }

    public float[] getRotationsNoMot2(Entity e) {
        final float deltaX = (float)((e.getX()) - this.mc.player.getX());
        final float deltaY = (float)((e.getY()+1) - (this.mc.player.getY() + this.mc.player.getEyeHeight(mc.player.getPose())));
        final float deltaZ = (float)((e.getZ() ) - this.mc.player.getZ());
        final float distance = (float)(Math.sqrt(Math.pow(deltaX, 2.0)) + Math.sqrt(Math.pow(deltaZ, 2.0)));
        float yaw = (float)Math.toDegrees(-Math.atan(deltaX / deltaZ));
        final float pitch = (float)(-Math.toDegrees(Math.atan(deltaY / distance)));
        if (deltaX < 0.0f && deltaZ < 0.0f) {
            yaw = (float)(90.0 + Math.toDegrees(Math.atan(deltaZ / deltaX)));
        }
        else if (deltaX > 0.0f && deltaZ < 0.0f) {
            yaw = (float)(-90.0 + Math.toDegrees(Math.atan(deltaZ / deltaX)));
        }
        if(yaw<-360) {
            yaw+=360;
        }
        if(yaw>360) {
            yaw-=360;
        }

        return new float[] { yaw, pitch };
    }
}
