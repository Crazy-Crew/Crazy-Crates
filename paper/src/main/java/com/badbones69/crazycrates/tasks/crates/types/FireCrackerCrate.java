package com.badbones69.crazycrates.tasks.crates.types;

import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.tasks.BukkitUserManager;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import fr.euphyllia.energie.model.SchedulerType;
import fr.euphyllia.energie.utils.SchedulerTaskRunnable;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.api.builders.CrateBuilder;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class FireCrackerCrate extends CrateBuilder {

    @NotNull
    private final CrateManager crateManager = this.plugin.getCrateManager();

    @NotNull
    private final BukkitUserManager userManager = this.plugin.getUserManager();

    public FireCrackerCrate(Crate crate, Player player, int size, Location location) {
        super(crate, player, size, location);
    }

    @Override
    public void open(KeyType type, boolean checkHand) {
        // Crate event failed so we return.
        if (isCrateEventValid(type, checkHand)) {
            return;
        }

        this.crateManager.addCrateInUse(getPlayer(), getLocation());

        boolean keyCheck = this.userManager.takeKeys(1, getPlayer().getUniqueId(), getCrate().getName(), type, checkHand);

        if (!keyCheck) {
            // Send the message about failing to take the key.
            MiscUtils.failedToTakeKey(getPlayer(), getCrate().getName());

            // Remove from opening list.
            this.crateManager.removePlayerFromOpeningList(getPlayer());

            return;
        }

        if (this.crateManager.getHolograms() != null) {
            this.crateManager.getHolograms().removeHologram(getLocation().getBlock());
        }

        List<Color> colors = Arrays.asList(Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE, Color.BLACK, Color.AQUA, Color.MAROON, Color.PURPLE);

        addCrateTask(new SchedulerTaskRunnable() {
            final int random = ThreadLocalRandom.current().nextInt(colors.size());
            final Location location = getLocation().clone().add(.5, 25, .5);

            int length = 0;

            @Override
            public void run() {
                this.location.subtract(0, 1, 0);
                MiscUtils.spawnFirework(this.location, colors.get(this.random));
                this.length++;

                if (this.length == 25) {
                    crateManager.endCrate(getPlayer());

                    QuickCrate quickCrate = new QuickCrate(getCrate(), getPlayer(), getLocation());

                    quickCrate.open(KeyType.free_key, false);
                }
            }
        }.runAtFixedRate(this.plugin, SchedulerType.SYNC, getPlayer(), null, 0, 2));
    }

    @Override
    public void run() {

    }
}