package com.badbones69.crazycrates.api.enums.other;

import com.badbones69.crazycrates.CrazyCrates;

public enum Plugins {

    oraxen("Oraxen"),

    nexo("Nexo"),

    items_adder("ItemsAdder"),

    head_database("HeadDatabase"),

    cmi("CMI"),

    fancy_holograms("FancyHolograms"),

    decent_holograms("DecentHolograms"),

    factions_uuid("FactionsUUID"),

    vault("Vault"),

    yard_watch("YardWatch"),

    world_guard("WorldGuard"),

    mcmmo("McMMO"),

    placeholder_api("PlaceholderAPI"),

    luckperms("LuckPerms");

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final String name;

    Plugins(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return this.plugin.getServer().getPluginManager().isPluginEnabled(this.name);
    }

    public String getName() {
        return this.name;
    }
}