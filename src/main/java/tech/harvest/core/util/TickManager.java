package tech.harvest.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import tech.harvest.MCHook;
import tech.harvest.core.features.event.Render2DEvent;

public class TickManager implements MCHook {
    static final List<Consumer<Render2DEvent>> nextTickRunners = new ArrayList<>();

    public static void runOnNextRender(Consumer<Render2DEvent> r) {
        nextTickRunners.add(r);
    }

    public static void render(Render2DEvent event) {
        if (mc.player == null) {
            nextTickRunners.clear();
        }
        for (Consumer<Render2DEvent> nextTickRunner : nextTickRunners) {
            nextTickRunner.accept(event);
        }
        nextTickRunners.clear();
    }
}
