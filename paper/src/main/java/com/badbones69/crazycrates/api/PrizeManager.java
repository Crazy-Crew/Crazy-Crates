package com.badbones69.crazycrates.api;

import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.objects.Tier;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.events.PlayerPrizeEvent;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Prize;
import com.badbones69.crazycrates.api.builders.ItemBuilder;
import com.ryderbelserion.vital.paper.api.enums.Support;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import com.badbones69.crazycrates.api.utils.MsgUtils;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import static java.util.regex.Matcher.quoteReplacement;

public class PrizeManager {
    
    private static @NotNull final CrazyCrates plugin = CrazyCrates.getPlugin();

    /**
     * Gets the prize for the player.
     *
     * @param player who the prize is for.
     * @param crate the player is opening.
     * @param prize the player is being given.
     */
    public static void givePrize(@NotNull final Player player, @Nullable Prize prize, @NotNull final Crate crate) {
        if (prize == null) {
            if (MiscUtils.isLogging()) plugin.getComponentLogger().warn("No prize was found when giving {} a prize.", player.getName());

            return;
        }

        prize = prize.hasPermission(player) ? prize.getAlternativePrize() : prize;

        for (ItemStack item : prize.getEditorItems()) {
            if (!MiscUtils.isInventoryFull(player)) {
                player.getInventory().addItem(item);
            } else {
                player.getWorld().dropItemNaturally(player.getLocation(), item);
            }
        }

        if (!prize.getItemBuilders().isEmpty()) {
            final boolean isPlaceholderAPIEnabled = Support.placeholder_api.isEnabled();

            for (final ItemBuilder item : prize.getItemBuilders()) {
                if (isPlaceholderAPIEnabled) {
                    final String displayName = item.getDisplayName();

                    if (!displayName.isEmpty()) {
                        item.setDisplayName(PlaceholderAPI.setPlaceholders(player, displayName));
                    }

                    final List<String> displayLore = item.getDisplayLore();

                    if (!displayLore.isEmpty()) {
                        List<String> lore = new ArrayList<>();

                        displayLore.forEach(line -> lore.add(PlaceholderAPI.setPlaceholders(player, line)));

                        item.setDisplayLore(lore);
                    }
                }

                if (!MiscUtils.isInventoryFull(player)) {
                    MiscUtils.addItem(player, item.setPlayer(player).getStack());
                } else {
                    player.getWorld().dropItemNaturally(player.getLocation(), item.setPlayer(player).getStack());
                }
            }
        }

        for (final String command : crate.getPrizeCommands()) {
            runCommands(player, prize, crate, command);
        }

        for (final String command : prize.getCommands()) {
            runCommands(player, prize, crate, command);
        }

        if (!crate.getPrizeMessage().isEmpty() && prize.getMessages().isEmpty()) {
            for (final String message : crate.getPrizeMessage()) {
                sendMessage(player, prize, crate, message);
            }

            return;
        }

        for (final String message : prize.getMessages()) {
            sendMessage(player, prize, crate, message);
        }

        prize.broadcast(crate);
    }

    private static void runCommands(@NotNull final Player player, @NotNull final Prize prize, @NotNull final Crate crate, @NotNull String command) {
        String cmd = command;

        if (cmd.contains("%random%:")) {
            final StringBuilder commandBuilder = new StringBuilder();

            for (String word : cmd.split(" ")) {
                if (word.startsWith("%random%:")) {// /give %player% iron %random%:1-64
                    word = word.replace("%random%:", "");

                    try {
                        long min = Long.parseLong(word.split("-")[0]);
                        long max = Long.parseLong(word.split("-")[1]);

                        commandBuilder.append(MiscUtils.pickNumber(min, max)).append(" ");
                    } catch (Exception e) {
                        commandBuilder.append("1 ");

                        if (MiscUtils.isLogging()) {
                            plugin.getComponentLogger().warn("The prize {} in the {} crate has caused an error when trying to run a command.", prize.getPrizeName(), prize.getCrateName());
                            plugin.getComponentLogger().warn("Command: {}", cmd);
                        }
                    }
                } else {
                    commandBuilder.append(word).append(" ");
                }
            }

            cmd = commandBuilder.toString();
            cmd = cmd.substring(0, cmd.length() - 1);
        }

        if (Support.placeholder_api.isEnabled() ) cmd = PlaceholderAPI.setPlaceholders(player, cmd);

        MiscUtils.sendCommand(cmd
                .replaceAll("%player%", quoteReplacement(player.getName()))
                .replaceAll("%reward%", quoteReplacement(prize.getPrizeName()))
                .replaceAll("%reward_stripped%", quoteReplacement(prize.getStrippedName()))
                .replaceAll("%crate_fancy%", quoteReplacement(crate.getCrateName()))
                .replaceAll("%crate%", quoteReplacement(crate.getFileName())));
    }

    private static void sendMessage(@NotNull final Player player, @NotNull final Prize prize, @NotNull final Crate crate, String message) {
        if (message.isEmpty()) return;

        final String defaultMessage = message
                .replaceAll("%player%", quoteReplacement(player.getName()))
                .replaceAll("%reward%", quoteReplacement(prize.getPrizeName()))
                .replaceAll("%reward_stripped%", quoteReplacement(prize.getStrippedName()))
                .replaceAll("%crate%", quoteReplacement(crate.getCrateName()));

        MsgUtils.sendMessage(player, Support.placeholder_api.isEnabled() ? PlaceholderAPI.setPlaceholders(player, defaultMessage) : defaultMessage, false);
    }

    /**
     * Gets the prize for the player.
     *
     * @param player who the prize is for.
     * @param crate the player is opening.
     * @param prize the player is being given.
     */
    public static void givePrize(@NotNull final Player player, @NotNull final Crate crate, @Nullable final Prize prize) {
        if (prize != null) {
            givePrize(player, prize, crate);

            if (prize.useFireworks()) MiscUtils.spawnFirework(player.getLocation().add(0, 1, 0), null);

            plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, prize));
        } else {
            Messages.prize_error.sendMessage(player, new HashMap<>() {{
                put("{crate}", crate.getCrateName());
            }});
        }
    }

    public static void getPrize(@NotNull final Crate crate, @NotNull final Inventory inventory, final int slot, @NotNull final Player player) {
        final ItemStack item = inventory.getItem(slot);

        if (item == null) return;

        final Prize prize = crate.getPrize(item);

        givePrize(player, prize, crate);
    }

    public static @Nullable Tier getTier(@NotNull final Crate crate) {
        if (!crate.getTiers().isEmpty()) {
            Random random = MiscUtils.useOtherRandom() ? ThreadLocalRandom.current() : new Random();

            for (int stopLoop = 0; stopLoop <= 100; stopLoop++) {
                for (final Tier tier : crate.getTiers()) {
                    final int chance = tier.getChance();

                    final int num = random.nextInt(tier.getMaxRange());

                    if (num >= 1 && num <= chance) {
                        return tier;
                    }
                }
            }
        }

        return null;
    }
}