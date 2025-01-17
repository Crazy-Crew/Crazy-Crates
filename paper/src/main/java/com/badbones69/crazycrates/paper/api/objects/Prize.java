package com.badbones69.crazycrates.paper.api.objects;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.PrizeManager;
import com.badbones69.crazycrates.paper.api.enums.Messages;
import com.badbones69.crazycrates.paper.api.enums.other.Plugins;
import com.badbones69.crazycrates.paper.api.enums.other.keys.ItemKeys;
import com.badbones69.crazycrates.paper.utils.ItemUtils;
import com.badbones69.crazycrates.paper.utils.MiscUtils;
import com.badbones69.crazycrates.paper.api.builders.ItemBuilder;
import com.badbones69.crazycrates.core.config.ConfigManager;
import com.badbones69.crazycrates.core.config.impl.messages.CrateKeys;
import com.ryderbelserion.paper.util.PaperMethods;
import com.ryderbelserion.core.util.Methods;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Color;
import org.bukkit.Server;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.bukkit.configuration.ConfigurationSection;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Prize {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final ConfigurationSection section;
    private final List<ItemBuilder> builders;
    private final List<String> commands;
    private final List<String> messages;
    private final String sectionName;
    private final String prizeName;

    private List<String> permissions = new ArrayList<>();
    private ItemBuilder displayItem = new ItemBuilder();
    private boolean firework = false;
    private String crateName = "";
    private double weight = -1;

    private int maxPulls;

    private List<Tier> tiers = new ArrayList<>();
    private Prize alternativePrize;

    private boolean broadcastToggle = false;
    private List<String> broadcastMessages = new ArrayList<>();
    private String broadcastPermission = "";

    private List<ItemStack> editorItems = new ArrayList<>();

    public Prize(@NotNull final ConfigurationSection section, List<ItemStack> editorItems, @NotNull final List<Tier> tierPrizes, @NotNull final String crateName, @Nullable final Prize alternativePrize) {
        this.section = section;

        this.sectionName = section.getName();

        this.crateName = crateName;

        this.builders = ItemUtils.convertStringList(this.section.getStringList("Items"), this.sectionName);

        this.maxPulls = section.getInt("Settings.Max-Pulls", -1);

        this.tiers = tierPrizes;

        this.alternativePrize = alternativePrize;

        this.prizeName = section.getString("DisplayName", "");
        this.weight = section.getDouble("Weight", -1);
        this.firework = section.getBoolean("Firework", false);

        this.messages = section.getStringList("Messages"); // this returns an empty list if not found anyway.
        this.commands = section.getStringList("Commands"); // this returns an empty list if not found anyway.

        this.permissions = section.getStringList("BlackListed-Permissions"); // this returns an empty list if not found anyway.

        if (!this.permissions.isEmpty()) {
            this.permissions.replaceAll(String::toLowerCase);
        }

        this.broadcastToggle = section.getBoolean("Settings.Broadcast.Toggle", false);
        this.broadcastMessages = section.getStringList("Settings.Broadcast.Messages");
        this.broadcastPermission = section.getString("Settings.Broadcast.Permission", "");

        if (this.broadcastToggle && !this.broadcastPermission.isEmpty()) {
            MiscUtils.registerPermission(this.broadcastPermission, "Hides the broadcast message for prize: " + this.prizeName + " if a player has this permission", false);
        } else if (!this.broadcastToggle && !this.broadcastPermission.isEmpty()) {
            MiscUtils.unregisterPermission(this.broadcastPermission);
        }

        this.displayItem = display();

        this.editorItems = editorItems;
    }

    /**
     * Create a new prize.
     * This option is used only for Alternative Prizes.
     *
     * @param section the configuration section.
     */
    public Prize(@NotNull final String prizeName, @NotNull final String sectionName, @NotNull final ConfigurationSection section) {
        this.prizeName = prizeName;

        this.messages = section.getStringList("Messages"); // this returns an empty list if not found anyway.
        this.commands = section.getStringList("Commands"); // this returns an empty list if not found anyway.

        this.sectionName = sectionName;

        this.section = section;

        this.builders = ItemUtils.convertStringList(this.section.getStringList("Items"), this.sectionName);
    }

    /**
     * @return the name of the prize.
     */
    public @NotNull final String getPrizeName() {
        return this.prizeName.isEmpty() ? "<lang:" + this.displayItem.getType().getItemTranslationKey() + ">" : this.prizeName;
    }

    public @NotNull final String getStrippedName() {
        return PlainTextComponentSerializer.plainText().serialize(Methods.parse(getPrizeName()));
    }

    /**
     * @return the section name.
     */
    public @NotNull final String getSectionName() {
        return this.sectionName;
    }

    /**
     * @return the display item that is shown for the preview and the winning prize.
     */
    public @NotNull final ItemStack getDisplayItem(final Crate crate) {
        return getDisplayItem(null, crate);
    }

    /**
     * @return the display item that is shown for the preview and the winning prize.
     */
    public @NotNull final ItemStack getDisplayItem(@Nullable final Player player, final Crate crate) {
        final int pulls = PrizeManager.getCurrentPulls(this, crate);
        final int maxPulls = getMaxPulls();
        final String amount = String.valueOf(pulls);

        List<String> lore = new ArrayList<>();

        final boolean isPapiEnabled = Plugins.placeholder_api.isEnabled();

        final String displayName = this.displayItem.getDisplayName();

        this.displayItem.setDisplayName(player != null && isPapiEnabled ? PlaceholderAPI.setPlaceholders(player, displayName) : displayName);

        if (this.section.contains("DisplayLore") && !this.section.contains("Lore")) {
            this.section.getStringList("DisplayLore").forEach(line -> lore.add(player != null && isPapiEnabled ? PlaceholderAPI.setPlaceholders(player, line) : line));
        }

        if (this.section.contains("Lore")) {
            if (MiscUtils.isLogging()) {
                List.of(
                        "Deprecated usage of Lore in your Prize " + this.sectionName + " in " + this.crateName + ".yml, please change Lore to DisplayLore",
                        "Lore will be removed in the next major version of Minecraft in favor of DisplayLore",
                        "You can turn my nagging off in config.yml, verbose_logging: true -> false"
                ).forEach(this.plugin.getComponentLogger()::warn);
            }

            this.section.getStringList("Lore").forEach(line -> lore.add(player != null && isPapiEnabled ? PlaceholderAPI.setPlaceholders(player, line) : line));
        }

        if (maxPulls != 0 && pulls != 0 && pulls >= maxPulls) {
            if (player != null) {
                final String line = Messages.crate_prize_max_pulls.getMessage(player);

                if (!line.isEmpty()) {
                    final String variable = line.replaceAll("\\{maxpulls}", String.valueOf(maxPulls)).replaceAll("\\{pulls}", amount);

                    lore.add(isPapiEnabled ? PlaceholderAPI.setPlaceholders(player, variable) : variable);
                }
            } else {
                final String line = ConfigManager.getMessages().getProperty(CrateKeys.crate_prize_max_pulls);

                if (!line.isEmpty()) {
                    lore.add(line.replaceAll("\\{maxpulls}", String.valueOf(maxPulls)).replaceAll("\\{pulls}", amount));
                }
            }
        }

        this.displayItem.setDisplayLore(lore);

        if (player != null) {
            this.displayItem.setPlayer(player);
        }

        final String weight = Methods.format(crate.getChance(getWeight()));

        this.displayItem.addLorePlaceholder("%chance%", weight).addLorePlaceholder("%maxpulls%", String.valueOf(maxPulls)).addLorePlaceholder("%pulls%", amount);
        this.displayItem.addNamePlaceholder("%chance%", weight).addNamePlaceholder("%maxpulls%", String.valueOf(maxPulls)).addNamePlaceholder("%pulls%", amount);

        return this.displayItem.setPersistentString(ItemKeys.crate_prize.getNamespacedKey(), this.sectionName).asItemStack();
    }

    /**
     * Gets the rounding mode from the config
     *
     * @return the rounding mode
     * @since 0.0.2
     */
    public final RoundingMode mode() {
        return RoundingMode.HALF_EVEN;
    }
    
    /**
     * @return the list of tiers the prize is in.
     */
    public @NotNull final List<Tier> getTiers() {
        return this.tiers;
    }
    
    /**
     * @return the messages sent to the player.
     */
    public @NotNull final List<String> getMessages() {
        return this.messages;
    }
    
    /**
     * @return the commands that are run when the player wins.
     */
    public @NotNull final List<String> getCommands() {
        return this.commands;
    }
    
    /**
     * @return the ItemBuilders for all the custom items made from the Items: option.
     */
    public @NotNull final List<ItemBuilder> getItemBuilders() {
        return this.builders;
    }
    
    /**
     * @return the name of the crate the prize is in.
     */
    public @NotNull final String getCrateName() {
        return this.crateName;
    }

    /**
     * Gets the weight
     *
     * @return the weight
     */
    public final double getWeight() {
        return this.weight;
    }
    
    /**
     * @return true if a firework explosion is played and false if not.
     */
    public final boolean useFireworks() {
        return this.firework;
    }
    
    /**
     * @return the alternative prize the player wins if they have a blacklist permission.
     */
    public @NotNull final Prize getAlternativePrize() {
        return this.alternativePrize;
    }
    
    /**
     * @return true if the prize doesn't have an alternative prize and false if it does.
     */
    public final boolean hasAlternativePrize() {
        return this.alternativePrize == null;
    }
    
    /**
     * @return true if they prize has blacklist permissions and false if not.
     */
    public final boolean hasPermission(@NotNull final Player player) {
        if (player.isOp()) return false;

        for (String permission : this.permissions) {
            if (player.hasPermission(permission)) return true;
        }

        return false;
    }

    public void broadcast(final Player target, final Crate crate) {
        if (this.broadcastToggle) {
            send(target, crate);
        } else if (crate.isBroadcastToggled()) {
            send(target, crate);
        }
    }

    private void send(final Player target, final Crate crate) {
        final Server server = this.plugin.getServer();

        final List<String> messages = this.broadcastToggle ? this.broadcastMessages : crate.getBroadcastMessages();
        final String permission = this.broadcastToggle ? this.broadcastPermission : crate.getBroadcastPermission();

        final String current_pulls = String.valueOf(PrizeManager.getCurrentPulls(this, crate));
        final String max_pulls = String.valueOf(getMaxPulls());

        final Component message = this.plugin.getFusion().color(target, Methods.toString(messages), new HashMap<>() {{
            put("%player%", target.getName());
            put("%crate%", crate.getCrateName());
            put("%reward%", getPrizeName().replaceAll("%maxpulls%", max_pulls).replaceAll("%pulls%", current_pulls));
            put("%maxpulls%", max_pulls);
            put("%pulls%", current_pulls);
            put("%reward_stripped%", getStrippedName());
        }});

        if (permission.isEmpty()) {
            server.broadcast(message);

            return;
        }

        server.broadcast(message, permission);
    }

    private @NotNull ItemBuilder display() {
        ItemBuilder builder = new ItemBuilder();

        try {
            if (this.section.contains("DisplayData")) {
                builder = builder.fromBase64(this.section.getString("DisplayData", ""));
            }

            if (this.section.contains("DisplayName")) {
                builder.setDisplayName(this.prizeName);
            }

            if (this.section.contains("DisplayItem")) {
                builder.withType(this.section.getString("DisplayItem", "red_terracotta").toLowerCase());
            }

            if (this.section.contains("DisplayAmount")) {
                builder.setAmount(this.section.getInt("DisplayAmount", 1));
            }

            if (this.section.contains("DisplayLore") && !this.section.contains("Lore")) {
                builder.setDisplayLore(this.section.getStringList("DisplayLore"));
            }

            if (this.section.contains("Lore")) {
                if (MiscUtils.isLogging()) {
                    List.of(
                            "Deprecated usage of Lore in your Prize " + this.sectionName + " in " + this.crateName + ".yml, please change Lore to DisplayLore",
                            "Lore will be removed in the next major version of Minecraft in favor of DisplayLore",
                            "You can turn my nagging off in config.yml, verbose_logging: true -> false"
                    ).forEach(this.plugin.getComponentLogger()::warn);
                }

                builder.setDisplayLore(this.section.getStringList("Lore"));
            }

            //builder.addLorePlaceholder("%chance%", this.getTotalChance());

            if (this.section.contains("Glowing")) {
                builder.setGlowing(this.section.getBoolean("Glowing", false));
            }

            builder.setDamage(this.section.getInt("DisplayDamage", 0));

            if (this.section.contains("Patterns")) {
                if (MiscUtils.isLogging()) {
                    List.of(
                            "Deprecated usage of Patterns in your Prize " + this.sectionName + " in " + this.crateName + ".yml, please change Patterns to DisplayPatterns",
                            "Patterns will be removed in the next major version of Minecraft in favor of DisplayPatterns",
                            "You can turn my nagging off in config.yml, verbose_logging: true -> false"
                    ).forEach(this.plugin.getComponentLogger()::warn);
                }

                for (final String pattern : this.section.getStringList("Patterns")) {
                    builder.addPattern(pattern.toLowerCase());
                }
            }

            if (this.section.contains("DisplayPatterns")) {
                for (final String pattern : this.section.getStringList("DisplayPatterns")) {
                    builder.addPattern(pattern.toLowerCase());
                }
            }

            builder.setHidingItemFlags(this.section.getBoolean("HideItemFlags", false) || !this.section.getStringList("Flags").isEmpty());

            builder.setUnbreakable(this.section.getBoolean("Unbreakable", false));

            builder.setCustomModelData(this.section.getInt("Settings.Custom-Model-Data", -1));

            if (this.section.contains("Settings.Mob-Type")) {
                final EntityType type = PaperMethods.getEntity(this.section.getString("Settings.Mob-Type", "cow"));

                if (type != null) {
                    builder.setEntityType(type);
                }
            }

            if (this.section.contains("Settings.RGB")) {
                final @Nullable Color color = PaperMethods.getRGB(this.section.getString("Settings.RGB", ""));

                if (color != null) {
                    builder.setColor(color);
                }
            } else if (this.section.contains("Settings.Color")) {
                builder.setColor(PaperMethods.getColor(this.section.getString("Settings.Color", "RED")));
            }

            if (this.section.contains("Skull")) {
                builder.setSkull(section.getString("Skull", ""));
            }

            if (this.section.contains("Player")) {
                builder.setPlayer(this.section.getString("Player", ""));
            }

            if (this.section.contains("DisplayTrim.Pattern") && builder.isArmor()) {
                builder.applyTrimPattern(this.section.getString("DisplayTrim.Pattern", "sentry"));
            }

            if (this.section.contains("DisplayTrim.Material") && builder.isArmor()) {
                builder.applyTrimMaterial(this.section.getString("DisplayTrim.Material", "quartz"));
            }

            if (this.section.contains("DisplayEnchantments")) {
                for (String ench : this.section.getStringList("DisplayEnchantments")) {
                    String[] value = ench.split(":");

                    builder.addEnchantment(value[0], Integer.parseInt(value[1]), true);
                }
            }

            return builder;
        } catch (Exception exception) {
            return new ItemBuilder(ItemType.RED_TERRACOTTA).setDisplayName("<red><bold>ERROR").setDisplayLore(new ArrayList<>() {{
                add("<red>There was an error with one of your prizes!");
                add("<red>The reward in question is labeled: <yellow>" + section.getName() + " <red>in crate: <yellow>" + crateName);
                add("<red>Name of the reward is " + section.getString("DisplayName"));
                add("<red>If you are confused, Stop by our discord for support!");
            }});
        }
    }

    public final List<ItemStack> getEditorItems() {
        return this.editorItems;
    }

    public final int getMaxPulls() {
        return this.maxPulls == -1 ? 0 : this.maxPulls;
    }
}