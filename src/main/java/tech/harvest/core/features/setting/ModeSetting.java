package tech.harvest.core.features.setting;

import java.util.function.Consumer;
import tech.harvest.core.types.setting.Setting;
import tech.harvest.core.types.setting.Visibility;

public class ModeSetting extends Setting {
    private static final Consumer<String> EMPTY = v -> {
    };
    private final String[] option;
    private final Consumer<String> onSetting;
    public boolean expand;
    private String value;
    private int index = 0;

    public ModeSetting(String name, Visibility visibility, String[] option, Consumer<String> onSetting) {
        super(name, visibility);
        this.option = option;
        this.value = option[0];
        this.onSetting = onSetting;
    }

    public static ModeBuilder build() {
        return new ModeBuilder();
    }

    private static int indexOf(String a, String[] b) {
        for (int i = 0; i < b.length; i++) {
            if (b[i].equals(a)) {
                return i;
            }
        }
        return -1;
    }

    public String[] getOption() {
        return this.option;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
        this.index = indexOf(this.value, this.option);
        this.onSetting.accept(value);
    }

    public void setValue(int index) {
        this.index = index;
        this.value = this.option[index];
        this.onSetting.accept(this.value);
    }

    public int getIndex() {
        return this.index;
    }

    public void increment(boolean positive) {
        if (positive) {
            this.index = this.index < this.option.length - 1 ? this.index + 1 : 0;
        } else {
            this.index = this.index <= 0 ? this.option.length - 1 : this.index - 1;
        }
        setValue(this.index);
    }

    public static class ModeBuilder extends Setting.Builder<ModeSetting, ModeBuilder> {
        private int index = -1;
        private String value = null;
        private String[] option = new String[0];
        private Consumer<String> onSetting = ModeSetting.EMPTY;

        private ModeBuilder() {
        }

        public ModeBuilder value(String value) {
            this.value = value;
            return this;
        }

        public ModeBuilder index(int index) {
            this.index = index;
            return this;
        }

        public ModeBuilder option(String... option) {
            this.option = option;
            return this;
        }

        public ModeBuilder onSetting(Consumer<String> onSetting) {
            this.onSetting = onSetting;
            return this;
        }

        @Override
        public ModeSetting end() {
            if (this.index == -1 && this.value == null) {
                String[] strArr = this.option;
                this.index = 0;
                this.value = strArr[0];
            } else if (this.index != -1) {
                this.value = this.option[this.index];
            } else {
                this.index = ModeSetting.indexOf(this.value, this.option);
            }
            return new ModeSetting(this.name, this.visibility, this.option, this.onSetting);
        }
    }
}
