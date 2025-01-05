package tech.harvest.core.types.event;

public abstract class EventArgument {
    private boolean cancelled = false;

    public abstract void call(EventListener eventListener);

    public boolean isCancelled() {
        return this.cancelled;
    }

    public final void cancel() {
        this.cancelled = true;
    }
}
