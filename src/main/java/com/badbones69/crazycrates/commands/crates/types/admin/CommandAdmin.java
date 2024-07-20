package com.badbones69.crazycrates.commands.crates.types.admin;

import com.badbones69.crazycrates.tasks.PaginationManager;
import com.badbones69.crazycrates.commands.crates.types.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

public class CommandAdmin extends BaseCommand {

    private final PaginationManager paginationManager = this.plugin.getPaginationManager();

    @Command("admin")
    @Permission(value = "crazycrates.admin", def = PermissionDefault.OP)
    public void admin(Player player) {

    }
}