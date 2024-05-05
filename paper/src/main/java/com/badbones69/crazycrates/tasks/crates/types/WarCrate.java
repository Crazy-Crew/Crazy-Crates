package com.badbones69.crazycrates.tasks.crates.types;

import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.builders.ItemBuilder;
import com.ryderbelserion.vital.util.scheduler.FoliaRunnable;
import com.badbones69.crazycrates.tasks.BukkitUserManager;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.api.builders.CrateBuilder;
import com.badbones69.crazycrates.api.builders.types.CratePrizeMenu;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class WarCrate extends CrateBuilder {

    private @NotNull final CrateManager crateManager = this.plugin.getCrateManager();

    private @NotNull final BukkitUserManager userManager = this.plugin.getUserManager();

    private final Map<ItemStack, String> colorCodes = new ConcurrentHashMap<>();

    public WarCrate(@NotNull final Crate crate, @NotNull final Player player, final int size) {
        super(crate, player, size);
    }

    @Override
    public void open(@NotNull final KeyType type, final boolean checkHand) {
        // Crate event failed so we return.
        if (isCrateEventValid(type, checkHand)) {
            return;
        }

        final Player player = getPlayer();
        final UUID uuid = player.getUniqueId();
        final Crate crate = getCrate();
        final String crateName = crate.getName();

        final boolean keyCheck = this.userManager.takeKeys(uuid, crateName, type, 1, checkHand);

        if (!keyCheck) {
            // Remove from opening list.
            this.crateManager.removePlayerFromOpeningList(player);

            // Remove closer/picker
            this.crateManager.removeCloser(player);
            this.crateManager.removePicker(player);

            return;
        }

        this.crateManager.addPicker(player, false);
        this.crateManager.addCloser(player, false);

        addCrateTask(new FoliaRunnable(player.getScheduler(), null) {
            int full = 0;
            int open = 0;

            @Override
            public void run() {
                if (this.full < 25) {
                    setRandomPrizes();

                    playSound("cycle-sound", SoundCategory.PLAYERS, "block.lava.pop");
                }

                this.open++;

                if (this.open >= 3) {
                    player.openInventory(getInventory());

                    this.open = 0;
                }

                this.full++;

                if (this.full == 26) {
                    playSound("stop-sound", SoundCategory.PLAYERS, "block.lava.pop");

                    setRandomGlass();

                    crateManager.addPicker(player, true);
                }
            }
        }.runAtFixedRate(this.plugin, 1, 3));
    }

    private void setRandomPrizes() {
        final Player player = getPlayer();
        final Crate crate = getCrate();

        if (!this.crateManager.isInOpeningList(player) && !(getInventory().getHolder(false) instanceof CratePrizeMenu)) return;

        for (int index = 0; index < 9; index++) {
            setItem(index, crate.pickPrize(player).getDisplayItem(player));
        }
    }

    private void setRandomGlass() {
        final Player player = getPlayer();

        if (!this.crateManager.isInOpeningList(player) && !(getInventory().getHolder(false) instanceof CratePrizeMenu)) return;

        if (this.colorCodes.isEmpty()) getColorCode();

        final ItemBuilder builder = MiscUtils.getRandomPaneColor();
        builder.setDisplayName("<" + this.colorCodes.get(builder.build()) + "><bold>???</bold>");
        final ItemStack item = builder.build();

        for (int index = 0; index < 9; index++) {
            setItem(index, item);
        }
    }

    private void getColorCode() {
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.WHITE_STAINED_GLASS_PANE).build(), "white");
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.ORANGE_STAINED_GLASS_PANE).build(), "gold");
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.MAGENTA_STAINED_GLASS_PANE).build(), "light_purple");
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.LIGHT_BLUE_STAINED_GLASS_PANE).build(), "dark_aqua");
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.YELLOW_STAINED_GLASS_PANE).build(), "yellow");
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.LIME_STAINED_GLASS_PANE).build(), "green");
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.PINK_STAINED_GLASS_PANE).build(), "red");
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.GRAY_STAINED_GLASS_PANE).build(), "dark_gray");
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.LIGHT_GRAY_STAINED_GLASS_PANE).build(), "gray");
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.CYAN_STAINED_GLASS_PANE).build(), "aqua");
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.PURPLE_STAINED_GLASS_PANE).build(), "dark_purple");
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.BLUE_STAINED_GLASS_PANE).build(), "dark_blue");
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.BROWN_STAINED_GLASS_PANE).build(), "gold");
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.GREEN_STAINED_GLASS_PANE).build(), "green");
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.RED_STAINED_GLASS_PANE).build(), "dark_red");
        this.colorCodes.put(new ItemBuilder().setMaterial(Material.BLACK_STAINED_GLASS_PANE).build(), "black");
    }

    @Override
    public void run() {

    }
}