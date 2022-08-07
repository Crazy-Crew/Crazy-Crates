package com.badbones69.crazycrates.support.structures

import com.badbones69.crazycrates.CrazyCrates
import com.badbones69.crazycrates.support.structures.interfaces.StructureControl
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.structure.Mirror
import org.bukkit.block.structure.StructureRotation
import java.io.File
import java.util.*

class StructureHandler(private val plugin: CrazyCrates, val file: File) : StructureControl {

    private val structureManager = plugin.server.structureManager.loadStructure(file)

    private val structureBlocks = arrayListOf<Block>()

    private val preStructureBlocks = arrayListOf<Block>()

    override fun pasteStructure(location: Location) {
        runCatching {
            // Save the old blocks
            getNearbyBlocks(location)

            structureManager.place(location, false, StructureRotation.NONE, Mirror.NONE, 0, 1F, Random())
        }.onFailure {
            plugin.logger.warning(it.message)
        }.onSuccess {
            // Save the structure blocks
            getStructureBlocks(location)
        }
    }

    override fun removeStructure(location: Location) {
        structureBlocks.forEach {
            it.location.block.type = Material.AIR
        }
    }

    override fun saveSchematic(locations: Array<out Location>?) {
        runCatching {

        }.onFailure {
            plugin.logger.warning(it.message)
        }
    }

    override fun getStructureX(): Double {
        return structureManager.size.x
    }

    override fun getStructureZ(): Double {
        return structureManager.size.z
    }

    private fun loop(getStructureBlocks: Boolean, location: Location) {
        for (x in 0 until structureManager.size.x.toInt()) {
            for (y in 0 until structureManager.size.y.toInt()) {
                for (z in 0 until structureManager.size.z.toInt()) {
                    if (getStructureBlocks) {
                        structureBlocks.add(location.block.getRelative(x, y, z))

                        structureBlocks.forEach { it.location.block.state.update() }
                        return
                    }

                    preStructureBlocks.add(location.block.getRelative(x, y, z))
                }
            }
        }
    }

    override fun getStructureBlocks(location: Location): ArrayList<Block> {
        loop(true, location)

        return structureBlocks
    }

    override fun getNearbyBlocks(location: Location): ArrayList<Block> {
        loop(false, location)

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