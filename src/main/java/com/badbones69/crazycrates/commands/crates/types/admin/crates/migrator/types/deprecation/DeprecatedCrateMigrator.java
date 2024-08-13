package com.badbones69.crazycrates.commands.crates.types.admin.crates.migrator.types.deprecation;

import com.badbones69.crazycrates.commands.crates.types.admin.crates.migrator.ICrateMigrator;
import com.badbones69.crazycrates.commands.crates.types.admin.crates.migrator.enums.MigrationType;
import com.ryderbelserion.vital.paper.files.config.CustomFile;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DeprecatedCrateMigrator extends ICrateMigrator {

    public DeprecatedCrateMigrator(final CommandSender sender, final MigrationType type) {
        super(sender, type);
    }

    @Override
    public void run() {
        final Set<CustomFile> customFiles = this.plugin.getFileManager().getCustomFiles();

        final List<String> failed = new ArrayList<>();
        final List<String> success = new ArrayList<>();

        customFiles.forEach(customFile -> {
            try {
                final YamlConfiguration configuration = customFile.getConfiguration();

                final ConfigurationSection prizes = configuration.getConfigurationSection("Crate.Prizes");

                if (prizes != null) {
                    prizes.getKeys(false).forEach(value -> {
                        if (configuration.contains("Crate.Prizes." + value + ".Lore")) {
                            configuration.set("Crate.Prizes." + value + ".DisplayLore", configuration.getStringList("Crate.Prizes." + value + ".Lore"));

                            configuration.set("Crate.Prizes." + value + ".Lore", null);
                        }

                        if (configuration.contains("Crate.Prizes." + value + ".Patterns")) {
                            configuration.set("Crate.Prizes." + value + ".DisplayPatterns", configuration.getStringList("Crate.Prizes." + value + ".Patterns"));

                            configuration.set("Crate.Prizes." + value + ".Patterns", null);
                        }
                    });

                    customFile.save();
                }

                success.add("<green>" + customFile.getStrippedName());
            } catch (Exception exception) {
                failed.add("<red>" + customFile.getStrippedName());
            }
        });

        final int convertedCrates = failed.size();
        final int failedCrates = success.size();

        sendMessage(new ArrayList<>(failedCrates + convertedCrates) {{
            addAll(failed);
            addAll(success);
        }}, failedCrates, convertedCrates);

        this.fileManager.init();
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