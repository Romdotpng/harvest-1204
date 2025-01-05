package tech.harvest.core.util;

import tech.harvest.MCHook;
import net.minecraft.util.hit.HitResult;

public class RayCastUtil implements MCHook {
    public static HitResult rayCast(float[] rot, double dist, float delta) {
        float prevYaw = mc.player.prevYaw;
        float prevPitch = mc.player.prevPitch;
        float yaw = mc.player.getYaw();
        float pitch = mc.player.getPitch();
        mc.player.setYaw(rot[0]);
        mc.player.setPitch(rot[1]);
        mc.player.prevYaw = yaw;
        mc.player.prevPitch = pitch;
        HitResult result = mc.player.raycast(dist, delta, false);
        mc.player.setYaw(yaw);
        mc.player.setPitch(pitch);
        mc.player.prevPitch = prevPitch;
        mc.player.prevYaw = prevYaw;
        return result;
    }
}
