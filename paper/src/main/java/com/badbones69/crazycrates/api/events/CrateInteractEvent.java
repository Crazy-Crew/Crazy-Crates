package com.badbones69.crazycrates.api.events;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.objects.crates.CrateLocation;
import com.badbones69.crazycrates.common.config.ConfigManager;
import com.badbones69.crazycrates.common.config.impl.ConfigKeys;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.api.events.furniture.NexoFurnitureBreakEvent;
import com.nexomc.nexo.api.events.furniture.NexoFurnitureInteractEvent;
import com.ryderbelserion.vital.paper.api.enums.Support;
import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import dev.lone.itemsadder.api.ItemsAdder;
import io.th0rgal.oraxen.api.OraxenFurniture;
import io.th0rgal.oraxen.api.events.furniture.OraxenFurnitureBreakEvent;
import io.th0rgal.oraxen.api.events.furniture.OraxenFurnitureInteractEvent;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

public class CrateInteractEvent extends Event implements Cancellable {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CrateManager crateManager = this.plugin.getCrateManager();

    private NexoFurnitureInteractEvent nexoInteractEvent;
    private NexoFurnitureBreakEvent nexoBreakEvent;

    private OraxenFurnitureInteractEvent oraxenInteractEvent;
    private OraxenFurnitureBreakEvent oraxenBreakEvent;

    private FurnitureInteractEvent itemsAdderInteractEvent;
    private FurnitureBreakEvent itemsAdderBreakEvent;

    private PlayerInteractEvent paperEvent;

    private CrateLocation crateLocation = null;
    private boolean isCancelled;

    private final EquipmentSlot slot;
    private final Location location;
    private final Player player;
    private final Action action;

    public CrateInteractEvent(@NotNull final OraxenFurnitureInteractEvent interactEvent, @NotNull final Action action, @NotNull final Location location) {
        this.oraxenInteractEvent = interactEvent;

        this.player = this.oraxenInteractEvent.getPlayer();
        this.slot = this.oraxenInteractEvent.getHand();
        this.location = location;
        this.action = action;

        setCancelled(this.slot == EquipmentSlot.OFF_HAND);

        if (!isCancelled()) {
            this.crateLocation = this.crateManager.getCrateLocation(this.location);
        }
    }

    public CrateInteractEvent(@NotNull final OraxenFurnitureBreakEvent breakEvent, @NotNull final Action action, @NotNull final Location location) {
        this.oraxenBreakEvent = breakEvent;

        this.player = this.oraxenBreakEvent.getPlayer();
        this.slot = this.player.getActiveItemHand();
        this.location = location;
        this.action = action;

        setCancelled(this.slot == EquipmentSlot.OFF_HAND);

        if (!isCancelled()) {
            this.crateLocation = this.crateManager.getCrateLocation(this.location);
        }
    }

    public CrateInteractEvent(@NotNull final FurnitureInteractEvent interactEvent, @NotNull final Action action, @NotNull final Location location) {
        this.itemsAdderInteractEvent = interactEvent;

        this.player = this.itemsAdderInteractEvent.getPlayer();
        this.slot = this.player.getActiveItemHand();
        this.location = location;
        this.action = action;

        setCancelled(this.slot == EquipmentSlot.OFF_HAND);

        if (!isCancelled()) {
            this.crateLocation = this.crateManager.getCrateLocation(this.location);
        }
    }

    public CrateInteractEvent(@NotNull final FurnitureBreakEvent breakEvent, @NotNull final Action action, @NotNull final Location location) {
        this.itemsAdderBreakEvent = breakEvent;

        this.player = this.itemsAdderBreakEvent.getPlayer();
        this.slot = this.player.getActiveItemHand();
        this.location = location;
        this.action = action;

        setCancelled(this.slot == EquipmentSlot.OFF_HAND);

        if (!isCancelled()) {
            this.crateLocation = this.crateManager.getCrateLocation(this.location);
        }
    }

