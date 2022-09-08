package com.badbones69.crazycrates.cratetypes;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.Methods;
import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.common.enums.crates.KeyType;
import com.badbones69.crazycrates.api.utilities.handlers.objects.crates.Crate;
import com.badbones69.crazycrates.api.utilities.handlers.objects.ItemBuilder;
import com.badbones69.crazycrates.api.utilities.ScheduleUtils;
import com.badbones69.crazycrates.api.utilities.handlers.tasks.CrateTaskHandler;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class CsgoCrate implements Listener {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    @Inject private CrazyManager crazyManager;

    // Utilities
    @Inject private Methods methods;
    @Inject private ScheduleUtils scheduleUtils;

    @Inject private CrateTaskHandler crateTaskHandler;

    private void setGlass(Inventory inv) {
        HashMap<Integer, ItemStack> glass = new HashMap<>();

        for (int i = 0; i < 10; i++) {
            if (i < 9 && i != 3) glass.put(i, inv.getItem(i));
        }

        for (int i : glass.keySet()) {
            if (inv.getItem(i) == null) {
                ItemStack item = methods.getRandomPaneColor().setName(" ").build();
                inv.setItem(i, item);
                inv.setItem(i + 18, item);
            }
        }

        for (int i = 1; i < 10; i++) {
            if (i < 9 && i != 4) glass.put(i, inv.getItem(i));
        }

        ItemStack item = methods.getRandomPaneColor().setName(" ").build();
        inv.setItem(0, glass.get(1));
        inv.setItem(18, glass.get(1));
        inv.setItem(1, glass.get(2));
        inv.setItem(1 + 18, glass.get(2));
        inv.setItem(2, glass.get(3));
        inv.setItem(2 + 18, glass.get(3));
        inv.setItem(3, glass.get(5));
        inv.setItem(3 + 18, glass.get(5));
        inv.setItem(4, new ItemBuilder().setMaterial(Material.BLACK_STAINED_GLASS_PANE).setName(" ").build());
        inv.setItem(4 + 18, new ItemBuilder().setMaterial(Material.BLACK_STAINED_GLASS_PANE).setName(" ").build());
        inv.setItem(5, glass.get(6));
        inv.setItem(5 + 18, glass.get(6));
        inv.setItem(6, glass.get(7));
        inv.setItem(6 + 18, glass.get(7));
        inv.setItem(7, glass.get(8));
        inv.setItem(7 + 18, glass.get(8));
        inv.setItem(8, item);
        inv.setItem(8 + 18, item);
    }
    
    public void openCSGO(Player player, Crate crate, KeyType keyType, boolean checkHand) {
        Inventory inv = plugin.getServer().createInventory(null, 27, crate.getFile().getString("Crate.CrateName"));
        setGlass(inv);

        for (int i = 9; i > 8 && i < 18; i++) {
            inv.setItem(i, crate.pickPrize(player).getDisplayItem());
        }

        player.openInventory(inv);

        if (crazyManager.takeKeys(1, player, crate, keyType, checkHand)) {
            startCSGO(player, inv, crate);
        } else {
            methods.failedToTakeKey(player, crate);
            crazyManager.removePlayerFromOpeningList(player);
        }
    }
    
    private void startCSGO(final Player player, final Inventory inv, Crate crate) {
        crateTaskHandler.addTask(player, scheduleUtils.timer(1L, 1L, () -> {
            AtomicInteger time = new AtomicInteger();
            AtomicInteger full = new AtomicInteger();
            AtomicInteger open = new AtomicInteger();

            if (full.incrementAndGet() <= 50) { // When spinning
                moveItems(inv, player, crate);
                setGlass(inv);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
            }

            if (open.incrementAndGet() >= 5) {
                player.openInventory(inv);
                open.set(0);
            }

            if (full.incrementAndGet() > 51) {

                if (slowSpin().contains(time.get())) { // When Slowing Down
                    moveItems(inv, player, crate);
                    setGlass(inv);
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                }

                if (time.incrementAndGet() == 60) { // When done
                    crateTaskHandler.endCrate(player, crate, inv);

                    scheduleUtils.later(40L, () -> {
                        if (player.getOpenInventory().getTopInventory().equals(inv)) player.closeInventory();
                    });
                } else if (time.get() > 60) { // Added this due reports of the prizes spamming when low tps.
                    crateTaskHandler.endCrate(player);
                }
            }
        }));
    }

    private ArrayList<Integer> slowSpin() {
        ArrayList<Integer> slow = new ArrayList<>();
        int full = 120;
        int cut = 15;

        for (int i = 120; cut > 0; full--) {
            if (full <= i - cut || full >= i - cut) {
                slow.add(i);
                i -= cut;
                cut--;
            }
        }

        return slow;
    }
    
    private void moveItems(Inventory inv, Player player, Crate crate) {
        ArrayList<ItemStack> items = new ArrayList<>();

        for (int i = 9; i > 8 && i < 17; i++) {
            items.add(inv.getItem(i));
        }

        inv.setItem(9, crate.pickPrize(player).getDisplayItem());

        for (int i = 0; i < 8; i++) {
            inv.setItem(i + 10, items.get(i));
        }
    }
}