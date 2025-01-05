package tech.harvest.core.util.legit;

import tech.harvest.MCHook;
import tech.harvest.core.util.AlgebraUtil;
import tech.harvest.core.util.RandomUtil;
import tech.harvest.core.util.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class LegitEntityRotation implements MCHook {
    private Entity entity;
    private float aYaw;
    private float aPitch;
    private long next;

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public float[] calcRotation() {
        Vec3d eye = mc.player.getEyePos();
        Box bb = this.entity.getBoundingBox();
        Vec3d nearest = AlgebraUtil.nearest(bb, eye);
        if (bb.intersects(eye, eye.add(mc.player.getRotationVec(1.0f).multiply(6.0d)))) {
            if (System.currentTimeMillis() > this.next) {
                float[] center = RotationUtil.rotation(this.entity.getEyePos().add(0.0d, -0.3d, 0.0d), eye);
                this.next = System.currentTimeMillis() + RandomUtil.nextInt(50);
                this.aYaw = RandomUtil.nextFloat(0.3f) * MathHelper.wrapDegrees(center[0] - mc.player.getYaw());
                this.aPitch = RandomUtil.nextFloat(0.3f) * MathHelper.wrapDegrees(center[1] - mc.player.getPitch());
            }
            return new float[]{mc.player.getYaw() + (this.aYaw * RandomUtil.nextFloat(1.0f)), mc.player.getPitch() + (this.aPitch * RandomUtil.nextFloat(1.0f))};
        }
        float[] z = RotationUtil.rotation(nearest.add(RandomUtil.nextDouble(-0.10000000149011612d, 0.1d), RandomUtil.nextDouble(-0.10000000149011612d, 0.1d), RandomUtil.nextDouble(-0.10000000149011612d, 0.1d)), eye);
        return z;
    }
}
