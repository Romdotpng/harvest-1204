package tech.harvest.core.features.module.render;

import java.awt.Color;
import tech.harvest.core.features.event.PostRender3DEvent;
import tech.harvest.core.features.setting.DoubleSetting;
import tech.harvest.core.types.module.Module;
import tech.harvest.core.types.module.ModuleCategory;
import me.x150.renderer.render.Renderer3d;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class ESP extends Module {
    private final DoubleSetting alpha = DoubleSetting.build().name("Alpha").value(100.0d).range(1.0d, 255.0d).increment(1.0d).end();

    public ESP() {
        super("ESP", "", ModuleCategory.Render);
        getSettings().add(this.alpha);
    }

    public static Vec3d getInterpolatedEntityPosition(Entity entity) {
        Vec3d a = entity.getPos();
        Vec3d b = new Vec3d(entity.prevX, entity.prevY, entity.prevZ);
        float p = mc.getTickDelta();
        return new Vec3d(MathHelper.lerp(p, b.x, a.x), MathHelper.lerp(p, b.y, a.y), MathHelper.lerp(p, b.z, a.z));
    }

    public static Color modify(Color original, int redOverwrite, int greenOverwrite, int blueOverwrite, int alphaOverwrite) {
        return new Color(redOverwrite == -1 ? original.getRed() : redOverwrite, greenOverwrite == -1 ? original.getGreen() : greenOverwrite, blueOverwrite == -1 ? original.getBlue() : blueOverwrite, alphaOverwrite == -1 ? original.getAlpha() : alphaOverwrite);
    }

    @Override
    public void onPostRender3D(PostRender3DEvent event) {
        if (!(mc.world == null || mc.player == null)) {
            for (Entity entity : mc.world.getEntities()) {
                if (!entity.getUuid().equals(mc.player.getUuid()) && shouldRenderEntity(entity)) {
                    Vec3d eSource = getInterpolatedEntityPosition(entity);
                    Renderer3d.renderFilled(event.getMatrices(), modify(new Color(entity.getTeamColorValue()), -1, -1, -1, (int) this.alpha.getValue()), eSource.subtract(new Vec3d(entity.getWidth(), 0.0d, entity.getWidth()).multiply(0.5d)), new Vec3d(entity.getWidth(), entity.getHeight(), entity.getWidth()));
                }
            }
        }
    }

    public boolean shouldRenderEntity(Entity le) {
        return le instanceof PlayerEntity;
    }
}
