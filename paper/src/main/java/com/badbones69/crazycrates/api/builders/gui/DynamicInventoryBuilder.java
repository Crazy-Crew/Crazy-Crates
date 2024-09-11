package com.badbones69.crazycrates.api.builders.gui;

import com.badbones69.crazycrates.api.objects.Crate;
import com.ryderbelserion.vital.paper.api.builders.gui.interfaces.Gui;
import com.ryderbelserion.vital.paper.api.builders.gui.interfaces.GuiItem;
import com.ryderbelserion.vital.paper.api.builders.gui.types.PaginatedGui;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public abstract class DynamicInventoryBuilder extends InventoryBuilder {

    private final PaginatedGui gui;
    private final Player player;
    private final Crate crate;

    public DynamicInventoryBuilder(final Player player, final String title, final int rows) {
        super(player);

        this.gui = Gui.paginated().setTitle(title).setRows(rows).disableInteractions().create();

        this.player = player;
        this.crate = null;
    }

    public DynamicInventoryBuilder(final Player player, final Crate crate, final String title, final int rows) {
        super(player);

        this.gui = Gui.paginated().setTitle(title).setRows(rows).disableInteractions().create();

        this.player = player;
        this.crate = crate;
    }

    public DynamicInventoryBuilder(final Player player, final Crate crate) {
        super(player);

        this.gui = Gui.paginated().setTitle(crate.getPreviewName()).setRows(crate.getPreviewTierCrateRows()).disableInteractions().create();

        this.player = player;
        this.crate = crate;
    }

    public abstract void open();

    /**
     * Gets the {@link Player}.
     *
     * @return {@link Player}
     */
    public final Player getPlayer() {
        return this.player;
    }

    /**
     * Gets the {@link Crate}.
     *
     * @return {@link Crate}
     */
    public @Nullable Crate getCrate() {
        return this.crate;
    }

    /**
     * Gets the {@link PaginatedGui}.
     *
     * @return {@link PaginatedGui}
     */
    public final PaginatedGui getGui() {
        return this.gui;
    }

    // Adds the back button
    public void setBackButton(final int row, final int column) {
        if (this.gui.getCurrentPageNumber() > 1) {
            this.gui.setItem(row, column, new GuiItem(this.inventoryManager.getBackButton(this.player, this.gui), event -> {
                event.setCancelled(true);

                this.gui.previous();

                final int page = this.gui.getCurrentPageNumber();

                if (page > 1) {
                    setBackButton(row, column);
                } else {
                    if (this.crate.isBorderToggle()) {
                        this.gui.setItem(row, column, new GuiItem(this.crate.getBorderItem().getStack()));
                    }
                }

                if (page < this.gui.getMaxPages()) {
                    setNextButton(6, 6);
                }
            }));
        }
    }

    // Adds the next button
    public void setNextButton(final int row, final int column) {
        if (this.gui.getCurrentPageNumber() < this.gui.getMaxPages()) {
            this.gui.setItem(row, column, new GuiItem(this.inventoryManager.getNextButton(this.player, this.gui), event -> {
                event.setCancelled(true);

                this.gui.next();

                final int page = this.gui.getCurrentPageNumber();

                if (page < this.gui.getMaxPages()) {
                    setNextButton(row, column);
                } else {
                    if (this.crate.isBorderToggle()) {
                        this.gui.setItem(row, column, new GuiItem(this.crate.getBorderItem().getStack()));
                    }
                }

                if (page > 1) {
                    setBackButton(6, 4);
                } else {
                    if (this.crate.isBorderToggle()) {
                        this.gui.setItem(6, 4, new GuiItem(this.crate.getBorderItem().getStack()));
                    }
                }
            }));
        }
    }
}