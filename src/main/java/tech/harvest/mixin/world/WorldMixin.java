package tech.harvest.mixin.world;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import tech.harvest.HarvestClient;
import tech.harvest.core.features.module.render.ClientWorld;

@Mixin(value={World.class})
public class WorldMixin {
    @ModifyReturnValue(method={"getTimeOfDay"}, at={@At(value="RETURN")})
    private long injectOverrideTime(long original) {
        return (long)(HarvestClient.getModuleManager().getModule(ClientWorld.class).getTime().getValue() * 1000.0);
    }
}