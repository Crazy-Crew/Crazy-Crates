package com.badbones69.crazycrates.api.objects.gui;

import com.badbones69.crazycrates.api.builders.ItemBuilder;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.utils.MsgUtils;
import com.badbones69.crazycrates.tasks.crates.effects.SoundEffect;
import com.ryderbelserion.vital.paper.api.builders.gui.interfaces.GuiItem;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.function.Consumer;

public class GuiButton {

    private final ConfigurationSection section;
    private final ItemBuilder guiItem;

    private final List<String> commands;
    private final List<String> messages;

    private final Crate crate;

    public GuiButton(final Crate crate, final ConfigurationSection section) {
        final String name = section.getString("name", "No display name found.");
        final String material = section.getString("material", "emerald_block");
        final List<String> lore = section.getStringList("lore");
        final List<String> commands = section.getStringList("commands");
        final List<String> messages = section.getStringList("messages");

        this.guiItem = new ItemBuilder().withType(material).setDisplayName(name).setDisplayLore(lore);

        this.commands = commands;
        this.messages = messages;
        this.section = section;
        this.crate = crate;
    }

    public final @NotNull GuiItem getGuiItem(@NotNull final Consumer<InventoryClickEvent> action) {
        return this.guiItem.asGuiItem(event -> {
            if (!(event.getWhoClicked() instanceof Player player)) return;

            final Server server = player.getServer();

            commands.forEach(command -> server.dispatchCommand(server.getConsoleSender(), command.replaceAll("%crate%", crate.getFileName())));
            messages.forEach(message -> MsgUtils.sendMessage(player, message.replaceAll("%crate%", crate.getCrateName()), false));

            final ConfigurationSection sound = section.getConfigurationSection("sound");

            if (sound != null) {
                SoundEffect effect = new SoundEffect(
                        section,
                        "sound",
                        "entity.villager.yes",
                        Sound.Source.PLAYER
                );

                effect.play(player, player.getLocation());
            }

            action.accept(event);
        });
    }
}