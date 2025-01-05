package tech.harvest.core.types.setting;

public abstract class Setting {
    private static final Visibility VISIBLE = () -> true;
    private final String name;
    private final Visibility visibility;

    public Setting(String name, Visibility visibility) {
        this.name = name;
        this.visibility = visibility;
    }

    public final String getName() {
        return this.name;
    }

    public final boolean isVisible() {
        return this.visibility.isVisible();
    }

    public static abstract class Builder<T extends Setting, E extends Builder> {
        protected String name = "unnamed setting";
        protected Visibility visibility = Setting.VISIBLE;

        public abstract T end();

        public E name(String name) {
            this.name = name;
            return (E)this;
        }

        public E visibility(Visibility visibility) {
            this.visibility = visibility;
            return (E)this;
        }
    }
}
