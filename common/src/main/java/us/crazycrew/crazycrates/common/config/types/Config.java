package us.crazycrew.crazycrates.common.config.types;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;

import java.util.List;

import static ch.jalu.configme.properties.PropertyInitializer.newListProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

/**
 * @author RyderBelserion
 * @author BadBones69
 *
 * Description: The config.yml options.
 */
public class Config implements SettingsHolder {

    protected Config() {}

    @Override
    public void registerComments(CommentsConfiguration conf) {
        String[] header = {
                "Support: https://discord.gg/badbones-s-live-chat-182615261403283459",
                "Github: https://github.com/Crazy-Crew",
                "",
                "Issues: https://github.com/Crazy-Crew/CrazyCrates/issues",
                "Features: https://github.com/Crazy-Crew/CrazyCrates/issues",
                ""
        };

        String[] deprecation = {
                "",
                "Warning: This section is subject to change so it is considered deprecated.",
                "This is your warning before the change happens.",
                ""
        };

        conf.setComment("crate-settings", header);
        conf.setComment("crate-settings.keys.key-sound", deprecation);
        conf.setComment("gui-settings.filler-items", deprecation);
        conf.setComment("gui-settings.customizer", deprecation);
    }

    @Comment({
            "Warning: The log file as is may cause your server to crash.",
            "It is recommended to clear it occasionally.",
            "",
            "Option will always be set to false by default."
    })
    public static final Property<Boolean> log_to_file = newProperty("crate-settings.crate-actions.log-to-file", false);

    @Comment("Whether you want to log crate actions to console or not.")
    public static final Property<Boolean> log_to_console = newProperty("crate-settings.crate-actions.log-to-console", false);

    @Comment({
            "Whether you want crates to knock you back if you have no keys.",
            "",
            "Warning: This option will be moved to be an option per crate."
    })
    public static final Property<Boolean> crate_knock_back = newProperty("crate-settings.knock-back", true);

    @Comment("Whether to notify the player they were given a virtual key when inventory is not empty.")
    public static final Property<Boolean> give_virtual_keys_message = newProperty("crate-settings.keys.inventory-not-empty.give-virtual-keys-message", true);

    @Comment("Whether to give virtual keys to a player if inventory is not empty.")
    public static final Property<Boolean> give_virtual_keys = newProperty("crate-settings.keys.inventory-not-empty.give-virtual-keys", true);

    @Comment({
            "Whether sound should play or not.",
            "",
            "Warning: This option will be moved to be an option per crate."
    })
    public static final Property<Boolean> key_sound_toggle = newProperty("crate-settings.keys.key-sound.toggle", true);

    @Comment("The sound that plays when a player tries to open a crate without keys.")
    public static final Property<String> key_sound_name = newProperty("crate-settings.keys.key-sound.name", "ENTITY_VILLAGER_NO");

    @Comment("Whether physical crates accept virtual keys or not.")
    public static final Property<Boolean> physical_accepts_virtual = newProperty("crate-settings.keys.physical-accepts-virtual-keys", true);
    @Comment("Whether physical crates accept physical keys or not.")
    public static final Property<Boolean> physical_accepts_physical = newProperty("crate-settings.keys.physical-accepts-physical-keys", true);
    @Comment("Whether virtual crates accept physical keys or not.")
    public static final Property<Boolean> virtual_accepts_physical_keys = newProperty("crate-settings.keys.virtual-accepts-physical-keys", true);

    @Comment({
            "How long a quad crate should be open?",
            "",
            "Warning: This option will be moved to be an option per crate.",
            "Moving this will allow each quad-crate to have different timers.",
            })
    public static final Property<Integer> quad_crate_timer = newProperty("crate-settings.quad-crate.timer", 300);

    @Comment({
            "Whether you want to deny crate usage in X world or not.",
            "",
            "Warning: This will potentially be moved to be an option per crate for more specific control."
            })
    public static final Property<Boolean> disabled_worlds_toggle = newProperty("crate-settings.disabled-worlds.toggle", false);

    @Comment("The list of worlds to deny crate usage in.")
    public static final Property<List<String>> disabled_worlds = newListProperty("crate-settings.disabled-worlds.worlds", List.of(
            "world_nether"
    ));

    @Comment("Whether you want the customizer to be enabled.")
    public static final Property<Boolean> customizer_toggle = newProperty("gui-settings.customizer.toggle", true);

    @Comment("Place any fancy item in the gui including custom items.")
    public static final Property<List<String>> customizer_item_list = newListProperty("gui-settings.customizer.items", List.of(
            "slot:1, item:BLACK_STAINED_GLASS_PANE, name: ",
            "slot:2, item:BLACK_STAINED_GLASS_PANE, name: ",
            "slot:3, item:BLACK_STAINED_GLASS_PANE, name: ",
            "slot:4, item:BLACK_STAINED_GLASS_PANE, name: ",
            "slot:5, item:BLACK_STAINED_GLASS_PANE, name: ",
            "slot:6, item:BLACK_STAINED_GLASS_PANE, name: ",
            "slot:7, item:BLACK_STAINED_GLASS_PANE, name: ",
            "slot:8, item:BLACK_STAINED_GLASS_PANE, name: ",
            "slot:9, item:BLACK_STAINED_GLASS_PANE, name: ",
            "slot:10, item:BLACK_STAINED_GLASS_PANE, name: ",
            "slot:18, item:BLACK_STAINED_GLASS_PANE, name: ",
            "slot:19, item:BLACK_STAINED_GLASS_PANE, name: ",
            "slot:27, item:BLACK_STAINED_GLASS_PANE, name: ",
            "slot:28, item:BLACK_STAINED_GLASS_PANE, name: ",
            "slot:36, item:BLACK_STAINED_GLASS_PANE, name: ",
            "slot:37, item:BLACK_STAINED_GLASS_PANE, name: ",
            "slot:45, item:BLACK_STAINED_GLASS_PANE, name: ",

            "slot:11, item:WHITE_STAINED_GLASS_PANE, name: ",
            "slot:12, item:WHITE_STAINED_GLASS_PANE, name: ",
            "slot:13, item:WHITE_STAINED_GLASS_PANE, name: ",
            "slot:14, item:WHITE_STAINED_GLASS_PANE, name: ",
            "slot:15, item:WHITE_STAINED_GLASS_PANE, name: ",
            "slot:16, item:WHITE_STAINED_GLASS_PANE, name: ",
            "slot:17, item:WHITE_STAINED_GLASS_PANE, name: ",
            "slot:20, item:WHITE_STAINED_GLASS_PANE, name: ",
            "slot:22, item:WHITE_STAINED_GLASS_PANE, name: ",
            "slot:24, item:WHITE_STAINED_GLASS_PANE, name: ",
            "slot:30, item:WHITE_STAINED_GLASS_PANE, name: ",
            "slot:33, item:WHITE_STAINED_GLASS_PANE, name: ",
            "slot:34, item:WHITE_STAINED_GLASS_PANE, name: ",
            "slot:35, item:WHITE_STAINED_GLASS_PANE, name: ",

            "slot:38, item:BLACK_STAINED_GLASS_PANE, name: ",
            "slot:39, item:BLACK_STAINED_GLASS_PANE, name: ",
            "slot:40, item:BLACK_STAINED_GLASS_PANE, name: ",
            "slot:41, item:BLACK_STAINED_GLASS_PANE, name: ",
            "slot:42, item:BLACK_STAINED_GLASS_PANE, name: ",
            "slot:43, item:BLACK_STAINED_GLASS_PANE, name: ",
            "slot:44, item:BLACK_STAINED_GLASS_PANE, name: "));
}