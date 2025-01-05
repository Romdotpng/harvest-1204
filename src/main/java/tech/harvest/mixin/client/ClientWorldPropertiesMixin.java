package tech.harvest.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import tech.harvest.HarvestClient;
import tech.harvest.core.features.module.render.ClientWorld;

@Mixin(value={net.minecraft.client.world.ClientWorld.Properties.class})
public class ClientWorldPropertiesMixin {
    @ModifyReturnValue(method={"getTimeOfDay"}, at={@At(value="RETURN")})
    private long injectOverrideTime(long original) {
        return (long)(HarvestClient.getModuleManager().getModule(ClientWorld.class).getTime().getValue() * 1000.0);
    }
}