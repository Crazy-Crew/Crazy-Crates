package com.badbones69.crazycrates.support.structures.blocks;

import com.badbones69.crazycrates.support.structures.interfaces.ChestControl;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.block.data.Directional;

public class ChestStateHandler implements ChestControl {
    @Override
    public void openChest(Block block, boolean forceUpdate) {
        if (block.getType() != Material.CHEST || block.getType() != Material.TRAPPED_CHEST || block.getType() != Material.ENDER_CHEST) return;

        BlockState blockState = block.getState();

        switch (block.getType()) {
            case ENDER_CHEST -> {
                EnderChest enderChest = (EnderChest) blockState;
                if (!enderChest.isOpen()) enderChest.open();
                blockState.update(forceUpdate);
            }

            case CHEST, TRAPPED_CHEST -> {
                Chest chest = (Chest) blockState;
                if (!chest.isOpen()) chest.open();
                blockState.update(forceUpdate);
            }
        }
    }

    @Override
    public void closeChest(Block block, boolean forceUpdate) {
        if (block.getType() != Material.CHEST || block.getType() != Material.TRAPPED_CHEST || block.getType() != Material.ENDER_CHEST) return;

        BlockState blockState = block.getState();

        switch (block.getType()) {
            case ENDER_CHEST -> {
                EnderChest enderChest = (EnderChest) blockState;
                if (enderChest.isOpen()) enderChest.close();
                blockState.update(forceUpdate);
            }

            case CHEST, TRAPPED_CHEST -> {
                Chest chest = (Chest) blockState;
                if (chest.isOpen()) chest.close();
                blockState.update(forceUpdate);
            }
        }
    }

    @Override
    public void rotateChest(Block block, int direction) {

        BlockFace blockFace = switch (direction) {
            case 0 -> // South
                    BlockFace.SOUTH;
            case 1 -> // East
                    BlockFace.EAST;
            case 2 -> // West
                    BlockFace.WEST;
            case 3 -> // North
                    BlockFace.NORTH;
            default -> BlockFace.DOWN;
        };

        Directional blockData = (Directional) block.getBlockData();

        blockData.setFacing(blockFace);

        block.setBlockData(blockData);

        block.getState().update(true);
    }
}