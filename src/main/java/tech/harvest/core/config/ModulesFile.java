package tech.harvest.core.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.File;

import tech.harvest.HarvestClient;

public class ModulesFile implements IFile {
    private File file;

    @Override
    public void save(Gson gson) {
        JsonObject object = new JsonObject();
        JsonObject modulesObject = new JsonObject();
        HarvestClient.getModuleManager().getModules().forEach(m -> {
            modulesObject.add(m.getName(), m.save());
        });
        object.add("Modules", modulesObject);
        writeFile(gson.toJson(object), this.file);
    }

    @Override
    public void load(Gson gson) {
        if (this.file.exists()) {
            JsonObject object = gson.fromJson(readFile(this.file), JsonObject.class);
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

    @Override
    public void setFile(File root) {
        this.file = new File(root, "/modules.json");
    }
}
