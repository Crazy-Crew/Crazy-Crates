package com.badbones69.crazycrates.listeners.items;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.events.CrateInteractEvent;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;

public class ItemsAdderInteractListener implements Listener {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CrateManager crateManager = this.plugin.getCrateManager();

    @EventHandler
    public void onFurnitureInteractEvent(FurnitureInteractEvent event) {
        final Entity entity = event.getBukkitEntity();
        final Location location = entity.getLocation();

        final Player player = event.getPlayer();

        final EquipmentSlot slot = player.getActiveItemHand();

        if (this.crateManager.hasEditorCrate(player) && slot != EquipmentSlot.OFF_HAND) {
            this.crateManager.addCrateByLocation(player, location);

            event.setCancelled(true);

            return;
        }

        if (this.crateManager.isCrateLocation(location)) {
            new CrateInteractEvent(event, Action.RIGHT_CLICK_BLOCK, location).callEvent();
        }
    }

    @EventHandler
    public void onFurnitureBreakEvent(FurnitureBreakEvent event) {
        final Entity entity = event.getBukkitEntity();
        final Location location = entity.getLocation();

        final Player player = event.getPlayer();

        final EquipmentSlot slot = player.getActiveItemHand();

        if (this.crateManager.hasEditorCrate(player) && slot != EquipmentSlot.OFF_HAND) {
            this.crateManager.removeCrateByLocation(player, location);

            event.setCancelled(true);

            return;
        }

        if (this.crateManager.isCrateLocation(location)) {
            new CrateInteractEvent(event, Action.LEFT_CLICK_BLOCK, location).callEvent();
        }
    }
}