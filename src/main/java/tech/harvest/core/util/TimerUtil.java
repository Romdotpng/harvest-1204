package tech.harvest.core.util;

public class TimerUtil {
    public long time = System.currentTimeMillis();

    public boolean hasTimeElapsed(long delay) {
        return System.currentTimeMillis() - this.time > delay;
    }

    public long getTime() {
        return System.currentTimeMillis() - this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void reset() {
        this.time = System.currentTimeMillis();
    }
}
