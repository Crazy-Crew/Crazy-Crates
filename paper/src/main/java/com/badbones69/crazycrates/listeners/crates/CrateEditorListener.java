package com.badbones69.crazycrates.listeners.crates;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.objects.crates.CrateLocation;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class CrateEditorListener implements Listener {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CrateManager crateManager = this.plugin.getCrateManager();

    @EventHandler
    public void onEditorClick(final PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;

        final Block block = event.getClickedBlock();

        if (block == null || block.getType().isAir()) return;

        final Player player = event.getPlayer();

        final Action action = event.getAction();

        if (!this.crateManager.hasEditorCrate(player)) return;

        if (!player.hasPermission("crazycrates.editor")) {
            this.crateManager.removeEditorCrate(player);

            Messages.force_editor_exit.sendMessage(player, "{reason}", "Lacking permission crazycrates.editor");

            return;
        }

        final Location location = block.getLocation();

        switch (action) {
            case RIGHT_CLICK_BLOCK -> this.crateManager.addCrateByLocation(player, location);
            case LEFT_CLICK_BLOCK -> this.crateManager.removeCrateByLocation(player, location);
        }

        event.setUseInteractedBlock(Event.Result.DENY);
        event.setUseItemInHand(Event.Result.DENY);
    }
}