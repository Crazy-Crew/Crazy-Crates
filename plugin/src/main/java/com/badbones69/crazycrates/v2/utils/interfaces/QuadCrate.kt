package com.badbones69.crazycrates.v2.utils.interfaces

import org.bukkit.block.Block

interface QuadCrate {

    // Open a chest by altering the block state
    fun openChest(block: Block, forceUpdate: Boolean)

    // Close a chest by altering the block state
    fun closeChest(block: Block, forceUpdate: Boolean)

    // Get the chest and rotate it by altering block state
    fun rotateChest(chest: Block, direction: Int)

}