package com.badbones69.crazycrates.support.libs;

import com.badbones69.crazycrates.CrazyCrates;

public enum PluginSupport {
    
    PLACEHOLDERAPI("PlaceholderAPI"),
    HOLOGRAPHIC_DISPLAYS("HolographicDisplays"),
    DECENT_HOLOGRAMS("DecentHolograms"),
    ITEMS_ADDER("ItemsAdder");
    
    private final String name;

    private static final CrazyCrates plugin = CrazyCrates.getPlugin();
    
    PluginSupport(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isPluginLoaded() {
        return plugin.getServer().getPluginManager().getPlugin(name) != null;
    }
}