package com.badbones69.crazycrates.paper.cratetypes;

import us.crazycrew.crazycrates.paper.CrazyCrates;
import us.crazycrew.crazycrates.paper.api.crates.CrateManager;
import com.badbones69.crazycrates.paper.api.events.PlayerPrizeEvent;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.ItemBuilder;
import com.badbones69.crazycrates.paper.api.objects.Prize;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import us.crazycrew.crazycrates.paper.utils.MiscUtils;
import us.crazycrew.crazycrates.paper.utils.MsgUtils;
import java.util.HashMap;

public class War implements Listener {
    
    private static final String crateNameString = "Crate.CrateName";
    private static final CrazyCrates plugin = CrazyCrates.getPlugin(CrazyCrates.class);

    private static final CrateManager crateManager = plugin.getCrateManager();

    private static HashMap<ItemStack, String> colorCodes;
    private static final HashMap<Player, Boolean> canPick = new HashMap<>();
    private static final HashMap<Player, Boolean> canClose = new HashMap<>();
    
    public static void openWarCrate(Player player, Crate crate, KeyType keyType, boolean checkHand) {
        String crateName = MsgUtils.sanitizeColor(crate.getFile().getString(crateNameString));
        Inventory inv = plugin.getServer().createInventory(null, 9, crateName);
        setRandomPrizes(player, inv, crate, crateName);
        InventoryView inventoryView = player.openInventory(inv);
        canPick.put(player, false);
        canClose.put(player, false);

        if (!plugin.getCrazyHandler().getUserManager().takeKeys(1, player.getUniqueId(), crate.getName(), keyType, checkHand)) {
            MiscUtils.failedToTakeKey(player, crate);
            crateManager.removePlayerFromOpeningList(player);
            canClose.remove(player);
            canPick.remove(player);
            return;
        }

        startWar(player, inv, crate, inventoryView.getTitle());
    }
    
    private static void startWar(final Player player, final Inventory inv, final Crate crate, final String inventoryTitle) {
        crateManager.addCrateTask(player, new BukkitRunnable() {
            int full = 0;
            int open = 0;
            
            @Override
            public void run() {
                if (full < 25) {
                    setRandomPrizes(player, inv, crate, inventoryTitle);
                    player.playSound(player.getLocation(), Sound.BLOCK_LAVA_POP, 1, 1);
                }

                open++;

                if (open >= 3) {
                    player.openInventory(inv);
                    open = 0;
                }

                full++;

                if (full == 26) { // Finished Rolling
                    player.playSound(player.getLocation(), Sound.BLOCK_LAVA_POP, 1, 1);
                    setRandomGlass(player, inv, inventoryTitle);
                    canPick.put(player, true);
                }
            }
        }.runTaskTimer(plugin, 1, 3));
    }
    
    private static void setRandomPrizes(Player player, Inventory inv, Crate crate, String inventoryTitle) {
        if (crateManager.isInOpeningList(player) && inventoryTitle.equalsIgnoreCase(MsgUtils.sanitizeColor(crateManager.getOpeningCrate(player).getFile().getString(crateNameString)))) {
            for (int i = 0; i < 9; i++) {
                inv.setItem(i, crate.pickPrize(player).getDisplayItem());
            }
        }
    }
    
    private static void setRandomGlass(Player player, Inventory inv, String inventoryTitle) {
        if (crateManager.isInOpeningList(player) && inventoryTitle.equalsIgnoreCase(MsgUtils.sanitizeColor(crateManager.getOpeningCrate(player).getFile().getString(crateNameString)))) {

            if (colorCodes == null) colorCodes = getColorCode();

            ItemBuilder itemBuilder = MiscUtils.getRandomPaneColor();
            itemBuilder.setName("&" + colorCodes.get(itemBuilder.build()) + "&l???");
            ItemStack item = itemBuilder.build();

            for (int i = 0; i < 9; i++) {
                inv.setItem(i, item);
            }
        }
    }
    
