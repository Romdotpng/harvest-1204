package tech.harvest.mixin.client;

import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.session.Session;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({MinecraftClient.class})
public interface MinecraftClientAccessor {
    @Accessor("session")
    @Mutable
    void setSession(Session session);

    @Invoker("doAttack")
    boolean accessDoAttack();

    @Invoker("doItemUse")
    void accessDoUseItem();

    @Accessor("authenticationService")
    @Mutable
    void setAuthenticationService(YggdrasilAuthenticationService yggdrasilAuthenticationService);

    @Accessor("sessionService")
    @Mutable
    void setSessionService(MinecraftSessionService minecraftSessionService);
}
