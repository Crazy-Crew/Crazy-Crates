package com.badbones69.crazycrates.commands.crates.types.admin.crates.migrator.types;

import com.badbones69.crazycrates.commands.crates.types.admin.crates.migrator.ICrateMigrator;
import com.badbones69.crazycrates.commands.crates.types.admin.crates.migrator.enums.MigrationType;
import com.ryderbelserion.vital.paper.files.config.CustomFile;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MojangMappedMigratorMultiple extends ICrateMigrator {

    public MojangMappedMigratorMultiple(final CommandSender sender, final MigrationType type) {
        super(sender, type);
    }

    @Override
    public void run() {
        final Set<CustomFile> customFiles = this.plugin.getFileManager().getCustomFiles();

        final List<String> failed = new ArrayList<>();
        final List<String> success = new ArrayList<>();

        customFiles.forEach(customFile -> {
            try {
                migrate(customFile, "");

                success.add("<green>" + customFile.getStrippedName());
            } catch (Exception exception) {
                failed.add("<red>" + customFile.getStrippedName());
            }
        });

        // reload crates
        this.crateManager.loadHolograms();
        this.crateManager.loadCrates();

        final int failedCrates = failed.size();
        final int convertedCrates = success.size();

        sendMessage(new ArrayList<>(failedCrates + convertedCrates) {{
            addAll(failed);
            addAll(success);
        }}, failedCrates, convertedCrates);
    }

    @Override
    public <T> void set(ConfigurationSection section, String path, T value) {
        section.set(path, value);
    }

    @Override
    public final File getCratesDirectory() {
        return new File(this.plugin.getDataFolder(), "crates");
    }
}