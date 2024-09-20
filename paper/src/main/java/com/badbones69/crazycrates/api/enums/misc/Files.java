package com.badbones69.crazycrates.api.enums.misc;

import com.badbones69.crazycrates.CrazyCrates;
import com.ryderbelserion.vital.paper.api.files.FileManager;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;

public enum Files {

    locations("locations.yml"),
    respin_gui("respin-gui.yml"),
    data("data.yml");

    private final String fileName;

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final FileManager fileManager = this.plugin.getFileManager();

    /**
     * A constructor to build a file
     *
     * @param fileName the name of the file
     */
    Files(final String fileName) {
        this.fileName = fileName;
    }

    public final YamlConfiguration getConfiguration() {
        if (this.fileName.equalsIgnoreCase("respin-gui.yml")) {
            return this.fileManager.getFile(this.fileName, true).getConfiguration();
        }

        return this.fileManager.getFile(this.fileName).getConfiguration();
    }

    public void reload() {
        this.fileManager.addFile(new File(this.plugin.getDataFolder(), this.fileName));
    }

    public void save() {
        this.fileManager.saveFile(this.fileName);
    }
}