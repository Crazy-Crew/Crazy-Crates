package us.crazycrew.crazycrates.platform.config.impl.messages;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;
import java.util.List;
import static ch.jalu.configme.properties.PropertyInitializer.newListProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class CommandKeys implements SettingsHolder {

    @Override
    public void registerComments(CommentsConfiguration conf) {
        String[] header = {
                "All messages related to commands."
        };

        conf.setComment("command", header);
    }

    @Comment("A list of available placeholders: {prefix}, {crate}, {player}")
    public static final Property<String> opened_a_crate = newProperty("command.open.opened-a-crate", "{prefix}<gray>You have opened the <gold>{crate} for <gold>{player}.");

    @Comment("A list of available placeholders: {prefix}, {amount}, {player}, {keytype}")
    public static final Property<String> gave_a_player_keys = newProperty("command.give.given-player-keys", "{prefix}<gray>You have given <gold>{player} {amount} <gray>key(s).");

    @Comment("A list of available placeholders: {prefix}, {amount}, {amount}, {keytype}")
    public static final Property<String> cannot_give_player_keys = newProperty("command.give.full-inventory", "{prefix}<gray>You have been given <gold>{amount} {key} <gray>virtual key(s) because your inventory was full.");

    @Comment("A list of available placeholders: {prefix}, {amount}, {keytype}")
    public static final Property<String> given_everyone_keys = newProperty("command.give.given-everyone-keys", "{prefix}<gray>You have given everyone <gold>{amount} <gray>key(s).");

    @Comment("A list of available placeholders: {prefix}, {amount}, {player}, {keytype}")
    public static final Property<String> given_offline_player_keys = newProperty("command.give.given-offline-player-keys", "{prefix}<gray>You have given <gold>{amount} <gray>key(s) to the offline player <gold>{player}.");

    @Comment("A list of available placeholders: {prefix}, {amount}, {player}, {keytype}")
    public static final Property<String> take_players_keys = newProperty("command.take.take-player-keys", "{prefix}<gray>You have taken <gold>{amount} <gray>key(s) from <gold>{player}.");

    @Comment("A list of available placeholders: {prefix}, {player}")
    public static final Property<String> cannot_take_keys = newProperty("command.take.cannot-take-keys", "{prefix}<gray>You cannot take key(s) from <gold>{player} <gray>as they are poor.");

    @Comment("A list of available placeholders: {prefix}, {amount}, {player}, {keytype}")
    public static final Property<String> take_offline_player_keys = newProperty("command.take.take-offline-player-keys", "{prefix}<gray>You have taken <gold>{amount} <gray>key(s) from the offline player <gold>{player}.");

    @Comment("A list of available placeholders: {prefix}, {crate}")
    public static final Property<String> no_item_in_hand = newProperty("command.additem.no-item-in-hand", "{prefix}<red>You need to have an item in your hand to add it {crate}.");

    @Comment("A list of available placeholders: {prefix}, {crate}, {prize}")
    public static final Property<String> added_item_with_editor = newProperty("command.additem.add-item-from-hand", "{prefix}<gray>The item has been added to the {crate} in prize #{prize}.");

    public static final Property<String> no_files_to_convert = newProperty("command.convert.no-files-to-convert", "<red>No available plugins to convert files.");

    public static final Property<String> error_converting_files = newProperty("command.convert.error-converting-files", "<red>An error has occurred while trying to convert files. We could not convert <green>{file} <red>so please check the console.");

    public static final Property<String> successfully_converted_files = newProperty("command.successfully-converted-files", "<green>Plugin Conversion has succeeded!");

    @Comment("A list of available placeholders: {prefix}")
    public static final Property<String> reloaded_plugin = newProperty("command.reload.completed", "{prefix}<dark_aqua>You have reloaded the Config and Data Files.");

    @Comment("A list of available placeholders: {prefix}")
    public static final Property<String> transfer_not_enough_keys = newProperty("command.transfer.not-enough-keys", "{prefix}<red>You do not have enough keys to transfer.");

    @Comment("A list of available placeholders: {prefix}, {amount}, {player}, {keytype}, {crate}")
    public static final Property<String> transfer_sent_keys = newProperty("command.transfer.transferred-keys", "{prefix}<gray>You have transferred {amount} {crate} keys to {player}.");

    @Comment("A list of available placeholders: {prefix}, {amount}, {player}, {keytype}, {crate}")
    public static final Property<String> transfer_received_keys = newProperty("command.transfer.transferred-keys-received", "{prefix}<gray>You have received {amount} {crate} keys from {player}.");

    @Comment("A list of available placeholders: {prefix}")
    public static final Property<String> no_virtual_keys = newProperty("command.keys.personal.no-virtual-keys", "{prefix}<bold><dark_gray>(<dark_red>!<dark_gray>)</bold> <gray>You currently do not have any virtual keys.");

    @Comment("A list of available placeholders: {crates_opened}")
    public static final Property<List<String>> virtual_keys_header = newListProperty("command.keys.personal.virtual-keys-header", List.of(
            "<bold><dark_gray>(<gold>!<dark_gray>)</bold> <gray>List of your current number of keys.",
            " <yellow> -> Total Crates Opened: <red>{crates_opened}",
            ""
    ));

    @Comment("A list of available placeholders: {prefix}, {player}")
    public static final Property<String> other_player_no_keys = newProperty("command.keys.other-player.no-virtual-keys", "{prefix}<bold><dark_gray>(<dark_red>!<dark_gray>)</bold> <gray>The player {player} does not have any keys.");

    @Comment("A list of available placeholders: {player}, {crates_opened}")
    public static final Property<List<String>> other_player_header = newListProperty("command.keys.other-player.virtual-keys-header", List.of(
            "<bold><dark_gray>(<gold>!<dark_gray>)</bold> <gray>List of {player}''s current number of keys.",
            " <yellow> -> Total Crates Opened: <red>{crates_opened}",
            ""
    ));

    @Comment("A list of available placeholders: {crate}, {keys}, {crate_opened}")
    public static final Property<String> per_crate = newProperty("command.keys.crate-format", "{crate} <bold><gray>><dark_gray>></bold> <gold>{keys} keys <gray>: Opened <gold>{crate_opened} times");

    @Comment("This requires crazycrates.command.help")
    public static final Property<List<String>> help = newListProperty("command.player-help", List.of(
            "<bold><yellow>Crazy Crates Player Help</bold>",
            "",
            "<gold>/keys view [player] <gray>- <yellow>Check the number of keys a player has.",
            "<gold>/keys <gray>- <yellow>Shows how many keys you have.",
            "<gold>/cc <gray>- <yellow>Opens the menu."
    ));

    @Comment("This requires crazycrates.command.admin.help")
    public static final Property<List<String>> admin_help = newListProperty("command.admin-help", List.of(
            "<bold><red>Crazy Crates Admin Help</bold>",
            "",
            "<gold>/cc additem <crate_name> <prize_number> <chance> [tier] <gray>- <yellow>Add items in-game to a prize in a crate including Cosmic/Casino.",
            "<gold>/cc preview <crate_name> [player] <gray>- <yellow>Opens the preview of a crate for a player.",
            "<gold>/cc list <gray>- <yellow>Lists all crates.",
            "<gold>/cc open <crate_name> <gray>- <yellow>Tries to open a crate for you if you have a key.",
            "<gold>/cc open-others <crate_name> [player] <gray>- <yellow>Tries to open a crate for a player if they have a key.",
            "<gold>/cc transfer <crate_name> [player] [amount <gray>- <yellow>Transfers keys to players you chose.",
            "<gold>/cc debug <gray>- <yellow>Debugs crates",
            "<gold>/cc admin <gray>- <yellow>Shows admin menu",
            "<gold>/cc forceopen <crate_name> [player] <gray>- <yellow>Opens a crate for a player for free.",
            "<gold>/cc mass-open <crate_name> <physical/virtual> [amount] <gray>- <yellow>Mass opens a set amount of crates.",
            "<gold>/cc tp <location> <gray>- <yellow>Teleport to a Crate.",
            "<gold>/cc give <physical/virtual> <crate_name> [amount] [player] <gray>- <yellow>Allows you to take keys from a player.",
            "<gold>/cc set <crate_name> <gray>- <yellow>Set the block you are looking at as a crate.",
            "<gold>/cc set Menu <gray>- <yellow>Set the block you are looking at to open the /cc menu.",
            "<gold>/cc reload <gray>- <yellow>Reloads the config/data files.",
            "<gold>/cc set1/set2 <gray>- <yellow>Sets position <red>#1 <yellow>or <red>#2 <yellow>for when making a new schematic for QuadCrates.",
            "<gold>/cc save <file name> <gray>- <yellow>Create a new nbt file in the schematics folder.",
            "",
            "<gold>/keys view [player] <gray>- <yellow>Check the number of keys a player has.",
            "<gold>/keys <gray>- <yellow>Shows how many keys you have.",
            "<gold>/cc <gray>- <yellow>Opens the menu.",
            "",
            "<gray>You can find a list of permissions @ <yellow>https://docs.crazycrew.us/crazycrates/info/commands/permissions"
    ));
}