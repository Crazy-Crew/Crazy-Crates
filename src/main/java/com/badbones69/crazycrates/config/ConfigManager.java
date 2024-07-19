package com.badbones69.crazycrates.config;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.resource.YamlFileResourceOptions;
import com.badbones69.crazycrates.config.migrate.ConfigMigration;
import com.badbones69.crazycrates.config.migrate.LocaleMigration;
import com.badbones69.crazycrates.config.impl.ConfigKeys;
import com.badbones69.crazycrates.config.impl.locale.CommandKeys;
import com.badbones69.crazycrates.config.impl.locale.CrateKeys;
import com.badbones69.crazycrates.config.impl.locale.ErrorKeys;
import com.badbones69.crazycrates.config.impl.locale.MiscKeys;
import com.badbones69.crazycrates.config.impl.locale.PlayerKeys;
import java.io.File;

public class ConfigManager {

    private static SettingsManager config;

    private static SettingsManager messages;

    /**
     * Loads configuration files.
     */
    public static void load(final File dataFolder) {
        YamlFileResourceOptions builder = YamlFileResourceOptions.builder().indentationSize(2).build();

        config = SettingsManagerBuilder
                .withYamlFile(new File(dataFolder, "config.yml"), builder)
                .migrationService(new ConfigMigration())
                .configurationData(ConfigKeys.class)
                .create();

        messages = SettingsManagerBuilder
                .withYamlFile(new File(dataFolder, "messages.yml"), builder)
                .migrationService(new LocaleMigration())
                .configurationData(MiscKeys.class, ErrorKeys.class, PlayerKeys.class, CrateKeys.class, CommandKeys.class)
                .create();
    }

    /**
     * Refreshes configuration files.
     */
    public static void refresh() {
        config.reload();
        messages.reload();
    }

    /**
     * @return gets config.yml
     */
    public static SettingsManager getConfig() {
        return config;
    }

    /**
     * @return gets messages.yml
     */
    public static SettingsManager getMessages() {
        return messages;
    }
}