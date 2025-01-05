package tech.harvest.core.util;

import tech.harvest.MCHook;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RotationUtil implements MCHook {
    public static float virtualYaw;
    public static float virtualPitch;
    public static float virtualPrevYaw;
    public static float virtualPrevPitch;

    public static float interpolateRotation(float current, float predicted, float percentage) {
        float f = MathHelper.wrapDegrees(predicted - current);
        if (f <= 10.0f && f >= -10.0f) {
            percentage = 1.0f;
        }
        return current + (percentage * f);
    }

    public static float smoothRot(float current, float goal, float speed) {
        return current + MathHelper.clamp(MathHelper.wrapDegrees(goal - current), -speed, speed);
    }

    public static float[] rotation(double x, double y, double z, double ax, double ay, double az) {
        double diffX = x - ax;
        double diffY = y - ay;
        double diffZ = z - az;
        float yaw = (float) (Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0d);
        float pitch = (float) (-Math.toDegrees(Math.atan2(diffY, Math.hypot(diffX, diffZ))));
        return new float[]{yaw, pitch};
    }

    public static float[] rotation(Vec3d a, Vec3d b) {
        return rotation(a.x, a.y, a.z, b.x, b.y, b.z);
    }

    public static float getFixedAngleDelta() {
        return 0.26f * 0.26f * 0.26f * 1.2f;
    }

    public static float getFixedSensitivityAngle(float targetAngle, float startAngle) {
        float gcd = getFixedAngleDelta();
        return startAngle + (((int) ((targetAngle - startAngle) / gcd)) * gcd);
    }
}
