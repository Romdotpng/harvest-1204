package tech.harvest.core.config;

import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;

import tech.harvest.HarvestClient;

public final class Config {
    private final String name;
    private final File file;

    public Config(String name) {
        this.name = name;
        this.file = new File(ConfigManager.CONFIGS_DIR, name + ".json");
        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (IOException e) {
            }
        }
    }

    public File getFile() {
        return this.file;
    }

    public String getName() {
        return this.name;
    }

    public JsonObject save() {
        JsonObject jsonObject = new JsonObject();
        JsonObject modulesObject = new JsonObject();
        HarvestClient.getModuleManager().getModules().forEach(m -> {
            modulesObject.add(m.getName(), m.save());
        });
        jsonObject.add("Modules", modulesObject);
        return jsonObject;
    }

    public void load(JsonObject object) {
        if (object.has("Modules")) {
            JsonObject modulesObject = object.getAsJsonObject("Modules");
            HarvestClient.getModuleManager().getModules().forEach(m -> {
                if (modulesObject.has(m.getName())) {
                    m.load(modulesObject.getAsJsonObject(m.getName()));
                }
            });
        }
    }
}