    private static HashMap<ItemStack, String> getColorCode() {
        HashMap<ItemStack, String> colorCodes = new HashMap<>();

        colorCodes.put(new ItemBuilder().setMaterial(Material.WHITE_STAINED_GLASS_PANE).build(), "f");
        colorCodes.put(new ItemBuilder().setMaterial(Material.ORANGE_STAINED_GLASS_PANE).build(), "6");
        colorCodes.put(new ItemBuilder().setMaterial(Material.MAGENTA_STAINED_GLASS_PANE).build(), "d");
        colorCodes.put(new ItemBuilder().setMaterial(Material.LIGHT_BLUE_STAINED_GLASS_PANE).build(), "3");
        colorCodes.put(new ItemBuilder().setMaterial(Material.YELLOW_STAINED_GLASS_PANE).build(), "e");
        colorCodes.put(new ItemBuilder().setMaterial(Material.LIME_STAINED_GLASS_PANE).build(), "a");
        colorCodes.put(new ItemBuilder().setMaterial(Material.PINK_STAINED_GLASS_PANE).build(), "c");
        colorCodes.put(new ItemBuilder().setMaterial(Material.GRAY_STAINED_GLASS_PANE).build(), "7");
        colorCodes.put(new ItemBuilder().setMaterial(Material.LIGHT_GRAY_STAINED_GLASS_PANE).build(), "7");
        colorCodes.put(new ItemBuilder().setMaterial(Material.CYAN_STAINED_GLASS_PANE).build(), "3");
        colorCodes.put(new ItemBuilder().setMaterial(Material.PURPLE_STAINED_GLASS_PANE).build(), "5");
        colorCodes.put(new ItemBuilder().setMaterial(Material.BLUE_STAINED_GLASS_PANE).build(), "9");
        colorCodes.put(new ItemBuilder().setMaterial(Material.BROWN_STAINED_GLASS_PANE).build(), "6");
        colorCodes.put(new ItemBuilder().setMaterial(Material.GREEN_STAINED_GLASS_PANE).build(), "2");
        colorCodes.put(new ItemBuilder().setMaterial(Material.RED_STAINED_GLASS_PANE).build(), "4");
        colorCodes.put(new ItemBuilder().setMaterial(Material.BLACK_STAINED_GLASS_PANE).build(), "8");

        return colorCodes;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        final Player player = (Player) e.getWhoClicked();
        final Inventory inv = e.getInventory();

        if (canPick.containsKey(player) && crateManager.isInOpeningList(player)) {
            Crate crate = crateManager.getOpeningCrate(player);

            if (crate.getCrateType() == CrateType.war && canPick.get(player)) {
                ItemStack item = e.getCurrentItem();

                if (item != null && item.getType().toString().contains(Material.GLASS_PANE.toString())) {
                    final int slot = e.getRawSlot();
                    Prize prize = crate.pickPrize(player);
                    inv.setItem(slot, prize.getDisplayItem());

                    if (crateManager.hasCrateTask(player)) crateManager.endCrate(player);

                    canPick.remove(player);
                    canClose.put(player, true);
                    plugin.getCrazyHandler().getPrizeManager().givePrize(player, prize, crate);

                    if (prize.useFireworks()) MiscUtils.spawnFirework(player.getLocation().add(0, 1, 0), null);

                    plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, crate.getName(), prize));
                    crateManager.removePlayerFromOpeningList(player);
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
                    // Sets all other non-picked prizes to show what they could have been.

                    crateManager.addCrateTask(player, new BukkitRunnable() {
                        @Override
                        public void run() {

                            for (int i = 0; i < 9; i++) {
                                if (i != slot) inv.setItem(i, crate.pickPrize(player).getDisplayItem());
                            }

                            if (crateManager.hasCrateTask(player)) crateManager.endCrate(player);

                            // Removing other items then the prize.
                            crateManager.addCrateTask(player, new BukkitRunnable() {
                                @Override
                                public void run() {

                                    for (int i = 0; i < 9; i++) {
                                        if (i != slot) inv.setItem(i, new ItemStack(Material.AIR));
                                    }

                                    if (crateManager.hasCrateTask(player)) crateManager.endCrate(player);

                                    // Closing the inventory when finished.
                                    crateManager.addCrateTask(player, new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            if (crateManager.hasCrateTask(player)) crateManager.endCrate(player);

                                            player.closeInventory();
                                        }
                                    }.runTaskLater(plugin, 30));
                                }
                            }.runTaskLater(plugin, 30));
                        }
                    }.runTaskLater(plugin, 30));
                }
            }
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();

        if (canClose.containsKey(player) && canClose.get(player)) {
            for (Crate crate : crateManager.getCrates()) {
                if (crate.getCrateType() == CrateType.war && e.getView().getTitle().equalsIgnoreCase(MsgUtils.sanitizeColor(crate.getFile().getString(crateNameString)))) {
                    canClose.remove(player);

                    if (crateManager.hasCrateTask(player)) crateManager.endCrate(player);
                }
            }
        }
    }
}