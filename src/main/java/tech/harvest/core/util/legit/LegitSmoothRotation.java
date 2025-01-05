package tech.harvest.core.util.legit;

import tech.harvest.MCHook;
import tech.harvest.core.util.RandomUtil;
import tech.harvest.core.util.RotationUtil;
import net.minecraft.util.math.MathHelper;

public class LegitSmoothRotation implements MCHook {
    private final float[] currentSpeed = new float[2];

    public void reset() {
        this.currentSpeed[0] = 0.0f;
        this.currentSpeed[1] = 0.0f;
    }

    public float[] calcRotation(float[] current, float[] target, float speed) {
        if (Math.hypot(MathHelper.wrapDegrees(target[0] - current[0]), target[1] - current[1]) < 100.0d) {
            return target;
        }
        this.currentSpeed[0] = RotationUtil.smoothRot(this.currentSpeed[0], MathHelper.wrapDegrees(target[0] - current[0]), RandomUtil.nextFloat(1.0f, 3.0f) * 1.0f);
        this.currentSpeed[1] = RotationUtil.smoothRot(this.currentSpeed[1], target[1] - current[1], RandomUtil.nextFloat(1.0f, 3.0f) * 1.0f);
        return new float[]{current[0] + this.currentSpeed[0], current[1] + this.currentSpeed[1]};
    }
}
