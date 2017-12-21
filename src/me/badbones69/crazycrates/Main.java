package me.badbones69.crazycrates;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.enums.CrateType;
import me.badbones69.crazycrates.api.enums.KeyType;
import me.badbones69.crazycrates.api.enums.Messages;
import me.badbones69.crazycrates.api.objects.Crate;
import me.badbones69.crazycrates.api.objects.CrateLocation;
import me.badbones69.crazycrates.controlers.CrateControl;
import me.badbones69.crazycrates.controlers.FileManager;
import me.badbones69.crazycrates.controlers.FileManager.Files;
import me.badbones69.crazycrates.controlers.FireworkDamageAPI;
import me.badbones69.crazycrates.controlers.GUIMenu;
import me.badbones69.crazycrates.controlers.MCUpdate;
import me.badbones69.crazycrates.cratetypes.CSGO;
import me.badbones69.crazycrates.cratetypes.Cosmic;
import me.badbones69.crazycrates.cratetypes.CrateOnTheGo;
import me.badbones69.crazycrates.cratetypes.QuadCrate;
import me.badbones69.crazycrates.cratetypes.QuickCrate;
import me.badbones69.crazycrates.cratetypes.Roulette;
import me.badbones69.crazycrates.cratetypes.War;
import me.badbones69.crazycrates.cratetypes.Wheel;
import me.badbones69.crazycrates.cratetypes.Wonder;
import me.badbones69.crazycrates.multisupport.MVdWPlaceholderAPISupport;
import me.badbones69.crazycrates.multisupport.PlaceholderAPISupport;
import me.badbones69.crazycrates.multisupport.Support;
import me.badbones69.crazycrates.multisupport.Version;
import me.badbones69.crazycrates.multisupport.nms.Events_v1_11_R1_Down;
import me.badbones69.crazycrates.multisupport.nms.Events_v1_12_R1_Up;

public class Main extends JavaPlugin implements Listener {
	
	private MCUpdate mcUpdate;
	private Boolean updateChecker = false;
	private CrazyCrates cc = CrazyCrates.getInstance();
	private FileManager fileManager = FileManager.getInstance();
	
	@Override
	public void onEnable() {
		//Crate Files
		fileManager.logInfo(true)
		.registerDefaultGenerateFiles("Basic.yml", "/Crates")
		.registerDefaultGenerateFiles("Classic.yml", "/Crates")
		.registerDefaultGenerateFiles("Crazy.yml", "/Crates")
		.registerDefaultGenerateFiles("Galactic.yml", "/Crates")
		//Schematics
		.registerDefaultGenerateFiles("Classic.schematic", "/Schematics")
		.registerDefaultGenerateFiles("Nether.schematic", "/Schematics")
		.registerDefaultGenerateFiles("OutDoors.schematic", "/Schematics")
		.registerDefaultGenerateFiles("Sea.schematic", "/Schematics")
		.registerDefaultGenerateFiles("Soul.schematic", "/Schematics")
		.registerDefaultGenerateFiles("Wooden.schematic", "/Schematics")
		//Register all files inside the custom folders.
		.registerCustomFilesFolder("/Crates")
		.registerCustomFilesFolder("/Schematics")
		.setup(this);
		if(!Files.LOCATIONS.getFile().contains("Locations")) {
			Files.LOCATIONS.getFile().set("Locations.Clear", null);
			Files.LOCATIONS.saveFile();
		}
		if(!Files.DATA.getFile().contains("Players")) {
			Files.DATA.getFile().set("Players.Clear", null);
			Files.DATA.saveFile();
		}
		if(Files.CONFIG.getFile().contains("Settings.Update-Checker")) {
			updateChecker = Files.CONFIG.getFile().getBoolean("Settings.Update-Checker");
		}else {
			updateChecker = true;
		}
		cc.loadCrates();
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(this, this);
		pm.registerEvents(new GUIMenu(), this);
		pm.registerEvents(new QuadCrate(), this);
		pm.registerEvents(new War(), this);
		pm.registerEvents(new CSGO(), this);
		pm.registerEvents(new Wheel(), this);
		pm.registerEvents(new Wonder(), this);
		pm.registerEvents(new Cosmic(), this);
		pm.registerEvents(new Roulette(), this);
		pm.registerEvents(new QuickCrate(), this);
		pm.registerEvents(new CrateControl(), this);
		pm.registerEvents(new CrateOnTheGo(), this);
		if(Version.getVersion().comparedTo(Version.v1_12_R1) >= 0) {
			pm.registerEvents(new Events_v1_12_R1_Up(), this);
		}else {
			pm.registerEvents(new Events_v1_11_R1_Down(), this);
		}
		try {
			if(Version.getVersion().getVersionInteger() >= Version.v1_11_R1.getVersionInteger()) {
				pm.registerEvents(new FireworkDamageAPI(this), this);
			}
		}catch(Exception e) {}
		if(Support.hasPlaceholderAPI()) {
			new PlaceholderAPISupport(this).hook();
		}
		if(Support.hasMVdWPlaceholderAPI()) {
			MVdWPlaceholderAPISupport.registerPlaceholders(this);
		}
		if(Bukkit.getServer().getOnlinePlayers() != null) {
			for(Player player : Bukkit.getServer().getOnlinePlayers()) {
				String uuid = player.getUniqueId().toString();
				if(!Files.DATA.getFile().contains("Players." + uuid)) {
					Files.DATA.getFile().set("Players." + uuid + ".Name", player.getName());
					for(String crate : fileManager.getAllCratesNames()) {
						int amount = fileManager.getFile(crate).getFile().getInt("Crate.StartingKeys");
						Files.DATA.getFile().set("Players." + uuid + "." + crate, amount);
					}
					Files.DATA.saveFile();;
				}
			}
		}
		try {
			mcUpdate = new MCUpdate(this, true);
			if(Files.CONFIG.getFile().contains("Settings.Update-Checker")) {
				mcUpdate.checkUpdate(Files.CONFIG.getFile().getBoolean("Settings.Update-Checker"));
			}
		}catch(IOException e) {}
		Methods.hasUpdate();
	}
	
