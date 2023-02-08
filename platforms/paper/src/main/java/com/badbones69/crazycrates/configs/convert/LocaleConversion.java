package com.badbones69.crazycrates.configs.convert;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.configs.Locale;
import com.badbones69.crazycrates.utils.FileUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.util.List;

public class LocaleConversion {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    public void convertMessages() {
        // The messages.yml
        File input = new File(plugin.getDirectory() + "/messages.yml");

        // The old configuration of messages.yml
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(input);

        if (yamlConfiguration.getString("Messages.No-Teleporting") == null && !input.exists()) {
            this.plugin.getLogger().warning(Locale.getConfig(this.plugin).getName() + " is up to date!");
            return;
        }

        // All the values of the old file.
        final String unknownCommand = yamlConfiguration.getString("Messages.Unknown-Command");
        final String noTeleporting = yamlConfiguration.getString("Messages.No-Teleporting");

        final String noCommandsWhileInCrate = yamlConfiguration.getString("Messages.No-Commands-While-In-Crate");

        final String noKeys = yamlConfiguration.getString("Messages.No-Key");
        final String noVirtualKeys = yamlConfiguration.getString("Messages.No-Virtual-Key");

        final String noPrizesFound = yamlConfiguration.getString("Messages.No-Prizes-Found");
        final String noSchematicsFound = yamlConfiguration.getString("Messages.No-Schematics-Found");

        final String internalError = yamlConfiguration.getString("Messages.Internal-Error");

        final String mustBePlayer = yamlConfiguration.getString("Messages.Must-Be-A-Player");
        final String mustBeConsole = yamlConfiguration.getString("Messages.Must-Be-A-Console-Sender");
        final String mustBeLookingAtBlock = yamlConfiguration.getString("Messages.Must-Be-Looking-At-A-Block");

        final String featureDisabled = yamlConfiguration.getString("Messages.Feature-Disabled");

        final String correctUsage = yamlConfiguration.getString("Messages.Correct-Usage");

        org.simpleyaml.configuration.file.YamlConfiguration configuration = Locale.getConfiguration(plugin);

        configuration.set("misc.unknown-command", unknownCommand);
        configuration.set("misc.no-teleporting", noTeleporting);
        configuration.set("misc.no-commands", noCommandsWhileInCrate);
        configuration.set("misc.no-keys", noKeys);
        configuration.set("misc.no-virtual-keys", noVirtualKeys);

        configuration.set("misc.feature-disabled", featureDisabled);
        configuration.set("misc.correct-usage", correctUsage);

        configuration.set("errors.no-prizes-found", noPrizesFound);
        configuration.set("errors.no-schematics-found", noSchematicsFound);

        configuration.set("errors.internal-error", internalError);

        configuration.set("player.requirements.must-be-player", mustBePlayer);
        configuration.set("player.requirements.must-be-console-sender", mustBeConsole);
        configuration.set("player.requirements.must-be-looking-at-block", mustBeLookingAtBlock);

        final String playerNotOnline = yamlConfiguration.getString("Messages.Not-Online");

        final String samePlayer = yamlConfiguration.getString("Messages.Same-Player");

        final String noPermission = yamlConfiguration.getString("Messages.No-Permission");

        final String inventoryFull = yamlConfiguration.getString("Messages.Inventory-Full");

        final String obtainingKeys = yamlConfiguration.getString("Messages.Obtaining-Keys");

        final String closeAnotherPlayer = yamlConfiguration.getString("Messages.To-Close-To-Another-Player");

        configuration.set("player.target-not-online", playerNotOnline);
        configuration.set("player.target-same-player", samePlayer);
        configuration.set("player.no-permission", noPermission);
        configuration.set("player.inventory-not-empty", inventoryFull);
        configuration.set("player.obtaining-keys", obtainingKeys);
        configuration.set("player.too-close-to-another-player", closeAnotherPlayer);

        final String notACrate = yamlConfiguration.getString("Messages.Not-A-Crate");
        final String notANumber = yamlConfiguration.getString("Messages.Not-A-Number");
        final String notOnBlock = yamlConfiguration.getString("Messages.Not-On-Block");
        final String outOfTime = yamlConfiguration.getString("Messages.Out-Of-Time");

        final String previewDisabled = yamlConfiguration.getString("Messages.Preview-Disabled");

        final String crateAlreadyOpened = yamlConfiguration.getString("Messages.Crate-Already-Opened");

        final String quickCrateInUse = yamlConfiguration.getString("Messages.Quick-Crate-In-Use");

        final String cannotBeVirtualCrate = yamlConfiguration.getString("Messages.Cant-Be-A-Virtual-Crate");
        final String needsRoom = yamlConfiguration.getString("Messages.Needs-More-Room");

        final String worldDisabled = yamlConfiguration.getString("Messages.World-Disabled");

