package com.badbones69.crazycrates.paper.cratetypes;

import com.badbones69.crazycrates.paper.CrazyCratesOld;
import com.badbones69.crazycrates.paper.Methods;
import com.badbones69.crazycrates.paper.api.CrazyManager;
import us.crazycrew.crazycrates.paper.api.plugin.users.BukkitUserManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.paper.api.interfaces.HologramController;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import us.crazycrew.crazycrates.paper.api.plugin.CrazyHandler;
import java.util.ArrayList;
import java.util.Random;

public class FireCracker {

    private final @NotNull CrazyCratesOld plugin = JavaPlugin.getPlugin(CrazyCratesOld.class);
    private final @NotNull CrazyHandler crazyHandler = this.plugin.getCrazyHandler();
    private final @NotNull BukkitUserManager userManager = this.crazyHandler.getUserManager();
    private final @NotNull Methods methods = this.crazyHandler.getMethods();
    private final @NotNull CrazyManager crazyManager = this.crazyHandler.getCrazyManager();
    
    public void startFireCracker(Player player, Crate crate, KeyType keyType, Location blockLocation, HologramController hologramController) {
        if (!this.userManager.takeKeys(1, player.getUniqueId(), crate.getName(), keyType, true)) {
            this.methods.failedToTakeKey(player.getName(), crate);
            this.crazyManager.removePlayerFromOpeningList(player);
            return;
        }

        if (hologramController != null) hologramController.removeHologram(blockLocation.getBlock());

        final ArrayList<Color> colors = new ArrayList<>();
        colors.add(Color.RED);
        colors.add(Color.YELLOW);
        colors.add(Color.GREEN);
        colors.add(Color.BLUE);
        colors.add(Color.BLACK);
        colors.add(Color.AQUA);
        colors.add(Color.MAROON);
        colors.add(Color.PURPLE);

        this.crazyManager.addCrateTask(player, new BukkitRunnable() {
            final Random random = new Random();
            final int color = random.nextInt(colors.size());
            int time = 0;
            final Location location = blockLocation.clone().add(.5, 25, .5);
            
            @Override
            public void run() {
                location.subtract(0, 1, 0);
                methods.firework(location, colors.get(color));
                time++;

                if (time == 25) {
                    crazyManager.endCrate(player);
                    // The key type is set to free because the key has already been taken above.
                    //TODO() Update static method.
                    //QuickCrate.openCrate(player, blockLocation, crate, KeyType.FREE_KEY, hologramController);
                }
            }
        }.runTaskTimer(this.plugin, 0, 2));
    }
}