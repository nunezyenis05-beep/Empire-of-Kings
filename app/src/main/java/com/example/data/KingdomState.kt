package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

enum class TerrainType {
    GRASS,
    FOREST,
    MOUNTAIN,
    WATER
}

enum class ResourceType {
    WOOD,
    GOLD,
    STONE,
    MANA
}

enum class BuildingType {
    CASTLE,
    BARRACKS,
    GOLD_MINE,
    WOODCUTTER,
    MAGE_TOWER
}

@JsonClass(generateAdapter = true)
data class KingdomTile(
    val x: Int,
    val y: Int,
    val terrain: TerrainType,
    val isStartingTerritory: Boolean = false
)

@JsonClass(generateAdapter = true)
data class ResourceNode(
    val id: String,
    val x: Int,
    val y: Int,
    val type: ResourceType,
    val initialAmount: Int,
    var currentAmount: Int,
    val name: String
)

@JsonClass(generateAdapter = true)
data class KingdomBuilding(
    val id: String,
    val x: Int,
    val y: Int,
    val type: BuildingType,
    val level: Int = 1,
    val name: String
)

@JsonClass(generateAdapter = true)
data class KingdomGameState(
    val tiles: List<KingdomTile> = emptyList(),
    val resources: List<ResourceNode> = emptyList(),
    val buildings: List<KingdomBuilding> = emptyList(),
    val woodStored: Int = 100,
    val goldStored: Int = 500,
    val stoneStored: Int = 50,
    val manaStored: Int = 10,
    val lastSavedTime: Long = System.currentTimeMillis()
)

@Entity(tableName = "kingdom_state")
data class KingdomStateEntity(
    @PrimaryKey val id: String = "local_kingdom",
    val serializedState: String
) {
    companion object {
        private val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        private val adapter = moshi.adapter(KingdomGameState::class.java)

        fun fromGameState(state: KingdomGameState): KingdomStateEntity {
            return KingdomStateEntity(
                serializedState = adapter.toJson(state)
            )
        }

        fun toGameState(entity: KingdomStateEntity?): KingdomGameState {
            if (entity == null) return createDefaultState()
            return try {
                adapter.fromJson(entity.serializedState) ?: createDefaultState()
            } catch (e: Exception) {
                createDefaultState()
            }
        }

        fun createDefaultState(): KingdomGameState {
            val tiles = mutableListOf<KingdomTile>()
            val resourceNodes = mutableListOf<ResourceNode>()
            val buildings = mutableListOf<KingdomBuilding>()

            // Simple 10x10 map layout
            // Grass in the center, forest on the top, river/water on the bottom, mountain on the left.
            for (y in 0 until 10) {
                for (x in 0 until 10) {
                    val terrain = when {
                        y == 0 || y == 1 -> TerrainType.FOREST
                        y == 9 -> TerrainType.WATER
                        x == 0 -> TerrainType.MOUNTAIN
                        else -> TerrainType.GRASS
                    }
                    // Starting player territory: center 3x3 (coordinates 4..6)
                    val isStarting = (x in 3..5 && y in 4..6)
                    tiles.add(KingdomTile(x, y, terrain, isStarting))
                }
            }

            // Seed some harvestable resource nodes
            resourceNodes.add(ResourceNode("res_tree_1", 2, 2, ResourceType.WOOD, 150, 150, "Pino Ancestral"))
            resourceNodes.add(ResourceNode("res_tree_2", 7, 1, ResourceType.WOOD, 100, 100, "Roble de la Sabiduría"))
            resourceNodes.add(ResourceNode("res_gold_1", 1, 4, ResourceType.GOLD, 300, 300, "Veta de Oro Brillante"))
            resourceNodes.add(ResourceNode("res_gold_2", 8, 5, ResourceType.GOLD, 250, 250, "Mina Abandonada"))
            resourceNodes.add(ResourceNode("res_stone_1", 1, 8, ResourceType.STONE, 200, 200, "Cantera Rúnica"))
            resourceNodes.add(ResourceNode("res_mana_1", 5, 2, ResourceType.MANA, 50, 50, "Cristal de Hechizo"))

            // Seed starting building (Castle)
            buildings.add(KingdomBuilding("bld_castle_1", 4, 5, BuildingType.CASTLE, 1, "Castillo del Trono"))
            buildings.add(KingdomBuilding("bld_lumberjack_1", 3, 5, BuildingType.WOODCUTTER, 1, "Cabaña de Leñador"))

            return KingdomGameState(
                tiles = tiles,
                resources = resourceNodes,
                buildings = buildings,
                woodStored = 150,
                goldStored = 1000,
                stoneStored = 80,
                manaStored = 20
            )
        }
    }
}
