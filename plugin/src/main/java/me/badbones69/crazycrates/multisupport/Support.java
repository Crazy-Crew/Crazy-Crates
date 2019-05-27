package me.badbones69.crazycrates.multisupport;

import org.bukkit.Bukkit;

public enum Support {
	
	PLACEHOLDERAPI("PlaceholderAPI"),
	MVDWPLACEHOLDERAPI("MVdWPlaceholderAPI");
	
	private String name;
	
	private Support(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public Boolean isPluginLoaded() {
		return Bukkit.getServer().getPluginManager().getPlugin(name) != null;
	}
	
}