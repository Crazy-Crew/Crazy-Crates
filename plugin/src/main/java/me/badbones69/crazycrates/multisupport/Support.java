package me.badbones69.crazycrates.multisupport;

import org.bukkit.Bukkit;

public enum Support {
    
    PLACEHOLDERAPI("PlaceholderAPI"),
    MVDWPLACEHOLDERAPI("MVdWPlaceholderAPI"),
    CRATESPLUS("CratesPlus"),
    HOLOGRAPHIC_DISPLAYS("HolographicDisplays"),
    HOLOGRAMS("Holograms"),
    DECENT_HOLOGRAMS("DecentHolograms");
    
    private final String name;
    
    Support(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isPluginLoaded() {
        return Bukkit.getServer().getPluginManager().getPlugin(name) != null;
    }
    
}