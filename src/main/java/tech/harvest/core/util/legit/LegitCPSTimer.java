package tech.harvest.core.util.legit;

import java.util.concurrent.ThreadLocalRandom;

public class LegitCPSTimer {
    private final long last = System.currentTimeMillis();

    public boolean canClick(double cps) {
        return ThreadLocalRandom.current().nextDouble() < cps / 20.0d;
    }
}
