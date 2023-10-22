package com.badbones69.crazycrates.paper.listeners.crates;

import com.badbones69.crazycrates.api.enums.types.CrateType;
import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.Methods;
import com.badbones69.crazycrates.paper.api.CrazyManager;
import com.badbones69.crazycrates.paper.api.enums.settings.Messages;
import com.badbones69.crazycrates.paper.api.events.crates.CrateOpenEvent;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.listeners.CrateControlListener;
import com.badbones69.crazycrates.paper.support.libraries.PluginSupport;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.List;

@SuppressWarnings("deprecation")
public class CrateOpenListener implements Listener {

    private final CrazyManager crazyManager;

    public CrateOpenListener(CrazyCrates plugin) {
        this.crazyManager = plugin.getStarter().getCrazyManager();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onCrateOpen(CrateOpenEvent event) {
        Player player = event.getPlayer();
        Crate crate = event.getCrate();

        if (crate.getCrateType() != CrateType.MENU) {
            if (!crate.canWinPrizes(player)) {
                player.sendMessage(Messages.NO_PRIZES_FOUND.getMessage());
                this.crazyManager.removePlayerFromOpeningList(player);
                this.crazyManager.removePlayerKeyType(player);

                event.setCancelled(true);

                return;
            }
        }

        if (!(player.hasPermission("crazycrates.open." + crate.getName()) || player.hasPermission("crazycrates.open.*"))) {
            player.sendMessage(Messages.NO_CRATE_PERMISSION.getMessage());
            this.crazyManager.removePlayerFromOpeningList(player);
            CrateControlListener.inUse.remove(player);

            event.setCancelled(true);

            return;
        }

        this.crazyManager.addPlayerToOpeningList(player, crate);
        this.crazyManager.addCrate(player, crate);

        JavaPlugin plugin = event.getPlugin();

        FileConfiguration configuration = event.getConfiguration();

        String broadcastMessage = configuration.getString("Crate.BroadCast", "");
        boolean broadcastToggle = configuration.contains("Crate.OpeningBroadCast") && configuration.getBoolean("Crate.OpeningBroadCast");

        if (broadcastToggle) {
            if (!broadcastMessage.isBlank()) {
                plugin.getServer().broadcastMessage(Methods.color(broadcastMessage.replaceAll("%prefix%", Methods.getPrefix())).replaceAll("%player%", Methods.getPrefix()));
            }
        }

        boolean commandToggle = configuration.contains("Crate.Opening-Command") && configuration.getBoolean("Crate.Opening-Command.Toggle");

        if (commandToggle) {
            List<String> commands = configuration.getStringList("Crate.Opening-Command.Commands");

            if (!commands.isEmpty()) {
                commands.forEach(line -> {
                    StringBuilder builder = new StringBuilder();

                    if (PluginSupport.PLACEHOLDERAPI.isPluginEnabled()) {
                        builder.append(PlaceholderAPI.setPlaceholders(player, line.replaceAll("%prefix%", Methods.getPrefix()).replaceAll("%player%", player.getName())));
                    } else {
                        builder.append(line.replaceAll("%prefix%", Methods.getPrefix()).replaceAll("%player%", player.getName()));
                    }

                    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), builder.toString());
                });
            }
        }
    }
}