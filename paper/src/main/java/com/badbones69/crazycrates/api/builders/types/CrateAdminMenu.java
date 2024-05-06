package com.badbones69.crazycrates.api.builders.types;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.enums.Permissions;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.tasks.BukkitUserManager;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.ryderbelserion.vital.items.ItemBuilder;
import com.ryderbelserion.vital.util.MiscUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.api.builders.InventoryBuilder;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CrateAdminMenu extends InventoryBuilder {

    public CrateAdminMenu(@NotNull final Player player, final int size, @NotNull final String title) {
        super(player, size, title);
    }

    @Override
    public InventoryBuilder build() {
        Inventory inventory = getInventory();

        inventory.setItem(49, new ItemBuilder(Material.CHEST)
                .setDisplayName("<red>What is this menu?")
                .addDisplayLore("")
                .addDisplayLore("<light_purple>A cheat cheat menu of all your available keys.")
                .addDisplayLore("<bold><gray>Right click to get virtual keys.")
                .addDisplayLore("<bold><gray>Shift right click to get 8 virtual keys.")
                .addDisplayLore("<bold><gray>Left click to get physical keys.")
                .addDisplayLore("<bold><gray>Shift left click to get 8 physical keys.")
                .build());

        for (Crate crate : this.crateManager.getUsableCrates()) {
            if (inventory.firstEmpty() >= 0) inventory.setItem(inventory.firstEmpty(), crate.getKey(1, getPlayer()));
        }

        return this;
    }

    public static class CrateAdminListener implements Listener {

        private @NotNull final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

        private @NotNull final CrateManager crateManager = this.plugin.getCrateManager();

        private @NotNull final BukkitUserManager userManager = this.plugin.getUserManager();

        @EventHandler
        public void onInventoryClick(InventoryClickEvent event) {
            Inventory inventory = event.getInventory();

            if (!(inventory.getHolder(false) instanceof CrateAdminMenu holder)) return;

            event.setCancelled(true);

            Player player = holder.getPlayer();

            InventoryView view = holder.getView();

            if (event.getClickedInventory() != view.getTopInventory()) return;

            if (!Permissions.CRAZYCRATES_ACCESS.hasPermission(player)) {
                player.closeInventory(InventoryCloseEvent.Reason.CANT_USE);
                player.sendRichMessage(Messages.no_permission.getMessage(player));

                return;
            }

            ItemStack item = event.getCurrentItem();

            if (item == null || item.getType() == Material.AIR) return;

            if (!this.crateManager.isKey(item)) return;

            Crate crate = this.crateManager.getCrateFromKey(item);

            if (crate == null) return;

            String crateName = crate.getName();
            UUID uuid = player.getUniqueId();

            ClickType clickType = event.getClick();

            Map<String, String> placeholders = new ConcurrentHashMap<>();

            placeholders.put("{amount}", String.valueOf(1));
            placeholders.put("{key}", crate.getKeyName());

            switch (clickType) {
                case LEFT -> {
                    ItemStack key = crate.getKey(player);

                    player.getInventory().addItem(key);

                    player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1f, 1f);

                    placeholders.put("{keytype}", KeyType.physical_key.getFriendlyName());

                    player.sendActionBar(MiscUtil.parse(Messages.obtaining_keys.getMessage(player, placeholders)));
                }

                case SHIFT_LEFT -> {
                    ItemStack key = crate.getKey(8, player);

                    player.getInventory().addItem(key);

                    player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1f, 1f);

                    placeholders.put("{keytype}", KeyType.physical_key.getFriendlyName());
                    placeholders.put("{amount}", "8");

                    player.sendActionBar(MiscUtil.parse(Messages.obtaining_keys.getMessage(player, placeholders)));
                }

                case RIGHT -> {
                    this.userManager.addKeys(uuid, crateName, KeyType.virtual_key, 1);

                    player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1f, 1f);

                    placeholders.put("{keytype}", KeyType.physical_key.getFriendlyName());

                    player.sendActionBar(MiscUtil.parse(Messages.obtaining_keys.getMessage(player, placeholders)));
                }

                case SHIFT_RIGHT -> {
                    this.userManager.addKeys(uuid, crateName, KeyType.virtual_key, 8);

                    player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1f, 1f);

                    placeholders.put("{keytype}", KeyType.physical_key.getFriendlyName());
                    placeholders.put("{amount}", "8");

                    player.sendActionBar(MiscUtil.parse(Messages.obtaining_keys.getMessage(player, placeholders)));
                }
            }
        }
    }
}