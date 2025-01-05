package tech.harvest.core.types.module;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import tech.harvest.HarvestClient;
import tech.harvest.MCHook;
import tech.harvest.core.features.setting.BooleanSetting;
import tech.harvest.core.features.setting.DoubleSetting;
import tech.harvest.core.features.setting.ModeSetting;
import tech.harvest.core.types.event.EventListener;
import tech.harvest.core.types.setting.Setting;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public abstract class Module implements MCHook, EventListener {
    private final String name;
    private final String description;
    private final ModuleCategory category;
    private final List<Setting> settings = new ArrayList<>();
    private int keyCode = -1;
    private boolean enabled;
    @Nullable
    protected String prefix;

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public ModuleCategory getCategory() {
        return this.category;
    }

    public List<Setting> getSettings() {
        return this.settings;
    }

    public int getKeyCode() {
        return this.keyCode;
    }

    @Nullable
    public String getPrefix() {
        return this.prefix;
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public Module(String name, String description, ModuleCategory category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public final void load(JsonObject object) {
        if (object.has("toggled")) {
            boolean a = object.get("toggled").getAsBoolean();
            if (a != isEnabled()) {
                toggle();
            }
        }
        if (object.has("key")) {
            this.keyCode = object.get("key").getAsInt();
        }
        if (object.has("Properties") && !this.settings.isEmpty()) {
            JsonObject propertiesObject = object.getAsJsonObject("Properties");
            for (Setting property : this.settings) {
                if (propertiesObject.has(property.getName())) {
                    if (property instanceof DoubleSetting) {
                        ((DoubleSetting) property).setValue(propertiesObject.get(property.getName()).getAsDouble());
                    } else if (property instanceof BooleanSetting) {
                        boolean b = propertiesObject.get(property.getName()).getAsBoolean();
                        if (b != ((BooleanSetting) property).getValue()) {
                            ((BooleanSetting) property).switchValue();
                        }
                    } else if (property instanceof ModeSetting) {
                        ((ModeSetting) property).setValue(propertiesObject.get(property.getName()).getAsString());
                    }
                }
            }
        }
    }

    public final void toggle() {
        this.enabled = !this.enabled;
        if (this.enabled) {
            onEnable();
            mc.world.playSound(mc.player, mc.player.getBlockPos(), SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.VOICE, 1.0f, 1.0f);
            HarvestClient.getEventManager().register(this);
            return;
        }
        mc.world.playSound(mc.player, mc.player.getBlockPos(), SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.VOICE, 1.0f, 0.8f);
        onDisable();
        HarvestClient.getEventManager().unregister(this);
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            if (this.enabled) {
                onEnable();
                HarvestClient.getEventManager().register(this);
                return;
            }
            onDisable();
            HarvestClient.getEventManager().unregister(this);
        }
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public JsonObject save() {
        JsonObject object = new JsonObject();
        object.addProperty("toggled", isEnabled());
        object.addProperty("key", this.keyCode);
        if (!this.settings.isEmpty()) {
            JsonObject propertiesObject = new JsonObject();
            for (Setting property : this.settings) {
                if (property instanceof DoubleSetting) {
                    propertiesObject.addProperty(property.getName(), ((DoubleSetting) property).getValue());
                } else if (property instanceof ModeSetting) {
                    propertiesObject.addProperty(property.getName(), ((ModeSetting) property).getValue());
                } else if (property instanceof BooleanSetting) {
                    propertiesObject.addProperty(property.getName(), ((BooleanSetting) property).getValue());
                }
            }
            object.add("Properties", propertiesObject);
        }
        return object;
    }
}
