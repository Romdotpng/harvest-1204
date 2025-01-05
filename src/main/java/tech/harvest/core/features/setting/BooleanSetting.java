package tech.harvest.core.features.setting;

import java.util.function.Consumer;
import tech.harvest.core.types.setting.Setting;
import tech.harvest.core.types.setting.Visibility;

public class BooleanSetting extends Setting {
    private static final Consumer<Boolean> EMPTY = v -> {
    };
    private final Consumer<Boolean> onSetting;
    private boolean value;

    private BooleanSetting(String name, Visibility visibility, boolean value, Consumer<Boolean> onSetting) {
        super(name, visibility);
        this.onSetting = onSetting;
        this.value = value;
    }

    public static BooleanBuilder build() {
        return new BooleanBuilder();
    }

    public boolean getValue() {
        return this.value;
    }

    public void switchValue() {
        this.value = !this.value;
        this.onSetting.accept(this.value);
    }

    public static class BooleanBuilder extends Setting.Builder<BooleanSetting, BooleanBuilder> {
        private final Consumer<Boolean> onSetting = BooleanSetting.EMPTY;
        private boolean value = false;

        private BooleanBuilder() {
        }

        public BooleanBuilder value(boolean value) {
            this.value = value;
            return this;
        }

        @Override
        public BooleanSetting end() {
            return new BooleanSetting(this.name, this.visibility, this.value, this.onSetting);
        }
    }
}
