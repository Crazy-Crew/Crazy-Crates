package com.badbones69.crazycrates.api.objects;

import com.badbones69.crazycrates.api.enums.PersistentKeys;
import com.badbones69.crazycrates.api.builders.ItemBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public class Tier {

    private final ItemBuilder item;
    private final int maxRange;
    private final String name;
    private final List<String> lore;
    private final String coloredName;
    private final int chance;
    private final int slot;

    public Tier(@NotNull final String tier, @NotNull final ConfigurationSection section) {
        this.name = tier;

        this.coloredName = section.getString("Name", "");

        this.lore = section.getStringList("Lore"); // this returns an empty list if not found anyway.

        this.item = new ItemBuilder().setMaterial(section.getString("Item", "chest")).hideItemFlags(section.getBoolean("HideItemFlags", false));

        this.chance = section.getInt("Chance");
        this.maxRange = section.getInt("MaxRange", 100);

        this.slot = section.getInt("Slot");
    }
    
    /**
     * @return name of the tier.
     */
    public @NotNull final String getName() {
        return this.name;
    }

    /**
     * @return colored name of the tier.
     */
    public @NotNull final String getColoredName() {
        return this.coloredName;
    }

    /**
     * @return the colored glass pane.
     */
    public @NotNull final ItemBuilder getItem() {
        return this.item;
    }
    
    /**
     * @return the chance of being picked.
     */
    public final int getChance() {
        return this.chance;
    }
    
    /**
     * @return the range of max possible chances.
     */
    public final int getMaxRange() {
        return this.maxRange;
    }

    /**
     * @return slot in the inventory.
     */
    public final int getSlot() {
        return this.slot;
    }

    /**
     * @return the tier item shown in the preview.
     */
    public @NotNull final ItemStack getTierItem(Player target) {
        if (target != null) this.item.setTarget(target);

        this.item.setDisplayName(this.coloredName).setDisplayLore(this.lore).setString(PersistentKeys.preview_tier_button.getNamespacedKey(), this.name);

        return this.item.build();
    }
}