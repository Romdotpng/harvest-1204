package tech.harvest.core.util;

import tech.harvest.MCHook;

public class MCTimerUtil implements MCHook {
    private static float timer = 1;

    public static void setTimer(float time) {
        timer = time;
    }

    public static float getTimer() {
        return timer;
    }
}
