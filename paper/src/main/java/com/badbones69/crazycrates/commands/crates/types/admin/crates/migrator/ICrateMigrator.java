package com.badbones69.crazycrates.commands.crates.types.admin.crates.migrator;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.utils.ItemUtils;
import com.badbones69.crazycrates.commands.crates.types.admin.crates.migrator.enums.MigrationType;
import com.badbones69.crazycrates.config.ConfigManager;
import com.badbones69.crazycrates.config.impl.ConfigKeys;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.ryderbelserion.vital.paper.api.files.CustomFile;
import com.ryderbelserion.vital.paper.api.files.FileManager;
import com.ryderbelserion.vital.common.utils.StringUtil;
import com.ryderbelserion.vital.paper.util.ItemUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public abstract class ICrateMigrator {

    protected final CrazyCrates plugin = CrazyCrates.getPlugin();

    protected final CrateManager crateManager = this.plugin.getCrateManager();

    protected final SettingsManager config = ConfigManager.getConfig();

    protected final FileManager fileManager = this.plugin.getFileManager();

    protected final CommandSender sender;

    protected final MigrationType type;

    protected final long startTime;

    public ICrateMigrator(final CommandSender sender, MigrationType type) {
        this.startTime = System.nanoTime();

        this.sender = sender;
        this.type = type;
    }

    protected String crateName;

    public ICrateMigrator(final CommandSender sender, final MigrationType type, final String crateName) {
        this(sender, type);

        this.crateName = crateName;
    }

    public abstract void run();

    public abstract <T> void set(ConfigurationSection section, String path, T value);

    public abstract File getCratesDirectory();

    public void sendMessage(List<String> files, final int success, final int failed) {
        Messages.successfully_migrated.sendMessage(this.sender, new HashMap<>() {{
            if (files.size() > 1) {
                put("{files}", StringUtils.chomp(StringUtil.convertList(files)));
            } else {
                put("{files}", files.getFirst());
            }

            put("{succeeded_amount}", String.valueOf(success));
            put("{failed_amount}", String.valueOf(failed));
            put("{type}", type.getName());
            put("{time}", time());
        }});
    }

    public void migrate(final CustomFile customFile, final String crateName) {
        final YamlConfiguration configuration = customFile.getConfiguration();

        if (configuration == null) return;

        final ConfigurationSection crate = configuration.getConfigurationSection("Crate");

        if (crate == null) {
            Messages.error_migrating.sendMessage(sender, new HashMap<>() {{
                put("{file}", crateName.isEmpty() ? customFile.getCleanName() : crateName);
                put("{type}", type.getName());
                put("{reason}", "File could not be found in our data, likely invalid yml file that didn't load properly.");
            }});

            return;
        }

        set(crate, "Item", crate.getString("Item", "diamond").toLowerCase());
        set(crate, "Preview.Glass.Item", crate.getString("Preview.Glass.Item", "gray_stained_glass_pane").toLowerCase());
        set(crate, "PhysicalKey.Item", crate.getString("PhysicalKey.Item", "lime_dye").toLowerCase());

        final ConfigurationSection prizes = crate.getConfigurationSection("Prizes");

        if (prizes != null) {
            prizes.getKeys(false).forEach(key -> {
                final ConfigurationSection prize = prizes.getConfigurationSection(key);

                if (prize == null) return;

                if (prize.contains("DisplayItem")) {
                    set(prize, "DisplayItem", prize.getString("DisplayItem", "red_terracotta").toLowerCase());
                }

                if (prize.contains("DisplayTrim")) {
                    set(prize, "DisplayTrim.Material", prize.getString("DisplayTrim.Material", "quartz").toLowerCase());
                    set(prize, "DisplayTrim.Pattern", prize.getString("DisplayTrim.Pattern", "sentry").toLowerCase());
                }

                if (prize.contains("DisplayEnchantments")) {
                    List<String> enchants = new ArrayList<>() {{
                        prize.getStringList("DisplayEnchantments").forEach(enchant -> add(ItemUtils.getEnchant(enchant)));
                    }};

                    set(prize, "DisplayEnchantments", enchants);
                }
            });
        }

        customFile.save();
        customFile.load();
    }

    public final String time() {
        final double time = (double) (System.nanoTime() - this.startTime) / 1.0E9D;

        return String.format(Locale.ROOT, "%.3fs", time);
    }
}