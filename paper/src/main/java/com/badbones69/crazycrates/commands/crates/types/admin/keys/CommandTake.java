package com.badbones69.crazycrates.commands.crates.types.admin.keys;

import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.commands.crates.types.BaseCommand;
import com.ryderbelserion.vital.util.builders.PlayerBuilder;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import us.crazycrew.crazycrates.api.enums.types.KeyType;

public class CommandTake extends BaseCommand {

    @Command("take")
    @Permission(value = "crazycrates.takekey", def = PermissionDefault.OP)
    public void take(CommandSender sender, @Suggestion("keys") String type, @Suggestion("crates") String crateName, @Suggestion("numbers") int amount, @Suggestion("players") PlayerBuilder target) {
        KeyType keyType = getKeyType(sender, type);

        Crate crate = getCrate(sender, crateName, false);

        if (crate == null) {
            return;
        }

        if (amount <= 0) {
            sender.sendRichMessage(Messages.not_a_number.getMessage(sender, "{number}", String.valueOf(amount)));

            return;
        }

        if (target.getPlayer() != null) {
            Player player = target.getPlayer();

            takeKey(sender, player, crate, keyType, amount);

            return;
        }

        takeKey(sender, target.getOfflinePlayer(), crate, keyType, amount);
    }
}