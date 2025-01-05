package tech.harvest.core.config;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FilenameUtils;

public final class ConfigManager {
    public static final File CONFIGS_DIR = new File("harvest-client", "configs");
    public static final String EXTENTION = ".json";
    private List<Config> contents = new ArrayList<>();

    public ConfigManager() {
        setContents(loadConfigs());
        CONFIGS_DIR.mkdirs();
    }

    private static ArrayList<Config> loadConfigs() {
        ArrayList<Config> loadedConfigs = new ArrayList<>();
        File[] files = CONFIGS_DIR.listFiles();
        if (files != null) {
            for (File file : files) {
                if (FilenameUtils.getExtension(file.getName()).equals("json")) {
                    loadedConfigs.add(new Config(FilenameUtils.removeExtension(file.getName())));
                }
            }
        }
        return loadedConfigs;
    }

    public boolean loadConfig(String configName) {
        Config config;
        if (configName == null || (config = findConfig(configName)) == null || !config.getFile().exists()) {
            return false;
        }
        try {
            FileReader reader = new FileReader(config.getFile());
            config.load(JsonParser.parseReader(reader).getAsJsonObject());
            reader.close();
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Config findConfig(String configName) {
        if (configName == null) {
            return null;
        }
        for (Config config : getContents()) {
            if (config.getName().equalsIgnoreCase(configName)) {
                return config;
            }
        }
        if (new File(CONFIGS_DIR, configName + ".json").exists()) {
            return new Config(configName);
        }
        return null;
    }

    public List<Config> getContents() {
        return this.contents;
    }

    public void setContents(ArrayList<Config> contents) {
        this.contents = contents;
    }

    public boolean saveConfig(String configName) {
        if (configName == null) {
            return false;
        }
        Config findConfig = findConfig(configName);
        Config config = findConfig;
        if (findConfig == null) {
            Config newConfig = new Config(configName);
            config = newConfig;
            getContents().add(newConfig);
        }
        String contentPrettyPrint = new GsonBuilder().setPrettyPrinting().create().toJson(config.save());
        try {
            FileWriter writer = new FileWriter(config.getFile());
            config.getFile().setWritable(true);
            writer.write(contentPrettyPrint);
            writer.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean deleteConfig(String configName) {
        Config config;
        if (configName == null || (config = findConfig(configName)) == null) {
            return false;
        }
        File f = config.getFile();
        getContents().remove(config);
        return f.exists() && f.delete();
    }
}
