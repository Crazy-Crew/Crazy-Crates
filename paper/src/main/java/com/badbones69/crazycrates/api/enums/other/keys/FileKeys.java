package com.badbones69.crazycrates.api.enums.other.keys;

import com.badbones69.crazycrates.CrazyCrates;
import com.ryderbelserion.core.api.enums.FileType;
import com.ryderbelserion.core.api.exception.FusionException;
import com.ryderbelserion.paper.files.CustomFile;
import com.ryderbelserion.paper.files.FileManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;
import java.io.File;

public enum FileKeys {

    respin_gui(FileType.YAML, "respin-gui.yml", "guis"),

    crate_log(FileType.NONE, "crates.log", "logs"),
    key_log(FileType.NONE, "keys.log", "logs"),

    locations(FileType.YAML, "locations.yml"),
    data(FileType.YAML, "data.yml");


    private final FileType fileType;
    private final String fileName;
    private final String folder;

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final FileManager fileManager = this.plugin.getFileManager();

    /**
     * A constructor to build a file
     *
     * @param fileType the file type
     * @param fileName the name of the file
     * @param folder the folder
     */
    FileKeys(final FileType fileType, final String fileName, final String folder) {
        this.fileType = fileType;
        this.fileName = fileName;
        this.folder = folder;
    }

    /**
     * A constructor to build a file
     *
     * @param fileType the file type
     * @param fileName the name of the file
     */
    FileKeys(final FileType fileType, final String fileName) {
        this.fileType = fileType;
        this.fileName = fileName;
        this.folder = "";
    }

    public final YamlConfiguration getConfiguration() {
        @Nullable final CustomFile customFile = this.fileManager.getFile(this.fileName, this.fileType);

        if (customFile == null) {
            throw new FusionException("File configuration for " + this.fileName + " is null.");
        }

        return customFile.getConfiguration();
    }

    public FileType getFileType() {
        return this.fileType;
    }

    public void reload() {
        this.fileManager.addFile(this.fileName);
    }

    public void save() {
        this.fileManager.saveFile(this.fileName);
    }

    public final File getFile() {
        return new File(this.folder.isEmpty() ? this.plugin.getDataFolder() : new File(this.plugin.getDataFolder(), this.folder), this.fileName);
    }
}