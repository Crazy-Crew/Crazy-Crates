package com.badbones69.crazycrates.configs.convert;

import com.badbones69.crazycrates.configs.Config;
import com.badbones69.crazycrates.utils.FileUtils;
import com.badbones69.crazycrates.utils.adventure.MsgWrapper;
import net.dehya.ruby.files.FileManager;
import org.simpleyaml.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class ConfigConversion {

    public void convertConfig(FileManager fileManager, Path directory) {
        double configVersion = 1.1;

        // The config.yml
        File input = new File(directory + "/config.yml");

        // The renamed file.
        File output = new File(directory + "/config-v1.yml");

        // The old configuration of config.yml
        YamlConfiguration yamlConfiguration = null;

        try {
            yamlConfiguration = YamlConfiguration.loadConfiguration(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (yamlConfiguration == null) return;

        if (yamlConfiguration.getString("Settings.Config-Version") == null && !output.exists()) {
            MsgWrapper.send("<#11e092>" + input.getName() + " <#E0115F>is up to date");
            return;
        }

        // Rename the file to the output file.
        if (input.renameTo(output)) MsgWrapper.send("<#E0115F>Renamed <#11e092>" + input.getName() + " <#E0115F>to <#11e092>" + output.getName() + ".");

        // The configuration of the output file.
        YamlConfiguration secondConfiguration = null;

        try {
            if (output.exists()) secondConfiguration = YamlConfiguration.loadConfiguration(output);
        } catch (IOException e) {
            MsgWrapper.send(e.getMessage());
        }

        if (secondConfiguration == null) return;

        // All the values of the old file.
        final String prefix = secondConfiguration.getString("Settings.Prefix");
        // final int version = secondConfiguration.getInt("Settings.Config-Version");
        final boolean updateChecker = secondConfiguration.getBoolean("Settings.Update-Checker");
        final boolean toggleMetrics = secondConfiguration.getBoolean("Settings.Toggle-Metrics");
        final boolean enableCrateMenu = secondConfiguration.getBoolean("Settings.Enable-Crate-Menu");
        final boolean crateLogFile = secondConfiguration.getBoolean("Settings.Crate-Actions.Log-File");
        final boolean crateLogConsole = secondConfiguration.getBoolean("Settings.Crate-Actions.Log-Console");

        final String invName = secondConfiguration.getString("Settings.InventoryName");
        final int invSize = secondConfiguration.getInt("Settings.InventorySize");

        final boolean knockBack = secondConfiguration.getBoolean("Settings.KnockBack");

        final boolean physAcceptsVirtual = secondConfiguration.getBoolean("Settings.Physical-Accepts-Virtual-Keys");
        final boolean physAcceptsPhys = secondConfiguration.getBoolean("Settings.Physical-Accepts-Physical-Keys");
        final boolean virtualAcceptsPhys = secondConfiguration.getBoolean("Settings.Virtual-Accepts-Physical-Keys");
        final boolean giveVirtualKeysInventoryMessage = secondConfiguration.getBoolean("Settings.Give-Virtual-Keys-When-Inventory-Full-Message");
        final boolean giveVirtualKeysInventory = secondConfiguration.getBoolean("Settings.Give-Virtual-Keys-When-Inventory-Full");

        // TODO() Move this to per crate.
        final String needKeySound = secondConfiguration.getString("Settings.Need-Key-Sound");

        // TODO() Move this to QuadCrate type.
        final int quadCrateTimer = secondConfiguration.getInt("Settings.QuadCrate.Timer");

        final List<String> disabledWorlds = secondConfiguration.getStringList("Settings.DisabledWorlds");

        // TODO() Move this to its own configuration file.
        final String menuName = secondConfiguration.getString("Settings.Preview.Buttons.Menu.Name");
        final String menuItem = secondConfiguration.getString("Settings.Preview.Buttons.Menu.Item");
        final List<String> menuLore = secondConfiguration.getStringList("Settings.Preview.Buttons.Menu.Lore");

        final String nextName = secondConfiguration.getString("Settings.Preview.Buttons.Next.Name");
        final String nextItem = secondConfiguration.getString("Settings.Preview.Buttons.Next.Item");
        //final List<String> nextLore = secondConfiguration.getStringList("Settings.Preview.Buttons.Next.Lore");

        final String backName = secondConfiguration.getString("Settings.Preview.Buttons.Back.Name");
        final String backItem = secondConfiguration.getString("Settings.Preview.Buttons.Back.Item");
        //final List<String> backLore = secondConfiguration.getStringList("Settings.Preview.Buttons.Back.Lore");

        final boolean fillerToggle = secondConfiguration.getBoolean("Settings.Filler.Toggle");
        final String fillerItem = secondConfiguration.getString("Settings.Filler.Item");
        final String fillerName = secondConfiguration.getString("Settings.Filler.Name");
        final List<String> fillerLore = secondConfiguration.getStringList("Settings.Filler.Lore");

        final List<String> guiCustomizer = secondConfiguration.getStringList("Settings.GUI-Customizer");

        org.simpleyaml.configuration.file.YamlFile configuration = Config.getConfiguration(fileManager);

        if (configuration == null) return;
        
        configuration.set("settings.prefix", prefix);
        configuration.set("settings.update-checker", updateChecker);
        configuration.set("settings.toggle-metrics", toggleMetrics);
        configuration.set("crate-settings.crate-actions.log-to-file", crateLogFile);
        configuration.set("crate-settings.crate-actions.log-to-console", crateLogConsole);

        configuration.set("crate-settings.preview-menu.toggle", enableCrateMenu);
        configuration.set("crate-settings.preview-menu.name", invName);
        configuration.set("crate-settings.preview-menu.size", invSize);

        configuration.set("crate-settings.knock-back", knockBack);
        configuration.set("crate-settings.keys.physical-accepts-virtual-keys", physAcceptsVirtual);
        configuration.set("crate-settings.keys.physical-accepts-physical-keys", physAcceptsPhys);
        configuration.set("crate-settings.keys.virtual-accepts-physical-keys", virtualAcceptsPhys);

        configuration.set("crate-settings.keys.inventory-not-empty.give-virtual-keys-message", giveVirtualKeysInventoryMessage);
        configuration.set("crate-settings.keys.inventory-not-empty.give-virtual-keys", giveVirtualKeysInventory);

        configuration.set("crate-settings.keys.key-sound.name", needKeySound);

        configuration.set("crate-settings.quad-crate.timer", quadCrateTimer);

        configuration.set("crate-settings.disabled-worlds.worlds", disabledWorlds);

        configuration.set("gui-settings.filler-items.toggle", fillerToggle);
        configuration.set("gui-settings.filler-items.item", fillerItem);
        configuration.set("gui-settings.filler-items.name", fillerName);

        configuration.set("gui-settings.filler-items.lore", fillerLore);

        configuration.set("gui-settings.buttons.menu.item", menuItem);
        configuration.set("gui-settings.buttons.menu.name", menuName);
        configuration.set("gui-settings.buttons.menu.lore", menuLore);

        configuration.set("gui-settings.buttons.next.item", nextItem);
        configuration.set("gui-settings.buttons.next.name", nextName);

        configuration.set("gui-settings.buttons.back.item", backItem);
        configuration.set("gui-settings.buttons.back.name", backName);

        configuration.set("gui-settings.customizer", guiCustomizer);

        FileUtils.copyFile(input, output, configuration);
    }
}