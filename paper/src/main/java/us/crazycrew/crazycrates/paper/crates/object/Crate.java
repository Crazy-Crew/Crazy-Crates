package us.crazycrew.crazycrates.paper.crates.object;

import us.crazycrew.crazycrates.paper.CrazyCrates;
import us.crazycrew.crazycrates.paper.crates.config.CrateConfig;
import java.util.List;

public class Crate {

    private final CrazyCrates plugin;

    private final String fileName;

    private final boolean isEnabled;

    private final String crateType;
    private final String crateName;

    private final int startingKeys;
    private final int requiredKeys;
    private final int maxMassOpen;

    private final boolean isCrateInMenu;

    private final int crateMenuRow;
    private final int crateMenuColumn;

    private final boolean isOpeningBroadcast;
    private final String openingBroadcast;

    private final boolean isPrizeMessageEnabled;
    private final List<String> prizeMessages;

    private final String crateItemType;
    private final boolean isCrateItemGlowing;
    private final String crateItemName;
    private final List<String> crateItemLore;

    private final boolean isPreviewEnabled;
    private final String previewName;
    private final int previewRows;
    private final boolean isPreviewGlassEnabled;
    private final String previewGlassName;
    private final String previewGlassType;

    private final String physicalKeyName;
    private final List<String> physicalKeyLore;
    private final String physicalKeyItem;
    private final boolean isPhysicalKeyGlowing;

    private final boolean isHologramEnabled;
    private final double hologramHeight;
    private final List<String> hologramMessages;

    private final List<Prize> prizes;

    public Crate(CrazyCrates plugin, CrateConfig crateConfig) {
        this.plugin = plugin;

        this.fileName = crateConfig.getFile().getName().replace(".yml", "");

        this.isEnabled = crateConfig.isEnabled();

        this.crateType = crateConfig.getCrateType();
        this.crateName = crateConfig.getCrateName();

        this.startingKeys = crateConfig.getStartingKeys();
        this.requiredKeys = crateConfig.getRequiredKeys();
        this.maxMassOpen = crateConfig.getMaxMassOpen();

        this.isCrateInMenu = crateConfig.isCrateInMenu();
        this.crateMenuRow = crateConfig.getCrateRow();
        this.crateMenuColumn = crateConfig.getCrateColumn();

        this.isOpeningBroadcast = crateConfig.isOpeningBroadcast();
        this.openingBroadcast = crateConfig.getOpeningBroadcast();

        this.isPrizeMessageEnabled = crateConfig.isPrizeMessageEnabled();
        this.prizeMessages = crateConfig.getPrizeMessages();

        this.crateItemType = crateConfig.getCrateItem();
        this.isCrateItemGlowing = crateConfig.isCrateItemGlowing();
        this.crateItemName = crateConfig.getCrateItemName();
        this.crateItemLore = crateConfig.getCrateItemLore();

        this.isPreviewEnabled = crateConfig.isPreviewEnabled();
        this.previewName = crateConfig.getPreviewName();
        this.previewRows = crateConfig.getPreviewRows();
        this.isPreviewGlassEnabled = crateConfig.isPreviewGlassEnabled();
        this.previewGlassName = crateConfig.getPreviewGlassName();
        this.previewGlassType = crateConfig.getPreviewGlassType();

        this.physicalKeyName = crateConfig.getPhysicalKeyName();
        this.physicalKeyLore = crateConfig.getPhysicalKeyLore();
        this.physicalKeyItem = crateConfig.getPhysicalKeyItem();
        this.isPhysicalKeyGlowing = crateConfig.isPhysicalKeyGlowing();

        this.isHologramEnabled = crateConfig.isHologramEnabled();
        this.hologramHeight = crateConfig.getHologramHeight();
        this.hologramMessages = crateConfig.getHologramMessage();

        this.prizes = crateConfig.getPrizes();
    }

    public String getFileName() {
        return this.fileName;
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }

    public String getCrateType() {
        return this.crateType;
    }

    public String getCrateName() {
        return this.crateName;
    }

    public int getStartingKeys() {
        return this.startingKeys;
    }

    public int getRequiredKeys() {
        return this.requiredKeys;
    }

    public int getMaxMassOpen() {
        return this.maxMassOpen;
    }

    /**
     * The crate display section.
     */
    public boolean isCrateInMenu() {
        return this.isCrateInMenu;
    }

    public int getCrateMenuColumn() {
        return this.crateMenuColumn;
    }

    public int getCrateMenuRow() {
        return this.crateMenuRow;
    }

    public String getCrateItemType() {
        return this.crateItemType;
    }

    public boolean isCrateItemGlowing() {
        return this.isCrateItemGlowing;
    }

    public String getCrateItemName() {
        return this.crateItemName;
    }

    public List<String> getCrateItemLore() {
        return this.crateItemLore;
    }

    /**
     * The broadcast section.
     */
    public boolean isOpeningBroadcast() {
        return this.isOpeningBroadcast;
    }

    public String getOpeningBroadcast() {
        return this.openingBroadcast;
    }

    /**
     * The default message section.
     */
    public boolean isPrizeMessageEnabled() {
        return this.isPrizeMessageEnabled;
    }

    public List<String> getPrizeMessages() {
        return this.prizeMessages;
    }

    /**
     * Preview section.
     */
    public boolean isPreviewEnabled() {
        return this.isPreviewEnabled;
    }

    public String getPreviewName() {
        return this.previewName;
    }

    public int getPreviewRows() {
        return this.previewRows;
    }

    public boolean isPreviewGlassEnabled() {
        return this.isPreviewGlassEnabled;
    }

    public String getPreviewGlassName() {
        return this.previewGlassName;
    }

    public String getPreviewGlassType() {
        return this.previewGlassType;
    }

    /**
     * The key section.
     */
    public String getPhysicalKeyName() {
        return this.physicalKeyName;
    }

    public List<String> getPhysicalKeyLore() {
        return this.physicalKeyLore;
    }

    public String getPhysicalKeyItem() {
        return this.physicalKeyItem;
    }

    public boolean isPhysicalKeyGlowing() {
        return this.isPhysicalKeyGlowing;
    }

    /**
     * The hologram section.
     */
    public boolean isHologramEnabled() {
        return this.isHologramEnabled;
    }

    public double getHologramHeight() {
        return this.hologramHeight;
    }

    public List<String> getHologramMessages() {
        return this.hologramMessages;
    }

    public List<Prize> getPrizes() {
        return this.prizes;
    }
}