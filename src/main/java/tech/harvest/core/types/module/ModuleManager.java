package tech.harvest.core.types.module;

import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import tech.harvest.core.features.module.misc.RPC;
import tech.harvest.core.features.module.combat.Antibot;
import tech.harvest.core.features.module.combat.KillAura;
import tech.harvest.core.features.module.combat.SlaughterAura;
import tech.harvest.core.features.module.combat.Velocity;
import tech.harvest.core.features.module.ghost.AimAssist;
import tech.harvest.core.features.module.ghost.AutoClicker;
import tech.harvest.core.features.module.ghost.Reach;
import tech.harvest.core.features.module.ghost.WTap;
import tech.harvest.core.features.module.misc.*;
import tech.harvest.core.features.module.player.*;
import tech.harvest.core.features.module.render.*;
import tech.harvest.core.features.module.movement.*;

public class ModuleManager {
    private final List<Module> modules = Arrays.asList(new Antibot(), new KillAura(), new Velocity(), new AutoSprint(), new SlaughterAura(), new Fly(), new FullBright(), new AutoL(), new ESP(), new Disabler(), new FlagDetector(), new AutoTool(), new FastBreak(), new DebugSpeed(), new InvMove(), new InvManager(), new HUD(), new ActiveModules(), new NameTags(), new ChestESP(), new CivBreak(), new AutoBreaker(), new Animations(), new Scaffold(), new Sneak(), new Debugger(), new Spammer(), new AdminChecker(), new AimAssist(), new AutoClicker(), new AutoSuccubus(), new WTap(), new BetterFightSound(), new Chams(), new Reach(), new Fucker(), new RPC(), new AntiVoid(), new DebugFly(), new Phase(), new NoSlow(), new Step(), new Jesus(), new NoFall(), new NoJumpDelay(), new ClientWorld(), new TargetThirdPerson());

    @Nullable
    public <M extends Module> M getModule(Class<M> moduleClass) {
        return (M)this.modules.stream().filter(m -> m.getClass().equals(moduleClass)).findFirst().orElse(null);
    }

    @Nullable
    public Module getModule(String moduleName) {
        return this.modules.stream().filter(m -> m.getName().equals(moduleName)).findFirst().orElse(null);
    }

    public List<Module> getModules() {
        return this.modules;
    }
}