    public CrateInteractEvent(@NotNull final NexoFurnitureInteractEvent interactEvent, @NotNull final Action action, @NotNull final Location location) {
        this.nexoInteractEvent = interactEvent;

        this.player = this.nexoInteractEvent.getPlayer();
        this.slot = this.nexoInteractEvent.getHand();
        this.location = location;
        this.action = action;

        setCancelled(this.slot == EquipmentSlot.OFF_HAND);

        if (!isCancelled()) {
            this.crateLocation = this.crateManager.getCrateLocation(this.location);
        }
    }

    public CrateInteractEvent(@NotNull final NexoFurnitureBreakEvent breakEvent, @NotNull final Action action, @NotNull final Location location) {
        this.nexoBreakEvent = breakEvent;

        this.player = this.nexoBreakEvent.getPlayer();
        this.slot = this.player.getActiveItemHand();
        this.location = location;
        this.action = action;

        setCancelled(this.slot == EquipmentSlot.OFF_HAND || isFurniture(location));

        if (!isCancelled()) {
            this.crateLocation = this.crateManager.getCrateLocation(this.location);
        }
    }

    public CrateInteractEvent(@NotNull final PlayerInteractEvent paperEvent, @NotNull final Location location) {
        this.paperEvent = paperEvent;

        this.player = this.paperEvent.getPlayer();
        this.action = this.paperEvent.getAction();
        this.slot = this.paperEvent.getHand();
        this.location = location;

        setCancelled(this.slot == EquipmentSlot.OFF_HAND || isFurniture(location));

        if (!isCancelled()) {
            this.crateLocation = this.crateManager.getCrateLocation(this.location);
        }
    }

    private final SettingsManager config = ConfigManager.getConfig();

    /**
     * A wrapper to check if a location has a piece of furniture at it.
     *
     * @param location the location
     * @return true or false
     */
    public boolean isFurniture(final Location location) {
        final String pluginName = this.config.getProperty(ConfigKeys.custom_items_plugin).toLowerCase();

        boolean isFurniture = false;

        switch (pluginName) {
            case "nexo" -> isFurniture = Support.nexo.isEnabled() && NexoFurniture.isFurniture(location);

            case "oraxen" -> isFurniture = Support.oraxen.isEnabled() && OraxenFurniture.isFurniture(location.getBlock());

            case "itemsadder" -> {
                if (Support.nexo.isEnabled()) {
                    final Entity entity = CustomFurniture.byAlreadySpawned(location.getBlock()).getEntity();

                    if (entity != null && entity.isValid()) {
                        isFurniture = true;
                    }
                }
            }

            default -> {}
        }

        return isFurniture;
    }

    /**
     * Prevents interacting/breaking the blocks.
     */
    public void cancel() {
        if (this.nexoInteractEvent != null) {
            this.nexoInteractEvent.setCancelled(true);

            return;
        }

        if (this.nexoBreakEvent != null) {
            this.nexoBreakEvent.setCancelled(true);

            return;
        }

        if (this.oraxenInteractEvent != null) {
            this.oraxenInteractEvent.setCancelled(true);

            return;
        }

        if (this.oraxenBreakEvent != null) {
            this.oraxenBreakEvent.setCancelled(true);

            return;
        }

        if (this.itemsAdderInteractEvent != null) {
            this.itemsAdderInteractEvent.setCancelled(true);

            return;
        }

        if (this.itemsAdderBreakEvent != null) {
            this.itemsAdderBreakEvent.setCancelled(true);

            return;
        }

        if (this.paperEvent != null) {
            this.paperEvent.setCancelled(true);
        }
    }

    public @NotNull CrateLocation getCrateLocation() {
        return this.crateLocation;
    }

    public @NotNull Location getLocation() {
        return this.location;
    }

    public @NotNull Player getPlayer() {
        return this.player;
    }

    public @NotNull Action getAction() {
        return this.action;
    }

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }
}