        final List<String> createdPhysicalCrate = yamlConfiguration.getStringList("Messages.Created-Physical-Crate");
        final String removedPhysCrate = yamlConfiguration.getString("Messages.Removed-Physical-Crate");

        configuration.set("crates.requirements.not-a-crate", notACrate);
        configuration.set("crates.requirements.not-a-number", notANumber);
        configuration.set("crates.not-on-block", notOnBlock);
        configuration.set("crates.out-of-time", outOfTime);
        configuration.set("crates.crate-preview-disabled", previewDisabled);
        configuration.set("crates.crate-already-open", crateAlreadyOpened);
        configuration.set("crates.crate-in-use", quickCrateInUse);
        configuration.set("crates.cannot-be-a-virtual-crate", cannotBeVirtualCrate);
        configuration.set("crates.need-more-room", needsRoom);
        configuration.set("crates.world-disabled", worldDisabled);
        configuration.set("crates.physical-crate.created", createdPhysicalCrate);
        configuration.set("crates.physical-crate.removed", removedPhysCrate);

        final String openedCrate = yamlConfiguration.getString("Messages.Opened-A-Crate");

        configuration.set("command.open.opened-a-crate", openedCrate);

        final String givenPlayerKeys = yamlConfiguration.getString("Messages.Given-A-Player-Keys");
        final String cannotGivePlayerKeys = yamlConfiguration.getString("Messages.Cannot-Give-Player-Keys");
        final String givenEveryoneKeys = yamlConfiguration.getString("Messages.Given-Everyone-Keys");
        final String givenOfflinePlayerKeys = yamlConfiguration.getString("Messages.Given-Offline-Player-Keys");

        configuration.set("command.give.given-player-keys", givenPlayerKeys);
        configuration.set("command.give.cannot-give-player-keys-because-inventory-not-empty", cannotGivePlayerKeys);
        configuration.set("command.give.given-everyone-keys", givenEveryoneKeys);
        configuration.set("command.give.given-offline-player-keys", givenOfflinePlayerKeys);

        final String takePlayerKeys = yamlConfiguration.getString("Messages.Take-A-Player-Keys");
        final String takeOfflineKeys = yamlConfiguration.getString("Messages.Take-Offline-Player-Keys");

        configuration.set("command.take.take-player-keys", takePlayerKeys);
        configuration.set("command.take.take-offline-player-keys", takeOfflineKeys);

        final String noItemInHand = yamlConfiguration.getString("Messages.No-Item-Hand");
        final String addedItem = yamlConfiguration.getString("Messages.Added-Item-With-Editor");

        configuration.set("command.additem.no-item-in-hand", noItemInHand);
        configuration.set("command.additem.add-item-from-hand", addedItem);

        final String filesConvertedNone = yamlConfiguration.getString("Messages.Files-Converted.No-Files-To-Convert");
        final String filesConvertedError = yamlConfiguration.getString("Messages.Files-Converted.Error-Converting-Files");

        configuration.set("command.convert.no-files-to-convert", filesConvertedNone);
        configuration.set("command.convert.error-converting-files", filesConvertedError);

        final String reload = yamlConfiguration.getString("Messages.Reload");

        configuration.set("command.reload.reload-complete", reload);

        final String transferKeys = yamlConfiguration.getString("Messages.Transfer-Keys.Not-Enough-Keys");
        final String transferredKeys = yamlConfiguration.getString("Messages.Transfer-Keys.Transferred-Keys");
        final String gotTransferKeys = yamlConfiguration.getString("Messages.Transfer-Keys.Received-Transferred-Keys");

        configuration.set("command.transfer.not-enough-keys", transferKeys);
        configuration.set("command.transfer.transferred-keys", transferredKeys);
        configuration.set("command.transfer.transferred-keys-received", gotTransferKeys);

        final String personalNoVirtualKeys = yamlConfiguration.getString("Messages.Keys.Personal.No-Virtual-Keys");
        final List<String> personalHeader = yamlConfiguration.getStringList("Messages.Keys.Personal.Header");
        final String otherPlayer = yamlConfiguration.getString("Messages.Keys.Other-Player.No-Virtual-Keys");
        final List<String> otherPlayerHeader = yamlConfiguration.getStringList("Messages.Keys.Other-Player.Header");
        final String perCrate = yamlConfiguration.getString("Messages.Keys.Per-Crate");

        configuration.set("command.keys.personal.no-virtual-keys", personalNoVirtualKeys);
        configuration.set("command.keys.personal.virtual-keys-header", personalHeader);
        configuration.set("command.keys.other-player.no-virtual-keys", otherPlayer);
        configuration.set("command.keys.other-player.virtual-keys-header", otherPlayerHeader);

        configuration.set("command.keys.crate-format", perCrate);

        FileUtils.copyFile(new File(this.plugin.getDirectory() + "/locale/locale-en.yml"), input, configuration, this.plugin);
    }
}