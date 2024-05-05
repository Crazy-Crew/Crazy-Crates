package com.badbones69.crazycrates.api.builders.types;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.enums.PersistentKeys;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.utils.ItemUtils;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import com.badbones69.crazycrates.tasks.BukkitUserManager;
import com.badbones69.crazycrates.tasks.InventoryManager;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.ryderbelserion.vital.items.ItemStackBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.platform.config.ConfigManager;
import us.crazycrew.crazycrates.platform.config.impl.ConfigKeys;
import com.badbones69.crazycrates.api.builders.InventoryBuilder;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import java.text.NumberFormat;
import java.util.List;
import java.util.UUID;

public class CrateMainMenu extends InventoryBuilder {

    private @NotNull final BukkitUserManager userManager = this.plugin.getUserManager();

    private @NotNull final SettingsManager config = ConfigManager.getConfig();

    public CrateMainMenu(@NotNull final Player player, final int size, @NotNull final String title) {
        super(player, size, title);
    }

    @Override
    public InventoryBuilder build() {
        final Inventory inventory = getInventory();

        final Player player = getPlayer();
        final UUID uuid = player.getUniqueId();

        if (this.config.getProperty(ConfigKeys.filler_toggle)) {
            final String id = this.config.getProperty(ConfigKeys.filler_item);
            final String name = this.config.getProperty(ConfigKeys.filler_name);
            final List<String> lore = this.config.getProperty(ConfigKeys.filler_lore);

            final ItemStackBuilder item = new ItemStackBuilder().withType(id, false).setDisplayName(name).setDisplayLore(lore).setPlayer(getPlayer()).build();

            for (int i = 0; i < getSize(); i++) {
                inventory.setItem(i, item);
            }
        }

        if (this.config.getProperty(ConfigKeys.gui_customizer_toggle)) {
            final List<String> customizer = this.config.getProperty(ConfigKeys.gui_customizer);

            if (!customizer.isEmpty()) {
                for (String custom : customizer) {
                    int slot = 0;
                    final ItemStackBuilder item = new ItemStackBuilder();

                    final String[] split = custom.split(", ");

                    for (String option : split) {
                        if (option.contains("item:")) item.withType(option.replace("item:", ""), false);

                        if (option.contains("name:")) {
                            option = option.replace("name:", "");

                            option = getCrates(option);

                            item.setDisplayName(option.replace("{player}", player.getName())); // ryder - this didn't need to be replaceAll, what logic would an owner need to put the player name twice in the displayName?
                        }

                        if (option.contains("lore:")) {
                            option = option.replace("lore:", "");
                            final String[] lore = option.split(",");

                            for (String line : lore) {
                                option = getCrates(option);

                                item.addDisplayLore(option.replaceAll("\\{player}", player.getName()));
                            }
                        }

                        if (option.contains("glowing:")) item.setGlowing(option.replace("glowing:", "").equalsIgnoreCase("true"));

                        //todo() test the new options.
                        //if (option.contains("hdb:")) item.setSkull(option.replace("hdb:", ""), HeadDatabaseListener.getHeads());

                        if (option.contains("player:")) item.setPlayer(option.replace("{player}", player.getName())); // ryder - this doesn't need to be replaceAll, what logic would an owner need to put the player name twice in the displayName?

                        if (option.contains("slot:")) slot = Integer.parseInt(option.replace("slot:", ""));

                        if (option.contains("unbreakable-item:")) item.setUnbreakable(option.replace("unbreakable-item:", "").equalsIgnoreCase("true"));

                        if (option.contains("hide-item-flags:")) item.setHiddenItemFlags(option.replace("hide-item-flags:", "").equalsIgnoreCase("true"));
                    }

                    if (slot > getSize()) continue;

                    slot--;

                    inventory.setItem(slot, item.setPlayer(player).build());
                }
            }
        }

        for (Crate crate : this.crateManager.getUsableCrates()) {
            final FileConfiguration file = crate.getFile();

            if (file != null) {
                final ConfigurationSection section = file.getConfigurationSection("Crate");

                if (section != null) {
                    if (section.getBoolean("InGUI", false)) {
                        final String crateName = crate.getName();

                        int slot = section.getInt("Slot");

                        if (slot > getSize()) continue;

                        slot--;

                        final ItemStackBuilder builder = new ItemStackBuilder()
                                .addPlaceholder("%keys%", NumberFormat.getNumberInstance().format(this.userManager.getVirtualKeys(uuid, crateName)), true)
                                .addPlaceholder("%keys_physical%", NumberFormat.getNumberInstance().format(this.userManager.getPhysicalKeys(uuid, crateName)), true)
                                .addPlaceholder("%keys_total%", NumberFormat.getNumberInstance().format(this.userManager.getTotalKeys(uuid, crateName)), true)
                                .addPlaceholder("%crate_opened%", NumberFormat.getNumberInstance().format(this.userManager.getCrateOpened(uuid, crateName)), true)
                                .addPlaceholder("%player%", getPlayer().getName(), true)
                                .setPersistentString(PersistentKeys.crate_key.getNamespacedKey(), crate.getName()).setDisplayName(section.getString("CrateName", crateName))
                                .withType(section.getString("Item", "chest"), false);

                        inventory.setItem(slot, ItemUtils.getItem(section, builder, player).build());
                    }
                }
            }
        }

        return this;
    }

