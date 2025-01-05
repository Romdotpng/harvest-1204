package tech.harvest.core.features.setting;

import java.util.function.Consumer;
import tech.harvest.core.types.setting.Setting;
import tech.harvest.core.types.setting.Visibility;
import net.minecraft.util.math.MathHelper;

public class DoubleSetting extends Setting {
    private static final Consumer<Double> EMPTY = v -> {
    };
    private final double min;
    private final double max;
    private final double increment;
    private final String unit;
    private final Consumer<Double> onSetting;
    private double value;

    public double getMin() {
        return this.min;
    }

    public double getMax() {
        return this.max;
    }

    public double getIncrement() {
        return this.increment;
    }

    public String getUnit() {
        return this.unit;
    }

    public double getValue() {
        return this.value;
    }

    private DoubleSetting(String name, Visibility visibility, double min, double max, double increment, double value, String unit, Consumer<Double> onSetting) {
        super(name, visibility);
        this.min = min;
        this.max = max;
        this.increment = increment;
        this.unit = unit;
        this.value = value;
        this.onSetting = onSetting;
    }

    public static DoubleBuilder build() {
        return new DoubleBuilder();
    }

    public double getPercentage() {
        return (this.value - this.min) / (this.max - this.min);
    }

    public void incrementValue(boolean positive) {
        setValue(this.value + ((positive ? 1.0d : -1.0d) * this.increment));
    }

    public void setValue(double value) {
        this.value = checkValue(value);
        this.onSetting.accept(this.value);
    }

    private double checkValue(double value) {
        double precision = 1.0d / this.increment;
        return Math.round(MathHelper.clamp(value, this.min, this.max) * precision) / precision;
    }

    public void setValue(float posX, float width, float mouseX) {
        setValue((((mouseX - posX) * (this.max - this.min)) / width) + this.min);
    }

    public static class DoubleBuilder extends Setting.Builder<DoubleSetting, DoubleBuilder> {
        private double min = 0.0d;
        private double max = 10.0d;
        private double increment = 0.1d;
        private String unit = "";
        private double value = 5.0d;
        private Consumer<Double> onSetting = DoubleSetting.EMPTY;

        private DoubleBuilder() {
        }

        public DoubleBuilder range(double min, double max) {
            this.min = min;
            this.max = max;
            return this;
        }

        public DoubleBuilder increment(double increment) {
            this.increment = increment;
            return this;
        }

        public DoubleBuilder unit(String unit) {
            this.unit = unit;
            return this;
        }

        public DoubleBuilder value(double value) {
            this.value = value;
            return this;
        }

        public DoubleBuilder onSetting(Consumer<Double> onSetting) {
            this.onSetting = onSetting;
            return this;
        }

        @Override
        public DoubleSetting end() {
            return new DoubleSetting(this.name, this.visibility, this.min, this.max, this.increment, this.value, this.unit, this.onSetting);
        }
    }
}
