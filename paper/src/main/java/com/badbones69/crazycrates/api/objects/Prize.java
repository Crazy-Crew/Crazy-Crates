package com.badbones69.crazycrates.api.objects;

import com.badbones69.crazycrates.api.enums.PersistentKeys;
import com.badbones69.crazycrates.api.builders.ItemBuilder;
import com.badbones69.crazycrates.api.utils.ItemUtils;
import com.ryderbelserion.vital.items.ItemStackBuilder;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Prize {

    private ItemStackBuilder displayItem = new ItemStackBuilder(Material.CHEST);
    private final List<ItemStack> items = new ArrayList<>();

    private List<String> permissions = new ArrayList<>();
    private final List<String> commands;
    private final List<String> messages;
    private boolean firework = false;
    private String crateName = "";
    private final String prizeName;
    private int maxRange = 100;
    private final String sectionName;
    private int chance = 0;

    private List<Tier> tiers = new ArrayList<>();
    private final List<ItemStackBuilder> builders;
    private Prize alternativePrize;

    private final ConfigurationSection section;

    public Prize(@NotNull final ConfigurationSection section, @NotNull final List<Tier> tierPrizes, @NotNull final String crateName, @Nullable final Prize alternativePrize) {
        this.section = section;

        this.sectionName = section.getName();

        this.crateName = crateName;

        List<?> list = section.getList("Editor-Items");

        if (list != null) {
            for (Object key : list) {
                this.items.add((ItemStack) key);
            }
        }

        this.builders = ItemBuilder.convertStringList(this.section.getStringList("Items"), this.sectionName);

        this.tiers = tierPrizes;

        this.alternativePrize = alternativePrize;

        this.prizeName = section.getString("DisplayName", WordUtils.capitalizeFully(section.getString("DisplayItem", "stone").replaceAll("_", " ")));
        this.maxRange = section.getInt("MaxRange", 100);
        this.chance = section.getInt("Chance", 50);
        this.firework = section.getBoolean("Firework", false);

        this.messages = section.getStringList("Messages"); // this returns an empty list if not found anyway.
        this.commands = section.getStringList("Commands"); // this returns an empty list if not found anyway.

        this.permissions = section.getStringList("BlackListed-Permissions"); // this returns an empty list if not found anyway.

        if (!this.permissions.isEmpty()) {
            this.permissions.replaceAll(String::toLowerCase);
        }

        this.displayItem = display();
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

        this.builders = ItemBuilder.convertStringList(this.section.getStringList("Items"), this.sectionName);
    }

    /**
     * @return the name of the prize.
     */
    public @NotNull final String getPrizeName() {
        return this.prizeName;
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
    public @NotNull final ItemStackBuilder getDisplayItem() {
        ItemStackBuilder itemStack = this.displayItem.build();

        itemStack.editMeta(itemMeta -> itemMeta.getPersistentDataContainer().set(PersistentKeys.crate_prize.getNamespacedKey(), PersistentDataType.STRING, this.sectionName));

        return itemStack;
    }

    /**
     * @return the display item that is shown for the preview and the winning prize.
     */
    public @NotNull final ItemStackBuilder getDisplayItem(@NotNull final Player player) {
        return this.displayItem.setPlayer(player).setPersistentString(PersistentKeys.crate_prize.getNamespacedKey(), this.sectionName).build();
    }

    /**
     * @return the ItemBuilder of the display item.
     */
    public @NotNull final ItemStackBuilder getDisplayItemBuilder() {
        return this.displayItem;
    }
    
    /**
     * @return the list of tiers the prize is in.
     */
    public @NotNull final List<Tier> getTiers() {
        return Collections.unmodifiableList(this.tiers);
    }
    
    /**
     * @return the messages sent to the player.
     */
    public @NotNull final List<String> getMessages() {
        return Collections.unmodifiableList(this.messages);
    }
    
    /**
     * @return the commands that are run when the player wins.
     */
    public @NotNull final List<String> getCommands() {
        return Collections.unmodifiableList(this.commands);
    }
    
    /**
     * @return the Editor ItemStacks that are given to the player that wins.
     */
    public @NotNull final List<ItemStack> getItems() {
        return Collections.unmodifiableList(this.items);
    }
    
    /**
     * @return the ItemBuilders for all the custom items made from the Items: option.
     */
    public @NotNull final List<ItemStackBuilder> getItemBuilders() {
        return Collections.unmodifiableList(this.builders);
    }
    
    /**
     * @return the name of the crate the prize is in.
     */
    public @NotNull final String getCrateName() {
        return this.crateName;
    }
    
    /**
     * @return the chance the prize has of being picked.
     */
    public final int getChance() {
        return this.chance;
    }
    
    /**
     * @return the max range of the prize.
     */
    public final int getMaxRange() {
        return this.maxRange;
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

    private @NotNull ItemStackBuilder display() {
        final ItemStackBuilder builder = new ItemStackBuilder(Material.CHEST);

        try {
            final String material = this.section.getString("DisplayItem", "red_terracotta");

            final int amount = this.section.getInt("DisplayAmount", 1);
            /*final String nbt = this.section.getString("DisplayNbt", "");

            if (!nbt.isEmpty()) {
                builder.setMaterial(material).setAmount(amount).setCompoundTag(nbt);

                builder.setString(PersistentKeys.crate_prize.getNamespacedKey(), this.section.getName());

                return builder;
            }*/

            builder.withType(material, false).setDisplayName(this.prizeName).setDisplayAmount(amount);

            return ItemUtils.getItem(this.section, builder).setPersistentString(PersistentKeys.crate_prize.getNamespacedKey(), this.section.getName());
        } catch (Exception exception) {
            final List<String> list = new ArrayList<>() {{
               add("<red>There was an error with one of your prizes!");
               add("<red>The reward in question is labeled: <yellow>" + section.getName() + " <red>in crate: <yellow>" + crateName);
               add("<red>Name of the reward is " + section.getString("DisplayName"));
               add("<red>If you are confused, Stop by our discord for support!");
            }};

            return new ItemStackBuilder(Material.RED_TERRACOTTA).setDisplayName("<bold><red>ERROR</bold>").setDisplayLore(list);
        }
    }
}