package com.badbones69.crazycrates.paper.api.managers;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.Methods;
import com.badbones69.crazycrates.paper.api.CrazyManager;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.users.BukkitUserManager;
import com.badbones69.crazycrates.paper.support.structures.QuadCrateSpiralHandler;
import com.badbones69.crazycrates.paper.support.structures.StructureHandler;
import com.badbones69.crazycrates.paper.support.structures.blocks.ChestManager;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import us.crazycrew.crazycrates.common.config.types.Config;
import us.crazycrew.crazycrates.common.crates.quadcrates.CrateParticles;
import us.crazycrew.crazycrates.paper.api.plugin.CrazyHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class QuadCrateManager {

    private final @NotNull CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    private final @NotNull CrazyHandler crazyHandler = this.plugin.getCrazyHandler();
    private final @NotNull BukkitUserManager userManager = this.crazyHandler.getUserManager();
    private final @NotNull Methods methods = this.crazyHandler.getMethods();

    private final @NotNull ChestManager chestManager = this.crazyHandler.getChestManager();

    private final @NotNull CrazyManager crazyManager = this.crazyHandler.getCrazyManager();

    private final List<QuadCrateManager> crateSessions = new ArrayList<>();

    private final QuadCrateManager instance;

    // Get the player.
    private final Player player;

    // Check player hand.
    private final boolean checkHand;

    // The crate that is being used.
    private final Crate crate;

    // The key type.
    private final KeyType keyType;

    // Get display rewards.
    private final List<Entity> displayedRewards = new ArrayList<>();

    /**
     * The spawn location.
     * Used to define where the structure will load.
     * Also used to get the center of the structure to teleport the player to.
     */
    private final Location spawnLocation;

    // The last location the player was originally at.
    private final Location lastLocation;

    // Defines the locations of the Chests that will spawn in.
    private final ArrayList<Location> crateLocations = new ArrayList<>();

    // Stores if the crate is open or not.
    private final HashMap<Location, Boolean> cratesOpened = new HashMap<>();

    // Saves all the chests spawned by the QuadCrate task.
    private final HashMap<Location, BlockState> quadCrateChests = new HashMap<>();

    // Saves all the old blocks to restore after.
    private final HashMap<Location, BlockState> oldBlocks = new HashMap<>();

    // Get the particles that will be used to display above the crates.
    private final Color particleColor;
    private final CrateParticles particle;

    // Get the structure handler.
    private final StructureHandler handler;

    /**
     * A constructor to build the quad crate session
     *
     * @param player opening the crate
     * @param crate the player is opening
     * @param keyType the player has
     * @param spawnLocation of the schematic
     * @param lastLocation the player was at
     * @param inHand check the hand of the player
     * @param handler the structure handler instance
     */
    public QuadCrateManager(Player player, Crate crate, KeyType keyType, Location spawnLocation, Location lastLocation, boolean inHand, StructureHandler handler) {
        this.instance = this;
        this.player = player;
        this.crate = crate;
        this.keyType = keyType;
        this.checkHand = inHand;

        this.spawnLocation = spawnLocation;
        this.lastLocation = lastLocation;

        this.handler = handler;

        List<CrateParticles> particles = Arrays.asList(CrateParticles.values());
        this.particle = particles.get(new Random().nextInt(particles.size()));
        this.particleColor = getColors().get(new Random().nextInt(getColors().size()));

        this.crateSessions.add(this.instance);
    }

    /**
     * Start the crate session
     */
    public void startCrate() {
        // Check if it is on a block.
        if (this.spawnLocation.clone().subtract(0, 1, 0).getBlock().getType() == Material.AIR) {
            //TODO() Update message enum.
            //this.player.sendMessage(Messages.NOT_ON_BLOCK.getMessage());
            this.crazyManager.removePlayerFromOpeningList(this.player);
            this.crateSessions.remove(this.instance);
            return;
        }

        // Check if schematic folder is empty.
        if (this.crazyManager.getCrateSchematics().isEmpty()) {
            //TODO() Update message enum.
            //this.player.sendMessage(Messages.NO_SCHEMATICS_FOUND.getMessage());
            this.crazyManager.removePlayerFromOpeningList(this.player);
            this.crateSessions.remove(this.instance);
            return;
        }

        // Check if the blocks are able to be changed.
        List<Location> structureLocations;

        structureLocations = this.handler.getBlocks(this.spawnLocation.clone());

        // Loop through the blocks and check if the blacklist contains the block type.
        // Do not open the crate if the block is not able to be changed.
        assert structureLocations != null;

        for (Location loc : structureLocations) {
            if (this.handler.getBlockBlackList().contains(loc.getBlock().getType())) {
                //TODO() Update message enum.
                //this.player.sendMessage(Messages.NEEDS_MORE_ROOM.getMessage());
                this.crazyManager.removePlayerFromOpeningList(this.player);
                this.crateSessions.remove(this.instance);
                return;
            } else {
                if (!loc.getBlock().getType().equals(Material.AIR)) this.oldBlocks.put(loc.getBlock().getLocation(), loc.getBlock().getState());
            }
        }

        List<Entity> shovePlayers = new ArrayList<>();

        for (Entity entity : this.player.getNearbyEntities(3, 3, 3)) {
            if (entity instanceof Player) {
                for (QuadCrateManager ongoingCrate : this.crateSessions) {
                    if (entity.getUniqueId() == ongoingCrate.player.getUniqueId()) {
                        //TODO() Update message enum.
                        //this.player.sendMessage(Messages.TO_CLOSE_TO_ANOTHER_PLAYER.getMessage("{player}", entity.getName()));
                        this.crazyManager.removePlayerFromOpeningList(this.player);
                        this.crateSessions.remove(instance);
                        return;
                    }
                }

                shovePlayers.add(entity);
            }
        }

        if (!this.userManager.takeKeys(1, this.player.getUniqueId(), this.crate.getName(), this.keyType, this.checkHand)) {
            this.methods.failedToTakeKey(this.player.getName(), this.crate);

            this.crazyManager.removePlayerFromOpeningList(this.player);
            this.crateSessions.remove(instance);
            return;
        }

        if (this.crazyManager.getHologramController() != null) this.crazyManager.getHologramController().removeHologram(this.spawnLocation.getBlock());

        // Shove other players away from the player opening the crate.
        shovePlayers.forEach(entity -> entity.getLocation().toVector().subtract(this.spawnLocation.clone().toVector()).normalize().setY(1));

        // Store the spawned Crates ( Chest Block ) in the ArrayList.
        addCrateLocations(2, 1, 0);
        addCrateLocations(0, 1, 2);

        addCrateLocations(-2, 1, 0);
        addCrateLocations(0, 1, -2);

        // Throws unopened crates in a HashMap.
        this.crateLocations.forEach(loc -> this.cratesOpened.put(loc, false));

        // This holds the quad crate's spawned chests.
        for (Location loc : this.crateLocations) {
            if (this.crateLocations.contains(loc)) this.quadCrateChests.put(loc.clone(), loc.getBlock().getState());
        }

        // Paste the structure in.
        this.handler.pasteStructure(this.spawnLocation.clone());

        this.player.teleport(this.spawnLocation.toCenterLocation().add(0, 1.0, 0));

        this.crazyManager.addQuadCrateTask(this.player, new BukkitRunnable() {
            private final QuadCrateSpiralHandler spiralHandler = new QuadCrateSpiralHandler();

            double radius = 0.0; // Radius of the particle spiral.
            int crateNumber = 0; // The crate number that spawns next.
            int tickTillSpawn = 0; // At tick 60 the crate will spawn and then reset the tick.
            Location particleLocation = crateLocations.get(this.crateNumber).clone().add(.5, 3, .5);
            List<Location> spiralLocationsClockwise = this.spiralHandler.getSpiralLocationClockwise(this.particleLocation);
            List<Location> spiralLocationsCounterClockwise = this.spiralHandler.getSpiralLocationCounterClockwise(this.particleLocation);

            @Override
            public void run() {
                if (this.tickTillSpawn < 60) {
                    spawnParticles(particle, particleColor, spiralLocationsClockwise.get(this.tickTillSpawn), spiralLocationsCounterClockwise.get(this.tickTillSpawn));
                    this.tickTillSpawn++;
                } else {
                    player.playSound(player.getLocation(), Sound.BLOCK_STONE_STEP, 1, 1);
                    Block chest = crateLocations.get(this.crateNumber).getBlock();

                    chest.setType(Material.CHEST);
                    chestManager.rotateChest(chest, this.crateNumber);

                    if (this.crateNumber == 3) { // Last crate has spawned.
                        crazyManager.endQuadCrate(player); // Cancelled when method is called.
                    } else {
                        this.tickTillSpawn = 0;
                        this.crateNumber++;
                        this.radius = 0;
                        this.particleLocation = crateLocations.get(this.crateNumber).clone().add(.5, 3, .5); // Set the new particle location for the new crate
                        this.spiralLocationsClockwise = this.spiralHandler.getSpiralLocationClockwise(this.particleLocation);
                        this.spiralLocationsCounterClockwise = this.spiralHandler.getSpiralLocationCounterClockwise(this.particleLocation);
                    }
                }
            }
        }.runTaskTimer(this.plugin, 0,1));

        this.crazyManager.addCrateTask(this.player, new BukkitRunnable() {
            @Override
            public void run() {
                // End the crate by force.
                endCrateForce(true);
                //TODO() Update message enum.
                //player.sendMessage(Messages.OUT_OF_TIME.getMessage());
            }
        }.runTaskLater(this.plugin, this.crazyHandler.getConfigManager().getConfig().getProperty(Config.quad_crate_timer)));
    }

    /**
     * End the crate gracefully.
     */
    public void endCrate() {
        new BukkitRunnable() {
            @Override
            public void run() {
                // Update spawned crate block states which removes them.
                crateLocations.forEach(location -> quadCrateChests.get(location).update(true, false));

                // Remove displayed rewards.
                displayedRewards.forEach(Entity::remove);

                // Teleport player to last location.
                player.teleport(lastLocation);

                // Remove the structure blocks.
                handler.removeStructure();

                // Restore the old blocks.
                oldBlocks.keySet().forEach(location -> oldBlocks.get(location).update(true, false));

                if (crate.getHologram().isEnabled() && crazyManager.getHologramController() != null) crazyManager.getHologramController().createHologram(spawnLocation.getBlock(), crate);

                // End the crate.
                crazyManager.endCrate(player);

                // Remove the player from the list saying they are opening a crate.
                crazyManager.removePlayerFromOpeningList(player);

                // Remove the "instance" from the crate sessions.
                crateSessions.remove(instance);
            }
        }.runTaskLater(this.plugin, 5);
    }

    /**
     * End the crate by force which cleans everything up.
     *
     * @param removeForce whether to stop the crate session or not
     */
    public void endCrateForce(boolean removeForce) {
        this.oldBlocks.keySet().forEach(location -> this.oldBlocks.get(location).update(true, false));
        this.crateLocations.forEach(location -> this.quadCrateChests.get(location).update(true, false));
        this.displayedRewards.forEach(Entity::remove);
        this.player.teleport(this.lastLocation);

        if (removeForce) {
            this.crazyManager.removePlayerFromOpeningList(this.player);
            crateSessions.remove(this.instance);
        }

        this.handler.removeStructure();
    }

    /**
     * Add a crate location
     *
     * @param x coordinate
     * @param y coordinate
     * @param z coordinate
     */
    public void addCrateLocations(int x, int y, int z) {
        this.crateLocations.add(this.spawnLocation.clone().add(x, y, z));
    }

    /**
     * Get an arraylist of colors
     *
     * @return list of colors
     */
    private List<Color> getColors() {
        return Arrays.asList(
                Color.AQUA, Color.BLACK, Color.BLUE, Color.FUCHSIA, Color.GRAY,
                Color.GREEN, Color.LIME, Color.MAROON, Color.NAVY, Color.OLIVE,
                Color.ORANGE, Color.PURPLE, Color.RED, Color.SILVER, Color.TEAL,
                Color.WHITE, Color.YELLOW);
    }

    /**
     * Spawn particles at 2 specific locations with a customizable color.
     *
     * @param quadCrateParticle the particle to spawn
     * @param particleColor the color of the particle
     * @param location1 the first location of the particle
     * @param location2 the second location of the particle
     */
    private void spawnParticles(CrateParticles quadCrateParticle, Color particleColor, Location location1, Location location2) {
        Particle particle = switch (quadCrateParticle) {
            case FLAME -> Particle.FLAME;
            case VILLAGER_HAPPY -> Particle.VILLAGER_HAPPY;
            case SPELL_WITCH -> Particle.SPELL_WITCH;
            default -> Particle.REDSTONE;
        };

        if (particle == Particle.REDSTONE) {
            location1.getWorld().spawnParticle(particle, location1, 0, new Particle.DustOptions(particleColor, 1));
            location2.getWorld().spawnParticle(particle, location2, 0, new Particle.DustOptions(particleColor, 1));
        } else {
            location1.getWorld().spawnParticle(particle, location1, 0);
            location2.getWorld().spawnParticle(particle, location2, 0);
        }
    }

    /**
     * Get the crate sessions
     *
     * @return list of crate sessions
     */
    public List<QuadCrateManager> getCrateSessions() {
        return this.crateSessions;
    }

    /**
     * Get the player opening the crate
     *
     * @return the player
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Get a list of crate locations.
     *
     * @return list of crate locations
     */
    public List<Location> getCrateLocations() {
        return this.crateLocations;
    }

    /**
     * Get the hashmap of opened crates.
     *
     * @return map of opened crates
     */
    public HashMap<Location, Boolean> getCratesOpened() {
        return this.cratesOpened;
    }

    /**
     * Get the crate object.
     *
     * @return the crate object
     */
    public Crate getCrate() {
        return this.crate;
    }

    /**
     * Get the list of display rewards.
     *
     * @return list of display rewards
     */
    public List<Entity> getDisplayedRewards() {
        return this.displayedRewards;
    }

    /**
     * Check if all crates have opened.
     *
     * @return true if yes otherwise false
     */
    public Boolean allCratesOpened() {
        for (Map.Entry<Location, Boolean> location : this.cratesOpened.entrySet()) {
            if (!location.getValue()) return false;
        }

        return true;
    }
}