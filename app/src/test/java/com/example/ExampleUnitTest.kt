package com.example

import com.example.data.UserAccount
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testDefaultUserAccountKingdomState() {
        val user = UserAccount()
        assertEquals(1, user.castleLevel)
        assertEquals(1, user.goldMineLevel)
        assertEquals(1, user.barracksLevel)
        assertEquals(1000L, user.woodCount)
        assertEquals(5, user.soldiersCount)
        assertEquals(2, user.archersCount)
        assertEquals(0, user.magesCount)
        assertEquals(1, user.kingdomLevel)
        assertEquals(0, user.kingdomExp)
    }

    @Test
    fun testResourceCollectionMath() {
        val user = UserAccount(goldCoins = 1000L, woodCount = 500L, goldMineLevel = 3, castleLevel = 2)
        val goldEarned = user.goldMineLevel * 300L
        val woodEarned = user.castleLevel * 150L

        val updatedUser = user.copy(
            goldCoins = user.goldCoins + goldEarned,
            woodCount = user.woodCount + woodEarned
        )

        assertEquals(1000L + (3 * 300L), updatedUser.goldCoins)
        assertEquals(500L + (2 * 150L), updatedUser.woodCount)
    }

    @Test
    fun testBuildingUpgradeCostCalculation() {
        val user = UserAccount(goldCoins = 50000L, woodCount = 30000L)

        // Upgrade Castle Cost
        val castleCurrentLevel = user.castleLevel
        val castleGoldCost = castleCurrentLevel * 10000L
        val castleWoodCost = castleCurrentLevel * 5000L

        assertTrue(user.goldCoins >= castleGoldCost)
        assertTrue(user.woodCount >= castleWoodCost)

        // Upgrade Gold Mine Cost
        val mineCurrentLevel = user.goldMineLevel
        val mineGoldCost = mineCurrentLevel * 4000L
        val mineWoodCost = mineCurrentLevel * 2000L

        assertTrue(user.goldCoins >= mineGoldCost)
        assertTrue(user.woodCount >= mineWoodCost)

        // Upgrade Barracks Cost
        val barracksCurrentLevel = user.barracksLevel
        val barracksGoldCost = barracksCurrentLevel * 6000L
        val barracksWoodCost = barracksCurrentLevel * 3000L

        assertTrue(user.goldCoins >= barracksGoldCost)
        assertTrue(user.woodCount >= barracksWoodCost)
    }

    @Test
    fun testBuildingUpgradeResourceDeductionAndKingdomXpProgress() {
        val user = UserAccount(goldCoins = 20000L, woodCount = 10000L, castleLevel = 1, kingdomLevel = 1, kingdomExp = 80)

        // Upgrade Castle (costs 10000 gold, 5000 wood, yields 50 XP)
        val goldCost = user.castleLevel * 10000L
        val woodCost = user.castleLevel * 5000L
        val xpGained = 50

        var newXp = user.kingdomExp + xpGained
        var newKingdomLevel = user.kingdomLevel
        var requiredXp = newKingdomLevel * 100

        while (newXp >= requiredXp) {
            newXp -= requiredXp
            newKingdomLevel += 1
            requiredXp = newKingdomLevel * 100
        }

        val updatedUser = user.copy(
            goldCoins = user.goldCoins - goldCost,
            woodCount = user.woodCount - woodCost,
            castleLevel = user.castleLevel + 1,
            kingdomLevel = newKingdomLevel,
            kingdomExp = newXp
        )

        assertEquals(10000L, updatedUser.goldCoins)
        assertEquals(5000L, updatedUser.woodCount)
        assertEquals(2, updatedUser.castleLevel)
        assertEquals(2, updatedUser.kingdomLevel) // 80 + 50 = 130 XP. Levels up to 2 (130 - 100 = 30 remainder XP)
        assertEquals(30, updatedUser.kingdomExp)
    }

    @Test
    fun testTroopTrainingRequirementsAndCosts() {
        val user = UserAccount(goldCoins = 10000L, woodCount = 5000L, barracksLevel = 2, soldiersCount = 0, archersCount = 0)

        // Soldier Cost: 1000 Gold, 500 Wood. Requires Barracks Level >= 1
        val soldierGold = 1000L
        val soldierWood = 500L
        assertTrue(user.barracksLevel >= 1)
        assertTrue(user.goldCoins >= soldierGold && user.woodCount >= soldierWood)

        // Archer Cost: 2000 Gold, 1000 Wood. Requires Barracks Level >= 2
        val archerGold = 2000L
        val archerWood = 1000L
        assertTrue(user.barracksLevel >= 2)
        assertTrue(user.goldCoins >= archerGold && user.woodCount >= archerWood)

        // Mage Cost: 5000 Gold, 2000 Wood, 10 Essence. Requires Barracks Level >= 3
        val reqBarracksForMage = 3
        assertFalse(user.barracksLevel >= reqBarracksForMage) // Barracks level is only 2
    }

    @Test
    fun testCombatPowerMathAndConquestOutcome() {
        // Deployed Force: 5 soldiers, 2 archers, 1 mage
        val deployedSoldiers = 5
        val deployedArchers = 2
        val deployedMages = 1

        val basePlayerPower = (deployedSoldiers * 12) + (deployedArchers * 25) + (deployedMages * 60)
        assertEquals(5 * 12 + 2 * 25 + 1 * 60, basePlayerPower) // 60 + 50 + 60 = 170

        // EASY difficulty: Enemy base power is 40. Player with 170 power will easily win
        val enemyBasePower = 40
        assertTrue(basePlayerPower > enemyBasePower)

        // Calculate survival losses for victory
        // Loss ratio = (Enemy / Player * 0.5)
        val lossRatio = (enemyBasePower.toFloat() / basePlayerPower.toFloat() * 0.5f).coerceIn(0.05f, 0.75f)
        val soldiersLost = (deployedSoldiers * lossRatio).toInt()
        val archersLost = (deployedArchers * lossRatio).toInt()
        val magesLost = (deployedMages * lossRatio).toInt()

        val survivingSoldiers = deployedSoldiers - soldiersLost
        val survivingArchers = deployedArchers - archersLost
        val survivingMages = deployedMages - magesLost

        assertTrue(survivingSoldiers >= 0)
        assertTrue(survivingArchers >= 0)
        assertTrue(survivingMages >= 0)
    }
}
