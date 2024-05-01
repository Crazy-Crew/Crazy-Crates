package com.badbones69.crazycrates.api.builders.types;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.builders.InventoryBuilder;
import com.badbones69.crazycrates.api.enums.PersistentKeys;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Tier;
import org.bukkit.plugin.java.JavaPlugin;
import us.crazycrew.crazycrates.platform.config.ConfigManager;
import us.crazycrew.crazycrates.platform.config.impl.ConfigKeys;
import com.badbones69.crazycrates.tasks.InventoryManager;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
import java.util.List;

public class CrateTierMenu extends InventoryBuilder {

    private final @NotNull InventoryManager inventoryManager = this.plugin.getInventoryManager();

    private final @NotNull SettingsManager config = ConfigManager.getConfig();

    public CrateTierMenu(List<Tier> tiers, Crate crate, Player player, int size, String title) {
        super(tiers, crate, player, size, title);
    }

    @Override
    public InventoryBuilder build() {
        setDefaultItems();

        return this;
    }

    private void setDefaultItems() {
        Inventory inventory = getInventory();
        Player player = getPlayer();
        Crate crate = getCrate();

        getTiers().forEach(tier -> inventory.setItem(tier.getSlot(), tier.getTierItem(player)));

        if (crate.isPreviewTierBorderToggle()) {
            List<Integer> borderItems = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8);

            for (int item : borderItems) { // Top border slots
                inventory.setItem(item, crate.getPreviewTierBorderItem().setTarget(player).build());
            }

            borderItems.replaceAll(crate::getAbsolutePreviewItemPosition);

            for (int item : borderItems) { // Bottom border slots
                inventory.setItem(item, crate.getPreviewTierBorderItem().setTarget(player).build());
            }
        }

        if (this.inventoryManager.inCratePreview(player) && this.config.getProperty(ConfigKeys.enable_crate_menu)) inventory.setItem(crate.getAbsolutePreviewItemPosition(4), this.inventoryManager.getMenuButton(player));
    }

    public static class CrateTierListener implements Listener {

        private final @NotNull CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

        private final @NotNull InventoryManager inventoryManager = this.plugin.getInventoryManager();

        private final @NotNull SettingsManager config = ConfigManager.getConfig();

        @EventHandler
        public void onInventoryClick(InventoryClickEvent event) {
            Inventory inventory = event.getInventory();

            if (!(inventory.getHolder(false) instanceof CrateTierMenu holder)) return;

            event.setCancelled(true);

            Player player = holder.getPlayer();

            ItemStack item = event.getCurrentItem();

            if (item == null || item.getType() == Material.AIR) return;

            if (!item.hasItemMeta()) return;

            Crate crate = this.inventoryManager.getCratePreview(player);

            if (crate == null) return;

            ItemMeta itemMeta = item.getItemMeta();

            PersistentDataContainer container = itemMeta.getPersistentDataContainer();

            if (container.has(PersistentKeys.main_menu_button.getNamespacedKey()) && this.config.getProperty(ConfigKeys.enable_crate_menu)) {
                if (this.inventoryManager.inCratePreview(player)) {
                    if (holder.overrideMenu()) return;

                    crate.playSound(player, player.getLocation(), "click-sound", "ui.button.click", SoundCategory.PLAYERS);

                    this.inventoryManager.removeViewer(player);
                    this.inventoryManager.closeCratePreview(player);

                    CrateMainMenu crateMainMenu = new CrateMainMenu(player, this.config.getProperty(ConfigKeys.inventory_size), this.config.getProperty(ConfigKeys.inventory_name));

                    player.openInventory(crateMainMenu.build().getInventory());
                }

                return;
            }

            if (container.has(PersistentKeys.preview_tier_button.getNamespacedKey())) {
                crate.playSound(player, player.getLocation(), "click-sound", "ui.button.click", SoundCategory.PLAYERS);

                String tierName = container.get(PersistentKeys.preview_tier_button.getNamespacedKey(), PersistentDataType.STRING);

                Tier tier = crate.getTier(tierName);

                Inventory cratePreviewMenu = crate.getPreview(player, this.inventoryManager.getPage(player), true, tier);

                player.openInventory(cratePreviewMenu);
            }
        }
    }
}