	@Override
	public void onDisable() {
		if(!QuadCrate.crates.isEmpty()) {
			for(Player player : QuadCrate.crates.keySet()) {
				QuadCrate.undoBuild(player);
			}
		}
		if(!QuickCrate.Rewards.isEmpty()) {
			for(Player player : QuickCrate.Rewards.keySet()) {
				QuickCrate.Rewards.get(player).remove();
			}
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLable, String[] args) {
		if(commandLable.equalsIgnoreCase("crazycrates") ||
		commandLable.equalsIgnoreCase("cc") ||
		commandLable.equalsIgnoreCase("crate") ||
		commandLable.equalsIgnoreCase("crates") ||
		commandLable.equalsIgnoreCase("ccrate") ||
		commandLable.equalsIgnoreCase("crazycrate")) {
			if(args.length == 0) {
				if(sender instanceof Player) {
					if(!Methods.permCheck((Player) sender, "Access")) {
						return true;
					}
				}else {
					sender.sendMessage(Messages.MUST_BE_A_PLAYER.getMessage());
					return true;
				}
				GUIMenu.openGUI((Player) sender);
				return true;
			}else if(args.length >= 1) {
				if(args[0].equalsIgnoreCase("help")) {
					if(sender instanceof Player) if(!Methods.permCheck((Player) sender, "Access")) return true;
					sender.sendMessage(Messages.HELP.getMessage());
					return true;
				}else if(args[0].equalsIgnoreCase("reload")) {
					if(sender instanceof Player) if(!Methods.permCheck((Player) sender, "Admin")) return true;
					fileManager.reloadAllFiles();
					fileManager.setup(this);
					if(!Files.LOCATIONS.getFile().contains("Locations")) {
						Files.LOCATIONS.getFile().set("Locations.Clear", null);
						Files.LOCATIONS.saveFile();
					}
					if(!Files.DATA.getFile().contains("Players")) {
						Files.DATA.getFile().set("Players.Clear", null);
						Files.DATA.saveFile();
					}
					cc.loadCrates();
					sender.sendMessage(Messages.RELOAD.getMessage());
					return true;
				}else if(args[0].equalsIgnoreCase("admin")) {
					if(!(sender instanceof Player)) return true;
					Player player = (Player) sender;
					if(!Methods.permCheck(player, "Admin")) return true;
					int size = cc.getCrates().size();
					int slots = 9;
					for(; size > 9; size -= 9)
						slots += 9;
					Inventory inv = Bukkit.createInventory(null, slots, Methods.color("&4&lAdmin Keys"));
					HashMap<ItemStack, ItemStack> keys = new HashMap<ItemStack, ItemStack>();
					for(Crate crate : cc.getCrates()) {
						if(crate.getCrateType() != CrateType.MENU) {
							ItemStack item1 = Methods.addLore(crate.getKey(), "");
							item1 = Methods.addLore(crate.getKey(), "&7&l(&6&l!&7&l) Left click for Physical Key");
							item1 = Methods.addLore(crate.getKey(), "&7&l(&6&l!&7&l) Right click for Virtual Key");
							if(inv.firstEmpty() >= 0) {
								inv.setItem(inv.firstEmpty(), item1);
							}
							keys.put(item1, crate.getKey());
						}
					}
					CrateControl.previewKeys.put(player, keys);
					player.openInventory(inv);
					return true;
				}else if(args[0].equalsIgnoreCase("list")) {
					if(sender instanceof Player) if(!Methods.permCheck((Player) sender, "Admin")) return true;
					String crates = "";
					String brokecrates = "";
					for(Crate crate : cc.getCrates()) {
						crates += "&a" + crate.getName() + "&8, ";
					}
					for(String crate : cc.getBrokeCrates()) {
						brokecrates += "&c" + crate + ".yml&8, ";
					}
					sender.sendMessage(Methods.color("&e&lCrates:&f " + crates));
					if(brokecrates.length() > 0) {
						sender.sendMessage(Methods.color("&6&lBroken Crates:&f " + brokecrates.substring(0, brokecrates.length() - 2)));
					}
					sender.sendMessage(Methods.color("&e&lAll Crate Locations:"));
					sender.sendMessage(Methods.color("&c[ID]&8, &c[Crate]&8, &c[World]&8, &c[X]&8, &c[Y]&8, &c[Z]"));
					int line = 1;
					for(CrateLocation loc : cc.getCrateLocations()) {
						Crate crate = loc.getCrate();
						String world = loc.getLocation().getWorld().getName();
						int x = loc.getLocation().getBlockX();
						int y = loc.getLocation().getBlockY();
						int z = loc.getLocation().getBlockZ();
						sender.sendMessage(Methods.color("&8[&b" + line + "&8]: " + "&c" + loc.getID() + "&8, &c" + crate.getName() + "&8, &c" + world + "&8, &c" + x + "&8, &c" + y + "&8, &c" + z));
						line++;
					}
					return true;
				}else if(args[0].equalsIgnoreCase("tp")) {// /cc TP <Location>
					if(sender instanceof Player) if(!Methods.permCheck((Player) sender, "Admin")) return true;
					if(args.length == 2) {
						String Loc = args[1];
						if(!Files.LOCATIONS.getFile().contains("Locations")) {
							Files.LOCATIONS.getFile().set("Locations.Clear", null);
							Files.LOCATIONS.saveFile();
						}
						for(String name : Files.LOCATIONS.getFile().getConfigurationSection("Locations").getKeys(false)) {
							if(name.equalsIgnoreCase(Loc)) {
								World W = Bukkit.getServer().getWorld(Files.LOCATIONS.getFile().getString("Locations." + name + ".World"));
								int X = Files.LOCATIONS.getFile().getInt("Locations." + name + ".X");
								int Y = Files.LOCATIONS.getFile().getInt("Locations." + name + ".Y");
								int Z = Files.LOCATIONS.getFile().getInt("Locations." + name + ".Z");
								Location loc = new Location(W, X, Y, Z);
								((Player) sender).teleport(loc.add(.5, 0, .5));
								sender.sendMessage(Methods.color(Methods.getPrefix() + "&7You have been teleported to &6" + name + "&7."));
								return true;
							}
						}
						sender.sendMessage(Methods.color(Methods.getPrefix() + "&cThere is no location called &6" + Loc + "&c."));
						return true;
					}
					sender.sendMessage(Methods.color(Methods.getPrefix() + "&c/cc TP <Location Name>"));
					return true;
				}else if(args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("s")) { // /Crate Set <Crate>
					if(sender instanceof Player) if(!Methods.permCheck((Player) sender, "Admin")) return true;
					if(!(sender instanceof Player)) {
						sender.sendMessage(Messages.MUST_BE_A_PLAYER.getMessage());
						return true;
					}
					if(args.length == 2) {
						Player player = (Player) sender;
						String c = args[1]; //Crate
						for(Crate crate : cc.getCrates()) {
							if(crate.getName().equalsIgnoreCase(c)) {
								Block block = player.getTargetBlock((Set<Material>) null, 5);
								if(block.isEmpty()) {
									player.sendMessage(Messages.MUST_BE_LOOKING_AT_A_BLOCK.getMessage());
									return true;
								}
								CrazyCrates.getInstance().addCrateLocation(block.getLocation(), crate);
								HashMap<String, String> placeholders = new HashMap<>();
								placeholders.put("%crate%", crate.getName());
								placeholders.put("%prefix%", Methods.getPrefix());
								player.sendMessage(Messages.CREATED_PHYSICAL_CRATE.getMessage(placeholders));
								return true;
							}
						}
						HashMap<String, String> placeholders = new HashMap<>();
						placeholders.put("%crate%", c);
						sender.sendMessage(Messages.NOT_A_CRATE.getMessage(placeholders));
						return true;
					}
					sender.sendMessage(Methods.color(Methods.getPrefix() + "&c/cc Set <Crate>"));
					return true;
				}else if(args[0].equalsIgnoreCase("preview")) {// /cc Preview <Crate> [Player]
					if(sender instanceof Player) {
						if(!Methods.permCheck((Player) sender, "preview")) {
							return true;
						}
					}
					if(args.length >= 2) {
						Crate crate = null;
						Player player = null;
						for(Crate c : cc.getCrates()) {
							if(crate.getCrateType() != CrateType.MENU) {
								if(c.getName().equalsIgnoreCase(args[1])) {
									crate = c;
								}
							}
						}
						if(crate != null) {
							if(crate.getCrateType() != CrateType.MENU) {
								if(args.length >= 3) {
									if(Methods.isOnline(args[2], sender)) {
										player = Methods.getPlayer(args[2]);
									}else {
										return true;
									}
								}else {
									if(!(sender instanceof Player)) {
										sender.sendMessage(Methods.color(Methods.getPrefix() + "&c/Crate Preview <Crate> [Player]"));
										return true;
									}else {
										player = (Player) sender;
									}
								}
								GUIMenu.openPreview(player, crate);
							}
							return true;
						}
					}
					sender.sendMessage(Methods.color(Methods.getPrefix() + "&c/Crate Preview <Crate> [Player]"));
					return true;
				}else if(args[0].equalsIgnoreCase("open")) {// /cc Open <Crate> [Player]
					if(sender instanceof Player) if(!Methods.permCheck((Player) sender, "Admin")) return true;
					if(args.length >= 2) {
						for(Crate crate : cc.getCrates()) {
							if(crate.getName().equalsIgnoreCase(args[1])) {
								Player player = null;
								if(args.length >= 3) {
									if(Methods.isOnline(args[2], sender)) {
										player = Methods.getPlayer(args[2]);
									}else {
										return true;
									}
								}else {
									if(!(sender instanceof Player)) {
										sender.sendMessage(Methods.color(Methods.getPrefix() + "&c/Crate Open <Crate> [Player]"));
										return true;
									}else {
										player = (Player) sender;
									}
								}
								if(crate.getCrateType() == CrateType.MENU) {
									sender.sendMessage(Methods.color(Methods.getPrefix() + "&c/Crate Open <Crate> [Player]"));
									return true;
								}
								if(CrazyCrates.getInstance().isInOpeningList(player)) {
									sender.sendMessage(Messages.CRATE_ALREADY_OPENED.getMessage());
									return true;
								}
								CrateType type = crate.getCrateType();
								if(type != null) {
									if(type != CrateType.CRATE_ON_THE_GO && type != CrateType.QUICK_CRATE && type != CrateType.FIRE_CRACKER) {
										cc.openCrate(player, crate, KeyType.FREE_KEY, player.getLocation());
										HashMap<String, String> placeholders = new HashMap<>();
										placeholders.put("%crate%", crate.getName());
										placeholders.put("%player%", player.getName());
										sender.sendMessage(Messages.OPENED_A_CRATE.getMessage(placeholders));
										return true;
									}else {
										sender.sendMessage(Messages.CANT_BE_A_VIRTUAL_CRATE.getMessage());
										return true;
									}
								}else {
									HashMap<String, String> placeholders = new HashMap<>();
									placeholders.put("%crate%", args[1]);
									sender.sendMessage(Messages.NOT_A_CRATE.getMessage(placeholders));
									return true;
								}
							}
						}
						HashMap<String, String> placeholders = new HashMap<>();
						placeholders.put("%crate%", args[1]);
						sender.sendMessage(Messages.NOT_A_CRATE.getMessage(placeholders));
						return true;
					}
					sender.sendMessage(Methods.color(Methods.getPrefix() + "&c/Crate Open <Crate> [Player]"));
					return true;
				}else if(args[0].equalsIgnoreCase("giveall")) {// /Crate GiveAll <Physical/Virtual> <Crate> <Amount>
					if(sender instanceof Player) if(!Methods.permCheck((Player) sender, "Admin")) return true;
					if(args.length >= 3) {
						int amount = 1;
						if(args.length >= 4) {
							if(!Methods.isInt(args[3])) {
								HashMap<String, String> placeholders = new HashMap<>();
								placeholders.put("%number%", args[3]);
								sender.sendMessage(Messages.NOT_A_NUMBER.getMessage(placeholders));
								return true;
							}
							amount = Integer.parseInt(args[3]);
						}
						String type = args[1];
						if(!(type.equalsIgnoreCase("Virtual") || type.equalsIgnoreCase("V") || type.equalsIgnoreCase("Physical") || type.equalsIgnoreCase("P"))) {
							sender.sendMessage(Methods.color(Methods.getPrefix() + "&cPlease use Virtual/V or Physical/P for a Key type."));
							return true;
						}
						Crate crate = cc.getCrateFromName(args[2]);
						if(crate != null) {
							if(crate.getCrateType() != CrateType.MENU) {
								HashMap<String, String> placeholders = new HashMap<>();
								placeholders.put("%amount%", amount + "");
								sender.sendMessage(Messages.GIVEN_EVERYONE_KEYS.getMessage(placeholders));
								for(Player p : Bukkit.getServer().getOnlinePlayers()) {
									if(crate.getCrateType() == CrateType.CRATE_ON_THE_GO) {
										p.getInventory().addItem(crate.getKey());
										return true;
									}
									if(type.equalsIgnoreCase("Virtual") || type.equalsIgnoreCase("V")) {
										CrazyCrates.getInstance().addKeys(amount, p, crate, KeyType.VIRTUAL_KEY);
									}
									if(type.equalsIgnoreCase("Physical") || type.equalsIgnoreCase("P")) {
										CrazyCrates.getInstance().addKeys(amount, p, crate, KeyType.PHYSICAL_KEY);
									}
								}
								return true;
							}
						}
						HashMap<String, String> placeholders = new HashMap<>();
						placeholders.put("%crate%", args[2]);
						sender.sendMessage(Messages.NOT_A_CRATE.getMessage(placeholders));
						return true;
					}
					sender.sendMessage(Methods.color(Methods.getPrefix() + "&c/Crate GiveAll <Physical/Virtual> <Crate> <Amount>"));
					return true;
				}else if(args[0].equalsIgnoreCase("give")) {// /Crate Give <Physical/Virtual> <Crate> [Amount] [Player]
					if(sender instanceof Player) if(!Methods.permCheck((Player) sender, "Admin")) return true;
					KeyType type = null;
					if(args.length >= 2) {
						type = KeyType.getFromName(args[1]);
					}
					if(type == null || type == KeyType.FREE_KEY) {
						sender.sendMessage(Methods.color(Methods.getPrefix() + "&cPlease use Virtual/V or Physical/P for a Key type."));
						return true;
					}
					if(args.length == 3) {
						Crate crate = cc.getCrateFromName(args[2]);
						if(crate != null) {
							if(crate.getCrateType() != CrateType.MENU) {
								if(!(sender instanceof Player)) {
									sender.sendMessage(Messages.MUST_BE_A_PLAYER.getMessage());
									return true;
								}
								HashMap<String, String> placeholders = new HashMap<>();
								placeholders.put("%amount%", "1");
								placeholders.put("%player%", sender.getName());
								sender.sendMessage(Messages.GIVEN_A_PLAYER_KEYS.getMessage(placeholders));
								if(crate.getCrateType() == CrateType.CRATE_ON_THE_GO) {
									((Player) sender).getInventory().addItem(crate.getKey());
								}else {
									if(type == KeyType.VIRTUAL_KEY) {
										cc.addKeys(1, (Player) sender, crate, KeyType.VIRTUAL_KEY);
									}else if(type == KeyType.PHYSICAL_KEY) {
										cc.addKeys(1, (Player) sender, crate, KeyType.PHYSICAL_KEY);
									}
								}
								return true;
							}
						}
						HashMap<String, String> placeholders = new HashMap<>();
						placeholders.put("%crate%", args[2]);
						sender.sendMessage(Messages.NOT_A_CRATE.getMessage(placeholders));
						return true;
					}else if(args.length == 4) {
						if(!(sender instanceof Player)) {
							sender.sendMessage(Messages.MUST_BE_A_PLAYER.getMessage());
							return true;
						}
						if(!Methods.isInt(args[3])) {
							HashMap<String, String> placeholders = new HashMap<>();
							placeholders.put("%number%", args[3]);
							sender.sendMessage(Messages.NOT_A_NUMBER.getMessage(placeholders));
							return true;
						}
						int amount = Integer.parseInt(args[3]);
						Crate crate = cc.getCrateFromName(args[2]);
						if(crate != null) {
							if(crate.getCrateType() != CrateType.MENU) {
								HashMap<String, String> placeholders = new HashMap<>();
								placeholders.put("%amount%", amount + "");
								placeholders.put("%player%", sender.getName());
								sender.sendMessage(Messages.GIVEN_A_PLAYER_KEYS.getMessage(placeholders));
								if(crate.getCrateType() == CrateType.CRATE_ON_THE_GO) {
									((Player) sender).getInventory().addItem(crate.getKey(amount));
								}else {
									if(type == KeyType.VIRTUAL_KEY) {
										cc.addKeys(amount, (Player) sender, crate, KeyType.VIRTUAL_KEY);
									}else if(type == KeyType.PHYSICAL_KEY) {
										cc.addKeys(amount, (Player) sender, crate, KeyType.PHYSICAL_KEY);
									}
								}
								return true;
							}
						}
						HashMap<String, String> placeholders = new HashMap<>();
						placeholders.put("%crate%", args[2]);
						sender.sendMessage(Messages.NOT_A_CRATE.getMessage(placeholders));
						return true;
					}else if(args.length == 5) {
						if(!Methods.isInt(args[3])) {
							HashMap<String, String> placeholders = new HashMap<>();
							placeholders.put("%number%", args[3]);
							sender.sendMessage(Messages.NOT_A_NUMBER.getMessage(placeholders));
							return true;
						}
						int amount = Integer.parseInt(args[3]);
						Player target = Methods.getPlayer(args[4]);
						Crate crate = cc.getCrateFromName(args[2]);
						if(crate != null) {
							if(crate.getCrateType() != CrateType.MENU) {
								if(crate.getCrateType() == CrateType.CRATE_ON_THE_GO) {
									target.getInventory().addItem(crate.getKey(amount));
								}else {
									if(type == KeyType.VIRTUAL_KEY) {
										if(target != null) {
											cc.addKeys(amount, target, crate, KeyType.VIRTUAL_KEY);
										}else {
											if(!cc.addOfflineKeys(args[4], crate, amount)) {
												sender.sendMessage(Messages.INTERNAL_ERROR.getMessage());
												return true;
											}else {
												HashMap<String, String> placeholders = new HashMap<>();
												placeholders.put("%amount%", amount + "");
												placeholders.put("%player%", args[4]);
												sender.sendMessage(Messages.GIVEN_OFFLINE_PLAYER_KEYS.getMessage(placeholders));
												return true;
											}
										}
									}else if(type == KeyType.PHYSICAL_KEY) {
										if(target != null) {
											cc.addKeys(amount, target, crate, KeyType.PHYSICAL_KEY);
										}else {
											if(!cc.addOfflineKeys(args[4], crate, amount)) {
												sender.sendMessage(Messages.INTERNAL_ERROR.getMessage());
												return true;
											}else {
												HashMap<String, String> placeholders = new HashMap<>();
												placeholders.put("%amount%", amount + "");
												placeholders.put("%player%", args[4]);
												sender.sendMessage(Messages.GIVEN_OFFLINE_PLAYER_KEYS.getMessage(placeholders));
												return true;
											}
										}
									}
								}
								HashMap<String, String> placeholders = new HashMap<>();
								placeholders.put("%amount%", amount + "");
								placeholders.put("%player%", target.getName());
								sender.sendMessage(Messages.GIVEN_A_PLAYER_KEYS.getMessage(placeholders));
								return true;
							}
						}
						HashMap<String, String> placeholders = new HashMap<>();
						placeholders.put("%crate%", args[2]);
						sender.sendMessage(Messages.NOT_A_CRATE.getMessage(placeholders));
						return true;
					}
					sender.sendMessage(Methods.color(Methods.getPrefix() + "&c/Crate Give <Physical/Virtual> <Crate> [Amount] [Player]"));
					return true;
				}else if(args[0].equalsIgnoreCase("take")) {// /Crate Give <Physical/Virtual> <Crate> [Amount] [Player]
					if(sender instanceof Player) if(!Methods.permCheck((Player) sender, "Admin")) return true;
					KeyType type = null;
					if(args.length >= 2) {
						type = KeyType.getFromName(args[1]);
					}
					if(type == null || type == KeyType.FREE_KEY) {
						sender.sendMessage(Methods.color(Methods.getPrefix() + "&cPlease use Virtual/V or Physical/P for a Key type."));
						return true;
					}
					if(args.length == 3) {
						Crate crate = cc.getCrateFromName(args[2]);
						if(crate != null) {
							if(crate.getCrateType() != CrateType.MENU) {
								if(!(sender instanceof Player)) {
									sender.sendMessage(Messages.MUST_BE_A_PLAYER.getMessage());
									return true;
								}
								HashMap<String, String> placeholders = new HashMap<>();
								placeholders.put("%amount%", "1");
								placeholders.put("%player%", sender.getName());
								sender.sendMessage(Messages.TAKE_A_PLAYER_KEYS.getMessage(placeholders));
								if(type == KeyType.VIRTUAL_KEY) {
									cc.takeKeys(1, (Player) sender, crate, KeyType.VIRTUAL_KEY);
								}else if(type == KeyType.PHYSICAL_KEY) {
									cc.takeKeys(1, (Player) sender, crate, KeyType.PHYSICAL_KEY);
								}
								return true;
							}
						}
						HashMap<String, String> placeholders = new HashMap<>();
						placeholders.put("%crate%", args[2]);
						sender.sendMessage(Messages.NOT_A_CRATE.getMessage(placeholders));
						return true;
					}else if(args.length == 4) {
						if(!(sender instanceof Player)) {
							sender.sendMessage(Messages.MUST_BE_A_PLAYER.getMessage());
							return true;
						}
						if(!Methods.isInt(args[3])) {
							HashMap<String, String> placeholders = new HashMap<>();
							placeholders.put("%number%", args[3]);
							sender.sendMessage(Messages.NOT_A_NUMBER.getMessage(placeholders));
							return true;
						}
						int amount = Integer.parseInt(args[3]);
						Crate crate = cc.getCrateFromName(args[2]);
						if(crate != null) {
							if(crate.getCrateType() != CrateType.MENU) {
								HashMap<String, String> placeholders = new HashMap<>();
								placeholders.put("%amount%", amount + "");
								placeholders.put("%player%", sender.getName());
								sender.sendMessage(Messages.TAKE_A_PLAYER_KEYS.getMessage(placeholders));
								if(type == KeyType.VIRTUAL_KEY) {
									cc.takeKeys(amount, (Player) sender, crate, KeyType.VIRTUAL_KEY);
								}else if(type == KeyType.PHYSICAL_KEY) {
									cc.takeKeys(amount, (Player) sender, crate, KeyType.PHYSICAL_KEY);
								}
								return true;
							}
						}
						HashMap<String, String> placeholders = new HashMap<>();
						placeholders.put("%crate%", args[2]);
						sender.sendMessage(Messages.NOT_A_CRATE.getMessage(placeholders));
						return true;
					}else if(args.length == 5) {
						if(!Methods.isInt(args[3])) {
							HashMap<String, String> placeholders = new HashMap<>();
							placeholders.put("%number%", args[3]);
							sender.sendMessage(Messages.NOT_A_NUMBER.getMessage(placeholders));
							return true;
						}
						int amount = Integer.parseInt(args[3]);
						Player target = Methods.getPlayer(args[4]);
						Crate crate = cc.getCrateFromName(args[2]);
						if(crate != null) {
							if(crate.getCrateType() != CrateType.MENU) {
								if(type == KeyType.VIRTUAL_KEY) {
									if(target != null) {
										HashMap<String, String> placeholders = new HashMap<>();
										placeholders.put("%amount%", amount + "");
										placeholders.put("%player%", target.getName());
										sender.sendMessage(Messages.TAKE_A_PLAYER_KEYS.getMessage(placeholders));
										cc.takeKeys(amount, target, crate, KeyType.VIRTUAL_KEY);
									}else {
										if(!cc.takeOfflineKeys(args[4], crate, amount)) {
											sender.sendMessage(Messages.INTERNAL_ERROR.getMessage());
											return true;
										}else {
											HashMap<String, String> placeholders = new HashMap<>();
											placeholders.put("%amount%", amount + "");
											placeholders.put("%player%", args[4]);
											sender.sendMessage(Messages.TAKE_OFFLINE_PLAYER_KEYS.getMessage(placeholders));
											return true;
										}
									}
								}else if(type == KeyType.PHYSICAL_KEY) {
									if(target != null) {
										HashMap<String, String> placeholders = new HashMap<>();
										placeholders.put("%amount%", amount + "");
										placeholders.put("%player%", target.getName());
										sender.sendMessage(Messages.TAKE_A_PLAYER_KEYS.getMessage(placeholders));
										cc.takeKeys(amount, target, crate, KeyType.PHYSICAL_KEY);
									}else {
										HashMap<String, String> placeholders = new HashMap<>();
										placeholders.put("%player%", args[4]);
										sender.sendMessage(Messages.NOT_ONLINE.getMessage(placeholders));
									}
								}
								return true;
							}
						}
						HashMap<String, String> placeholders = new HashMap<>();
						placeholders.put("%crate%", args[2]);
						sender.sendMessage(Messages.NOT_A_CRATE.getMessage(placeholders));
						return true;
					}
					sender.sendMessage(Methods.color(Methods.getPrefix() + "&c/Crate Take <Physical/Virtual> <Crate> [Amount] [Player]"));
					return true;
				}
			}
			sender.sendMessage(Methods.color(Methods.getPrefix() + "&cPlease do /cc help for more info."));
			return true;
		}
		return false;
	}
	
	public static Plugin getPlugin() {
		return Bukkit.getPluginManager().getPlugin("CrazyCrates");
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		cc.loadOfflinePlayersKeys(player);
		new BukkitRunnable() {
			@Override
			public void run() {
				if(player.getName().equals("BadBones69")) {
					player.sendMessage(Methods.getPrefix() + Methods.color("&7This server is running your Crazy Crates Plugin. " + "&7It is running version &av" + Bukkit.getServer().getPluginManager().getPlugin("CrazyCrates").getDescription().getVersion() + "&7."));
				}
				if(player.isOp() && updateChecker) {
					Methods.hasUpdate(player);
				}
			}
		}.runTaskLaterAsynchronously(this, 40);
	}
	
}