    private @NotNull String getCrates(@NotNull String option) {
        if (option.isEmpty()) return "";

        final UUID uuid = getPlayer().getUniqueId();

        for (Crate crate : this.crateManager.getUsableCrates()) {
            final String crateName = crate.getName();
            final String lowerCase = crateName.toLowerCase();

            option = option.replaceAll("%" + lowerCase + "}", this.userManager.getVirtualKeys(uuid, crateName) + "")
                    .replaceAll("%" + lowerCase + "_physical%", this.userManager.getPhysicalKeys(uuid, crateName) + "")
                    .replaceAll("%" + lowerCase + "_total%", this.userManager.getTotalKeys(uuid, crateName) + "")
                    .replaceAll("%" + lowerCase + "_opened%", this.userManager.getCrateOpened(uuid, crateName) + "");
        }

        return option;
    }

    public static class CrateMenuListener implements Listener {

        private @NotNull final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

        private @NotNull final InventoryManager inventoryManager = this.plugin.getInventoryManager();

        private @NotNull final SettingsManager config = ConfigManager.getConfig();

        private @NotNull final CrateManager crateManager = this.plugin.getCrateManager();

        private @NotNull final BukkitUserManager userManager = this.plugin.getUserManager();

        @EventHandler
        public void onInventoryClick(InventoryClickEvent event) {
            final Inventory inventory = event.getInventory();

            if (!(inventory.getHolder(false) instanceof CrateMainMenu holder)) return;

            event.setCancelled(true);

            final Player player = holder.getPlayer();
            final Location location = player.getLocation();
            final String playerWorld = player.getWorld().getName();
            final UUID uuid = player.getUniqueId();

            final ItemStack item = event.getCurrentItem();

            if (item == null || item.getType() == Material.AIR) return;

            if (!item.hasItemMeta()) return;

            final Crate crate = this.crateManager.getCrateFromName(ItemUtils.getKey(item.getItemMeta()));

            if (crate == null) return;

            final String crateName = crate.getName();

            if (event.getAction() == InventoryAction.PICKUP_HALF) { // Right-clicked the item
                if (crate.isPreviewEnabled()) {
                    crate.playSound(player, location, "click-sound", "ui.button.click", SoundCategory.PLAYERS);

                    player.closeInventory();

                    this.inventoryManager.addViewer(player);
                    this.inventoryManager.openNewCratePreview(player, crate);
                } else {
                    player.sendRichMessage(Messages.preview_disabled.getMessage(player, "{crate}", crateName));
                }

                return;
            }

            if (this.crateManager.isInOpeningList(player)) {
                player.sendRichMessage(Messages.already_opening_crate.getMessage(player, "{crate}", crateName));

                return;
            }

            boolean hasKey = false;
            KeyType keyType = KeyType.virtual_key;

            if (this.userManager.getVirtualKeys(uuid, crateName) >= 1) {
                hasKey = true;
            } else {
                if (this.config.getProperty(ConfigKeys.virtual_accepts_physical_keys) && this.userManager.hasPhysicalKey(uuid, crateName, false)) {
                    hasKey = true;
                    keyType = KeyType.physical_key;
                }
            }

            if (!hasKey) {
                if (this.config.getProperty(ConfigKeys.need_key_sound_toggle)) {
                    player.playSound(player.getLocation(), Sound.valueOf(this.config.getProperty(ConfigKeys.need_key_sound)), SoundCategory.PLAYERS, 1f, 1f);
                }

                player.sendRichMessage(Messages.no_virtual_key.getMessage(player, "{crate}", crateName));

                return;
            }

            for (String world : this.config.getProperty(ConfigKeys.disabled_worlds)) {
                if (world.equalsIgnoreCase(playerWorld)) {
                    player.sendRichMessage(Messages.world_disabled.getMessage(player, "{world}", playerWorld));

                    return;
                }
            }

            if (MiscUtils.isInventoryFull(player)) {
                player.sendRichMessage(Messages.inventory_not_empty.getMessage(player, "{crate}", crateName));

                return;
            }

            this.crateManager.openCrate(player, crate, keyType, location, true, false);
        }
    }
}