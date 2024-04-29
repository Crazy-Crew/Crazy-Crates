package com.badbones69.crazycrates.api.objects;

import com.badbones69.crazycrates.api.enums.PersistentKeys;
import com.badbones69.crazycrates.api.builders.ItemBuilder;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.persistence.PersistentDataType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Prize {

    private final List<ItemStack> items = new ArrayList<>();

    private List<String> permissions = new ArrayList<>();
    private ItemBuilder displayItem = new ItemBuilder();
    private final List<String> commands;
    private final List<String> messages;
    private boolean firework = false;
    private String crateName = "";
    private final String prizeName;
    private int maxRange = 100;
    private final String prizeNumber;
    private int chance = 0;

    private List<Tier> tiers = new ArrayList<>();
    private final List<ItemBuilder> builders;
    private Prize alternativePrize;

    private final ConfigurationSection section;

    public Prize(ConfigurationSection section, List<Tier> tierPrizes, String crateName, Prize alternativePrize) {
        this.section = section;

        this.prizeNumber = section.getName();

        this.crateName = crateName;

        List<?> list = section.getList("Editor-Items");

        if (list != null) {
            for (Object key : list) {
                this.items.add((ItemStack) key);
            }
        }

        this.builders = ItemBuilder.convertStringList(this.section.getStringList("Items"), this.prizeNumber);

        this.tiers = tierPrizes;

        this.alternativePrize = alternativePrize;

        this.prizeName = section.getString("DisplayName", WordUtils.capitalizeFully(section.getString("DisplayItem", "STONE").replaceAll("_", " ")));
        this.maxRange = section.getInt("MaxRange", 100);
        this.chance = section.getInt("Chance", 50);
        this.firework = section.getBoolean("Firework", false);

        this.messages = section.contains("Messages") ? section.getStringList("Messages") : Collections.emptyList();
        this.commands = section.contains("Commands") ? section.getStringList("Commands") : Collections.emptyList();

        this.permissions = section.contains("BlackListed-Permissions") ? section.getStringList("BlackListed-Permissions") : Collections.emptyList();

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
    public Prize(String prizeName, String prizeNumber, ConfigurationSection section) {
        this.prizeName = prizeName;

        this.builders = ItemBuilder.convertStringList(section.getStringList("Items"), prizeNumber);

        this.messages = section.getStringList("Messages");
        this.commands = section.getStringList("Commands");

        this.prizeNumber = prizeNumber;

        this.section = section;
    }
    
    /**
     * @return the name of the prize.
     */
    public String getPrizeName() {
        return this.prizeName;
    }

    /**
     * @return the prize number.
     */
    public String getPrizeNumber() {
        return this.prizeNumber;
    }

    /**
     * @return the display item that is shown for the preview and the winning prize.
     */
    public ItemStack getDisplayItem() {
        ItemStack itemStack = this.displayItem.build();

        itemStack.editMeta(itemMeta -> itemMeta.getPersistentDataContainer().set(PersistentKeys.crate_prize.getNamespacedKey(), PersistentDataType.STRING, this.prizeNumber));

        return itemStack;
    }

    /**
     * @return the display item that is shown for the preview and the winning prize.
     */
    public ItemStack getDisplayItem(Player player) {
        ItemStack itemStack = this.displayItem.setTarget(player).build();

        itemStack.editMeta(itemMeta -> itemMeta.getPersistentDataContainer().set(PersistentKeys.crate_prize.getNamespacedKey(), PersistentDataType.STRING, this.prizeNumber));

        return itemStack;
    }

    /**
     * @return the ItemBuilder of the display item.
     */
    public ItemBuilder getDisplayItemBuilder() {
        return this.displayItem;
    }
    
    /**
     * @return the list of tiers the prize is in.
     */
    public List<Tier> getTiers() {
        return this.tiers;
    }
    
    /**
     * @return the messages sent to the player.
     */
    public List<String> getMessages() {
        return this.messages;
    }
    
    /**
     * @return the commands that are run when the player wins.
     */
    public List<String> getCommands() {
        return this.commands;
    }
    
    /**
     * @return the Editor ItemStacks that are given to the player that wins.
     */
    public List<ItemStack> getItems() {
        return this.items;
    }
    
    /**
     * @return the ItemBuilders for all the custom items made from the Items: option.
     */
    public List<ItemBuilder> getItemBuilders() {
        return this.builders;
    }
    
    /**
     * @return the name of the crate the prize is in.
     */
    public String getCrateName() {
        return this.crateName;
    }
    
    /**
     * @return the chance the prize has of being picked.
     */
    public int getChance() {
        return this.chance;
    }
    
    /**
     * @return the max range of the prize.
     */
    public int getMaxRange() {
        return this.maxRange;
    }
    
    /**
     * @return true if a firework explosion is played and false if not.
     */
    public boolean useFireworks() {
        return this.firework;
    }
    
    /**
     * @return the alternative prize the player wins if they have a blacklist permission.
     */
    public Prize getAlternativePrize() {
        return this.alternativePrize;
    }
    
    /**
     * @return true if the prize doesn't have an alternative prize and false if it does.
     */
    public boolean hasAlternativePrize() {
        return this.alternativePrize == null;
    }
    
    /**
     * @return true if they prize has blacklist permissions and false if not.
     */
    public boolean hasPermission(Player player) {
        if (player.isOp()) {
            return false;
        }

        for (String permission : this.permissions) {
            if (player.hasPermission(permission)) return true;
        }

        return false;
    }

    private ItemBuilder display() {
        ItemBuilder builder = new ItemBuilder();

        try {
            String material = this.section.getString("DisplayItem", "RED_TERRACOTTA");
            int amount = this.section.getInt("DisplayAmount", 1);
            String nbt = this.section.getString("DisplayNbt");

            if (nbt != null && !nbt.isEmpty()) {
                builder.setMaterial(material).setAmount(amount).setTag(nbt);

                NamespacedKey cratePrize = PersistentKeys.crate_prize.getNamespacedKey();

                ItemMeta itemMeta = builder.getItemMeta();

                itemMeta.getPersistentDataContainer().set(cratePrize, PersistentDataType.STRING, this.section.getName());

                builder.setItemMeta(itemMeta);

                return builder;
            }

            List<String> lore = this.section.getStringList("Lore");
            boolean isGlowing = this.section.getBoolean("Glowing", false);
            boolean isUnbreakable = this.section.getBoolean("Unbreakable", false);
            boolean hideItemFlags = this.section.getBoolean("HideItemFlags", false);
            List<String> itemFlags = this.section.getStringList("Flags");
            List<String> patterns = this.section.getStringList("Patterns");
            String playerName = this.section.getString("Player", "");

            builder.setMaterial(material)
                    .setAmount(amount)
                    .setName(this.prizeName)
                    .setLore(lore)
                    .setGlow(isGlowing)
                    .setUnbreakable(isUnbreakable)
                    .hideItemFlags(hideItemFlags)
                    .addItemFlags(itemFlags)
                    .addPatterns(patterns)
                    .setPlayerName(playerName);

            NamespacedKey cratePrize = PersistentKeys.crate_prize.getNamespacedKey();

            ItemMeta itemMeta = builder.getItemMeta();

            itemMeta.getPersistentDataContainer().set(cratePrize, PersistentDataType.STRING, this.section.getName());

            builder.setItemMeta(itemMeta);

            int displayDamage = this.section.getInt("DisplayDamage", 0);

            builder.setDamage(displayDamage);

            if (this.section.contains("DisplayTrim.Pattern")) {
                NamespacedKey key = null;

                String trimPattern = this.section.getString("DisplayTrim.Pattern");

                if (trimPattern != null) {
                    key = NamespacedKey.minecraft(trimPattern.toLowerCase());
                }

                if (key != null) {
                    TrimPattern registry = Registry.TRIM_PATTERN.get(key);
                    builder.setTrimPattern(registry);
                }
            }

            if (this.section.contains("DisplayTrim.Material")) {
                NamespacedKey key = null;

                String trimMaterial = this.section.getString("DisplayTrim.Material");

                if (trimMaterial != null) {
                    key = NamespacedKey.minecraft(trimMaterial.toLowerCase());
                }

                if (key != null) {
                    TrimMaterial registry = Registry.TRIM_MATERIAL.get(key);
                    builder.setTrimMaterial(registry);
                }
            }

            if (this.section.contains("DisplayEnchantments")) {
                for (String name : this.section.getStringList("DisplayEnchantments")) {
                    Enchantment enchantment = MiscUtils.getEnchantment(name.split(":")[0]);

                    if (enchantment != null) {
                        builder.addEnchantment(enchantment, Integer.parseInt(name.split(":")[1]), true);
                    }
                }
            }

            return builder;
        } catch (Exception exception) {
            List<String> list = new ArrayList<>() {{
               add("<red>There was an error with one of your prizes!");
               add("<red>The reward in question is labeled: <yellow>" + section.getName() + " <red>in crate: <yellow>" + crateName);
               add("<red>Name of the reward is " + section.getString("DisplayName"));
               add("<red>If you are confused, Stop by our discord for support!");
            }};

            return new ItemBuilder(new ItemStack(Material.RED_TERRACOTTA)).setName("<bold><red>ERROR</bold>").setLore(list);
        }
    }
}