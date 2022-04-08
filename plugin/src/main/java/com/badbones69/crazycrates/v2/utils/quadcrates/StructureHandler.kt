package com.badbones69.crazycrates.v2.utils.quadcrates

import com.badbones69.crazycrates.getPlugin
import com.badbones69.crazycrates.v2.utils.interfaces.Structures
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.structure.Mirror
import org.bukkit.block.structure.StructureRotation
import java.io.File
import java.util.*

class StructureHandler(val file: File) : Structures {

    private val structureManager = getPlugin().server.structureManager.loadStructure(file)

    private val structureBlocks = arrayListOf<Block>()

    private val preStructureBlocks = arrayListOf<Block>()

    override fun pasteStructure(location: Location) {
        runCatching {
            // Save the old blocks
            getNearbyBlocks(location)

            structureManager.place(location, false, StructureRotation.NONE, Mirror.NONE, 0, 1F, Random())
        }.onFailure { getPlugin().logger.warning(it.message) }.onSuccess {
            getPlugin().logger.info("Saving structure blocks to list.")

            // Save the structure blocks
            getStructureBlocks(location)
        }
    }

    // Don't need yet
    override fun saveStructure(location: ArrayList<Location>) {
        runCatching {

        }.onFailure { getPlugin().logger.warning(it.message) }
    }

    override fun removeStructure(location: Location) {

    }

    override fun getStructureBlocks(location: Location): ArrayList<Block> {
        for (x in 0 until structureManager.size.x.toInt()) {
            for (y in 0 until structureManager.size.y.toInt()) {
                for (z in 0 until structureManager.size.z.toInt()) {
                    structureBlocks.add(location.block.getRelative(x, y, z))
                }
            }
        }
        return structureBlocks
    }

    override fun getNearbyBlocks(location: Location): ArrayList<Block> {
        for (x in 0 until structureManager.size.x.toInt()) {
            for (y in 0 until structureManager.size.y.toInt()) {
                for (z in 0 until structureManager.size.z.toInt()) {
                    preStructureBlocks.add(location.block.getRelative(x, y, z))
                }
            }
        }
        return preStructureBlocks
    }

    override fun getBlackList(): List<Material> {
        return arrayListOf(
            Material.ACACIA_BUTTON,
            Material.BIRCH_BUTTON,
            Material.DARK_OAK_BUTTON,
            Material.JUNGLE_BUTTON,
            Material.OAK_BUTTON,
            Material.SPRUCE_BUTTON,
            Material.STONE_BUTTON,
            // Add all signs
            Material.ACACIA_SIGN,
            Material.BIRCH_SIGN,
            Material.DARK_OAK_SIGN,
            Material.JUNGLE_SIGN,
            Material.OAK_SIGN,
            Material.SPRUCE_SIGN,
            Material.ACACIA_WALL_SIGN,
            Material.BIRCH_WALL_SIGN,
            Material.DARK_OAK_WALL_SIGN,
            Material.JUNGLE_WALL_SIGN,
            Material.OAK_WALL_SIGN,
            Material.SPRUCE_WALL_SIGN
        )
    }
}