package tech.harvest.core.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import tech.harvest.MCHook;

public class FileFactory extends Container<IFile> implements MCHook {
    private final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
    private File root;

    public void add(IFile item) {
        item.setFile(this.root);
        super.add(item);
    }

    public void saveFile(Class<? extends IFile> iFile) {
        IFile file = findByClass(iFile);
        if (file != null) {
            file.save(this.GSON);
        }
    }

    public void loadFile(Class<? extends IFile> iFile) {
        IFile file = findByClass(iFile);
        if (file != null) {
            file.load(this.GSON);
        }
    }

    public void save() {
        forEach(file -> {
            file.save(this.GSON);
        });
    }

    public void load() {
        forEach(file -> {
            file.load(this.GSON);
        });
    }

    public void setupRoot(String name) {
        this.root = new File(mc.runDirectory, name);
        if (!this.root.exists() && !this.root.mkdirs()) {
            System.out.println("Failed to create the root folder \"" + this.root.getPath() + "\".");
        }
    }
}
