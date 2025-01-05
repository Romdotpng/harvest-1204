package tech.harvest.core.features.module.combat;

import java.util.stream.StreamSupport;
import tech.harvest.core.features.event.PreUpdateEvent;
import tech.harvest.core.types.module.Module;
import tech.harvest.core.types.module.ModuleCategory;
import tech.harvest.core.util.TimerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Hand;

public class SlaughterAura extends Module {
    private final TimerUtil timer = new TimerUtil();

    public SlaughterAura() {
        super("SlaughterAura", "", ModuleCategory.Combat);
    }

    @Override
    public void onEnable() {
        this.timer.reset();
    }

    @Override
    public void onPreUpdate(PreUpdateEvent event) {
        for (Entity clientPlayerEntity : StreamSupport.stream(mc.world.getEntities().spliterator(), false).toList()) {
            if (!Antibot.isToggledStatic() || clientPlayerEntity.getTeamColorValue() != 16777215) {
                if (clientPlayerEntity.getTeamColorValue() != mc.player.getTeamColorValue() && clientPlayerEntity != mc.player && (clientPlayerEntity instanceof LivingEntity) && !(clientPlayerEntity instanceof ArmorStandEntity) && mc.player.distanceTo(clientPlayerEntity) <= 8.0f) {
                    if (this.timer.hasTimeElapsed(500L)) {
                        mc.getNetworkHandler().sendPacket(PlayerInteractEntityC2SPacket.attack(clientPlayerEntity, mc.player.isSneaking()));
                        mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
                    }
                    mc.particleManager.addEmitter(clientPlayerEntity, ParticleTypes.CRIT);
                    mc.particleManager.addEmitter(clientPlayerEntity, ParticleTypes.ENCHANTED_HIT);
                }
            }
        }
        if (this.timer.hasTimeElapsed(500L)) {
            this.timer.reset();
        }
        super.onPreUpdate(event);
    }
}
