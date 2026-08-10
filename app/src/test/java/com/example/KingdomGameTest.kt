package com.example

import com.example.data.*
import org.junit.Assert.*
import org.junit.Test

class KingdomGameTest {

    @Test
    fun testDefaultKingdomStateSeeding() {
        val defaultState = KingdomStateEntity.createDefaultState()

        // 10x10 grid has 100 tiles
        assertEquals(100, defaultState.tiles.size)

        // Count start territory
        val startingTiles = defaultState.tiles.filter { it.isStartingTerritory }
        assertEquals(9, startingTiles.size) // 3x3 region

        // Starting buildings
        assertTrue(defaultState.buildings.any { it.type == BuildingType.CASTLE })
        assertTrue(defaultState.buildings.any { it.type == BuildingType.WOODCUTTER })

        // Initial resources stored should match defaults
        assertEquals(150, defaultState.woodStored)
        assertEquals(1000, defaultState.goldStored)
        assertEquals(80, defaultState.stoneStored)
        assertEquals(20, defaultState.manaStored)
    }

    @Test
    fun testResourceHarvestingMath() {
        val defaultState = KingdomStateEntity.createDefaultState()
        val woodNode = defaultState.resources.first { it.type == ResourceType.WOOD }

        val initialAmount = woodNode.currentAmount
        val harvestAmount = 15.coerceAtMost(woodNode.currentAmount)

        // Act - subtract from resource node and add to storage
        woodNode.currentAmount -= harvestAmount
        val updatedState = defaultState.copy(
            woodStored = defaultState.woodStored + harvestAmount
        )

        // Assert
        assertEquals(initialAmount - 15, woodNode.currentAmount)
        assertEquals(165, updatedState.woodStored)
    }

    @Test
    fun testBuildingUpgradeMathAndValidation() {
        val defaultState = KingdomStateEntity.createDefaultState()
        val initialCastle = defaultState.buildings.first { it.type == BuildingType.CASTLE }

        // We have 150 Wood and 1000 Gold by default. Upgrade costs 50 Wood and 200 Gold.
        assertTrue(defaultState.woodStored >= 50)
        assertTrue(defaultState.goldStored >= 200)

        // Act - Upgrade castle
        val updatedBuildings = defaultState.buildings.map {
            if (it.id == initialCastle.id) it.copy(level = it.level + 1) else it
        }
        val updatedState = defaultState.copy(
            buildings = updatedBuildings,
            woodStored = defaultState.woodStored - 50,
            goldStored = defaultState.goldStored - 200
        )

        // Assert
        val upgradedCastle = updatedState.buildings.first { it.type == BuildingType.CASTLE }
        assertEquals(2, upgradedCastle.level)
        assertEquals(100, updatedState.woodStored)
        assertEquals(800, updatedState.goldStored)
    }

    @Test
    fun testSerializationAndDeserialization() {
        val state = KingdomStateEntity.createDefaultState()

        // Act
        val entity = KingdomStateEntity.fromGameState(state)
        val deserialized = KingdomStateEntity.toGameState(entity)

        // Assert
        assertEquals(state.woodStored, deserialized.woodStored)
        assertEquals(state.goldStored, deserialized.goldStored)
        assertEquals(state.stoneStored, deserialized.stoneStored)
        assertEquals(state.manaStored, deserialized.manaStored)
        assertEquals(state.tiles.size, deserialized.tiles.size)
        assertEquals(state.resources.size, deserialized.resources.size)
        assertEquals(state.buildings.size, deserialized.buildings.size)
    }
}
