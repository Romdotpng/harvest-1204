package tech.harvest.core.features.event;

import tech.harvest.core.types.event.EventArgument;
import tech.harvest.core.types.event.EventListener;

public class KeyPressEvent extends EventArgument {
    private final int keyCode;
    private final int scanCode;

    public KeyPressEvent(int keyCode, int scanCode) {
        this.keyCode = keyCode;
        this.scanCode = scanCode;
    }

    public int getKeyCode() {
        return this.keyCode;
    }

    public int getScanCode() {
        return this.scanCode;
    }

    @Override
    public void call(EventListener listener) {
        listener.onKeyPress(this);
    }
}
