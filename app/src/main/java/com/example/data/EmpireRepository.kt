package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

data class KingdomUpgradeResult(
    val success: Boolean,
    val message: String
)

data class KingdomRecruitResult(
    val success: Boolean,
    val message: String
)

data class KingdomBattleResult(
    val success: Boolean,
    val log: List<String>,
    val survivorsSoldiers: Int,
    val survivorsArchers: Int,
    val survivorsMages: Int,
    val goldReward: Long,
    val woodReward: Long,
    val essenceReward: Int,
    val xpReward: Int
)

class EmpireRepository(private val dao: EmpireDao) {

    val userAccount: Flow<UserAccount?> = dao.getUserAccount()
    val weapons: Flow<List<WeaponItem>> = dao.getAllWeapons()
    val inventory: Flow<List<InventoryItem>> = dao.getInventory()
    val friends: Flow<List<FriendUser>> = dao.getFriends()

    fun getChatMessages(channel: String): Flow<List<ChatMessage>> = dao.getChatMessages(channel)

    suspend fun initializeSeedDataIfNeeded() = withContext(Dispatchers.IO) {
        // Seed user account
        dao.saveUserAccount(
            UserAccount(
                id = "local_player",
                username = "KING_PLAYER",
                level = 99,
                title = "ELITE REY DE GUERRA",
                goldCoins = 1250000L,
                coronasDiamonds = 500L,
                mysticalEssence = 450,
                clanName = "IMPERIO IMPERIAL",
                castleLevel = 1,
                goldMineLevel = 1,
                barracksLevel = 1,
                woodCount = 2000L,
                soldiersCount = 5,
                archersCount = 2,
                magesCount = 0,
                kingdomLevel = 1,
                kingdomExp = 0
            )
        )

        // Seed Weapons
        val initialWeapons = listOf(
            WeaponItem(
                id = "wpn_corona_king",
                name = "Corona del Rey",
                category = "Rifle de Asalto",
                isMysticalPremium = true,
                level = 10,
                damage = 92,
                fireRate = 85,
                range = 80,
                accuracy = 88,
                costDiamonds = 120,
                isEquipped = true,
                auraEffectUnlocked = true,
                floatRunesUnlocked = true,
                goldenMasteryUnlocked = false,
                description = "Rifle Místico Forjado con Fuego Azul Imperial y Runas de Mando."
            ),
            WeaponItem(
                id = "wpn_juicio_dorado",
                name = "Juicio Dorado",
                category = "Rifle de Asalto",
                isMysticalPremium = false,
                level = 5,
                damage = 78,
                fireRate = 80,
                range = 75,
                accuracy = 82,
                costGold = 2500,
                isEquipped = false,
                auraEffectUnlocked = true,
                description = "Rifle estándar de los Guardianes Imperiales."
            ),
            WeaponItem(
                id = "wpn_escopeta_abismo",
                name = "Escopeta del Abismo",
                category = "Escopeta",
                isMysticalPremium = true,
                level = 12,
                damage = 98,
                fireRate = 45,
                range = 35,
                accuracy = 60,
                costDiamonds = 150,
                isEquipped = false,
                auraEffectUnlocked = true,
                floatRunesUnlocked = true,
                description = "Devastación de sombra a corta distancia con perdigones místico-púrpura."
            ),
            WeaponItem(
                id = "wpn_tormenta_cristal",
                name = "Tormenta de Cristal",
                category = "Subfusil",
                isMysticalPremium = true,
                level = 8,
                damage = 72,
                fireRate = 98,
                range = 50,
                accuracy = 75,
                costDiamonds = 90,
                isEquipped = false,
                auraEffectUnlocked = true,
                description = "Subfusil de ráfaga ultra-rápida alimentado por cristales de maná."
            ),
            WeaponItem(
                id = "wpn_ojo_leon",
                name = "Ojo de León",
                category = "Rifle de Precisión",
                isMysticalPremium = true,
                level = 15,
                damage = 100,
                fireRate = 30,
                range = 100,
                accuracy = 99,
                costDiamonds = 250,
                isEquipped = false,
                auraEffectUnlocked = true,
                floatRunesUnlocked = true,
                goldenMasteryUnlocked = true,
                description = "Francotirador Mítico Definitivo. Permite eliminaciones doradas instantáneas."
            ),
            WeaponItem(
                id = "wpn_baston_flama_azul",
                name = "Bastón Flama Azul",
                category = "Mágica Pura",
                isMysticalPremium = true,
                level = 6,
                damage = 88,
                fireRate = 65,
                range = 70,
                accuracy = 90,
                costDiamonds = 110,
                isEquipped = false,
                auraEffectUnlocked = true,
                description = "Canaliza rayos de fuego azur para atravesar escudos de energía."
            ),
            WeaponItem(
                id = "wpn_espada_mística",
                name = "Mandoble Imperial de León",
                category = "Cuerpo a Cuerpo",
                isMysticalPremium = false,
                level = 1,
                damage = 85,
                fireRate = 50,
                range = 15,
                accuracy = 95,
                costGold = 1000,
                isEquipped = false,
                description = "Espada ceremonial dorada de los Guerreros del Trono."
            )
        )
        dao.insertWeapons(initialWeapons)

        // Seed Inventory
        val initialItems = listOf(
            InventoryItem("item_1", "Esencia Mística", "Fragmento", 450, "Legendaria", "Utilizada para subir de nivel las armas místicas.", "ic_essence"),
            InventoryItem("item_2", "Poción de Vida Imperial", "Poción", 25, "Rara", "Restaura 100 de salud en batalla.", "ic_potion"),
            InventoryItem("item_3", "Muro de Cristal (Pared Gloo)", "Muro", 15, "Épica", "Crea una barrera protectora mágica de cristal.", "ic_wall"),
            InventoryItem("item_4", "Cofre de Obsidiana", "Cofre", 5, "Mítica", "Contiene skins exclusivas y diamantes.", "ic_chest"),
            InventoryItem("item_5", "Corona de la Eternidad", "Vestuario", 1, "Mítica", "Casco imperial dorado con brillo de runas.", "ic_crown")
        )
        dao.insertInventoryItems(initialItems)

        // Seed Friends
        val initialFriends = listOf(
            FriendUser("f1", "Rey_Guerrero_01", 88, true, "En Lobby", "Rango Diamante V"),
            FriendUser("f2", "Sombra_Imperial", 92, true, "En Batalla", "Rango Maestro I"),
            FriendUser("f3", "Drako_Fuego_Azul", 75, false, "Desconectado", "Rango Oro III"),
            FriendUser("f4", "Valquiria_K", 100, true, "En Lobby", "Rango Gran Rey")
        )
        dao.insertFriends(initialFriends)

        // Seed Chat
        dao.insertChatMessage(ChatMessage(senderName = "Sombra_Imperial", message = "¡Listos para la Batalla de los Tronos!", channel = "GLOBAL"))
        dao.insertChatMessage(ChatMessage(senderName = "Valquiria_K", message = "Únanse a mi squad para Dúos.", channel = "GLOBAL"))
        dao.insertChatMessage(ChatMessage(senderName = "Rey_Guerrero_01", message = "Tengo gemas para regalar en la Discoteca Imperial.", channel = "CLAN"))
    }

    suspend fun saveUser(user: UserAccount) = dao.saveUserAccount(user)

    suspend fun equipWeapon(weaponId: String) = dao.equipWeapon(weaponId)

    suspend fun upgradeWeapon(weapon: WeaponItem, user: UserAccount): Boolean {
        if (weapon.level >= 15) return false
        val diamondCost = if (weapon.isMysticalPremium) 20L * weapon.level else 0L
        val goldCost = if (!weapon.isMysticalPremium) 500L * weapon.level else 0L
        val essenceCost = 15 * weapon.level

        if (user.coronasDiamonds < diamondCost || user.goldCoins < goldCost || user.mysticalEssence < essenceCost) {
            return false
        }

        val updatedUser = user.copy(
            coronasDiamonds = user.coronasDiamonds - diamondCost,
            goldCoins = user.goldCoins - goldCost,
            mysticalEssence = user.mysticalEssence - essenceCost
        )
        dao.saveUserAccount(updatedUser)

        val newLevel = weapon.level + 1
        val updatedWeapon = weapon.copy(
            level = newLevel,
            damage = weapon.damage + 2,
            accuracy = (weapon.accuracy + 1).coerceAtMost(100),
            auraEffectUnlocked = weapon.auraEffectUnlocked || newLevel >= 5,
            floatRunesUnlocked = weapon.floatRunesUnlocked || newLevel >= 10,
            goldenMasteryUnlocked = weapon.goldenMasteryUnlocked || newLevel >= 15
        )
        dao.updateWeapon(updatedWeapon)
        return true
    }

    suspend fun buyDiamonds(amount: Long, coinsBonus: Long = 0) {
        // Helper to update current player user record
    }

    suspend fun sendChatMessage(sender: String, message: String, channel: String) {
        dao.insertChatMessage(ChatMessage(senderName = sender, message = message, channel = channel))
    }

    // --- KINGDOM EXTRA MECHANICS ---

    suspend fun collectResources(user: UserAccount): UserAccount {
        val goldEarned = user.goldMineLevel * 300L
        val woodEarned = user.castleLevel * 150L
        val updated = user.copy(
            goldCoins = user.goldCoins + goldEarned,
            woodCount = user.woodCount + woodEarned
        )
        dao.saveUserAccount(updated)
        return updated
    }

    suspend fun upgradeBuilding(buildingType: String, user: UserAccount): KingdomUpgradeResult {
        var goldCost = 0L
        var woodCost = 0L
        var currentLevel = 1
        var buildingName = ""

        when (buildingType) {
            "CASTILLO" -> {
                currentLevel = user.castleLevel
                goldCost = currentLevel * 10000L
                woodCost = currentLevel * 5000L
                buildingName = "Castillo Real"
            }
            "MINA" -> {
                currentLevel = user.goldMineLevel
                goldCost = currentLevel * 4000L
                woodCost = currentLevel * 2000L
                buildingName = "Mina de Oro"
            }
            "CUARTEL" -> {
                currentLevel = user.barracksLevel
                goldCost = currentLevel * 6000L
                woodCost = currentLevel * 3000L
                buildingName = "Cuartel Militar"
            }
            else -> return KingdomUpgradeResult(false, "Edificio desconocido")
        }

        if (user.goldCoins < goldCost || user.woodCount < woodCost) {
            return KingdomUpgradeResult(false, "No tienes suficientes recursos para mejorar $buildingName (Nivel $currentLevel). Necesitas 💰 $goldCost Oro y 🪵 $woodCost Madera.")
        }

        val xpGained = when (buildingType) {
            "CASTILLO" -> 50
            "MINA" -> 20
            "CUARTEL" -> 30
            else -> 10
        }

        var newXp = user.kingdomExp + xpGained
        var newKingdomLevel = user.kingdomLevel
        var requiredXp = newKingdomLevel * 100

        while (newXp >= requiredXp) {
            newXp -= requiredXp
            newKingdomLevel += 1
            requiredXp = newKingdomLevel * 100
        }

        val updatedUser = when (buildingType) {
            "CASTILLO" -> user.copy(
                goldCoins = user.goldCoins - goldCost,
                woodCount = user.woodCount - woodCost,
                castleLevel = currentLevel + 1,
                kingdomLevel = newKingdomLevel,
                kingdomExp = newXp
            )
            "MINA" -> user.copy(
                goldCoins = user.goldCoins - goldCost,
                woodCount = user.woodCount - woodCost,
                goldMineLevel = currentLevel + 1,
                kingdomLevel = newKingdomLevel,
                kingdomExp = newXp
            )
            "CUARTEL" -> user.copy(
                goldCoins = user.goldCoins - goldCost,
                woodCount = user.woodCount - woodCost,
                barracksLevel = currentLevel + 1,
                kingdomLevel = newKingdomLevel,
                kingdomExp = newXp
            )
            else -> user
        }

        dao.saveUserAccount(updatedUser)
        return KingdomUpgradeResult(true, "¡Has mejorado $buildingName al Nivel ${currentLevel + 1}! XP del Reino +$xpGained.")
    }

    suspend fun trainTroop(troopType: String, user: UserAccount): KingdomRecruitResult {
        var goldCost = 0L
        var woodCost = 0L
        var essenceCost = 0
        var reqBarracks = 1
        var troopName = ""

        when (troopType) {
            "SOLDIER" -> {
                goldCost = 1000L
                woodCost = 500L
                reqBarracks = 1
                troopName = "Soldado de Infantería"
            }
            "ARCHER" -> {
                goldCost = 2000L
                woodCost = 1000L
                reqBarracks = 2
                troopName = "Arquero Real"
            }
            "MAGE" -> {
                goldCost = 5000L
                woodCost = 2000L
                essenceCost = 10
                reqBarracks = 3
                troopName = "Mago Místico"
            }
            else -> return KingdomRecruitResult(false, "Unidad militar desconocida")
        }

        if (user.barracksLevel < reqBarracks) {
            return KingdomRecruitResult(false, "El Cuartel necesita ser Nivel $reqBarracks para desbloquear $troopName.")
        }

        if (user.goldCoins < goldCost || user.woodCount < woodCost || user.mysticalEssence < essenceCost) {
            return KingdomRecruitResult(false, "Recursos insuficientes para entrenar $troopName. Requiere 💰 $goldCost, 🪵 $woodCost, 🔮 $essenceCost.")
        }

        val updatedUser = when (troopType) {
            "SOLDIER" -> user.copy(
                goldCoins = user.goldCoins - goldCost,
                woodCount = user.woodCount - woodCost,
                soldiersCount = user.soldiersCount + 1,
                kingdomExp = user.kingdomExp + 5
            )
            "ARCHER" -> user.copy(
                goldCoins = user.goldCoins - goldCost,
                woodCount = user.woodCount - woodCost,
                archersCount = user.archersCount + 1,
                kingdomExp = user.kingdomExp + 10
            )
            "MAGE" -> user.copy(
                goldCoins = user.goldCoins - goldCost,
                woodCount = user.woodCount - woodCost,
                mysticalEssence = user.mysticalEssence - essenceCost,
                magesCount = user.magesCount + 1,
                kingdomExp = user.kingdomExp + 20
            )
            else -> user
        }

        // Handle level up after training XP
        var finalXp = updatedUser.kingdomExp
        var finalLvl = updatedUser.kingdomLevel
        var requiredXp = finalLvl * 100
        while (finalXp >= requiredXp) {
            finalXp -= requiredXp
            finalLvl += 1
            requiredXp = finalLvl * 100
        }
        val finalUser = updatedUser.copy(kingdomLevel = finalLvl, kingdomExp = finalXp)

        dao.saveUserAccount(finalUser)
        return KingdomRecruitResult(true, "¡Has entrenado 1 $troopName con éxito!")
    }

    suspend fun simulateBattle(
        deployedSoldiers: Int,
        deployedArchers: Int,
        deployedMages: Int,
        difficulty: String,
        user: UserAccount
    ): KingdomBattleResult {
        // Validation: Player must have these troops
        if (deployedSoldiers > user.soldiersCount || deployedArchers > user.archersCount || deployedMages > user.magesCount) {
            return KingdomBattleResult(false, listOf("Error: No tienes suficientes tropas en tu reserva militar."), deployedSoldiers, deployedArchers, deployedMages, 0L, 0L, 0, 0)
        }
        if (deployedSoldiers + deployedArchers + deployedMages <= 0) {
            return KingdomBattleResult(false, listOf("Error: Debes desplegar al menos 1 soldado para ir a la batalla."), 0, 0, 0, 0L, 0L, 0, 0)
        }

        val log = mutableListOf<String>()
        log.add("⚔️ ¡Tu ejército marcha hacia el combate!")

        // Calculate power
        val basePlayerPower = (deployedSoldiers * 12) + (deployedArchers * 25) + (deployedMages * 60)
        // Add random variance (+-15%)
        val playerVariance = (0.85f + Math.random() * 0.30f).toFloat()
        val finalPlayerPower = (basePlayerPower * playerVariance).toInt()

        val enemyBasePower = when (difficulty) {
            "EASY" -> 40
            "MEDIUM" -> 140
            "HARD" -> 350
            else -> 50
        }
        val enemyName = when (difficulty) {
            "EASY" -> "Campamento Orco"
            "MEDIUM" -> "Guarida del Dragón Rúnico"
            "HARD" -> "Ciudadela del Trono Rival"
            else -> "Invasores Rebeldes"
        }

        val enemyVariance = (0.90f + Math.random() * 0.20f).toFloat()
        val finalEnemyPower = (enemyBasePower * enemyVariance).toInt()

        log.add("👑 Poder Imperial Depliega: $finalPlayerPower | 🛡️ Enemigo ($enemyName) Poder: $finalEnemyPower")

        val victory = finalPlayerPower >= finalEnemyPower

        var sSurvivors = deployedSoldiers
        var aSurvivors = deployedArchers
        var mSurvivors = deployedMages

        var gReward = 0L
        var wReward = 0L
        var eReward = 0
        var xpGained = 0

        if (victory) {
            log.add("🎉 ¡VICTORIA SUPREMA! El Reino de Aetherfall prevalece ante el enemigo.")

            // Calculate troop loss ratio
            val lossRatio = (finalEnemyPower.toFloat() / finalPlayerPower.toFloat() * 0.5f).coerceIn(0.05f, 0.75f)

            // Calculate losses
            val sLoss = (deployedSoldiers * lossRatio).toInt().coerceAtMost(deployedSoldiers)
            val aLoss = (deployedArchers * lossRatio).toInt().coerceAtMost(deployedArchers)
            val mLoss = (deployedMages * lossRatio).toInt().coerceAtMost(deployedMages)

            sSurvivors -= sLoss
            aSurvivors -= aLoss
            mSurvivors -= mLoss

            if (sLoss > 0) log.add("💀 Bajas Soldados: -$sLoss")
            if (aLoss > 0) log.add("💀 Bajas Arqueros: -$aLoss")
            if (mLoss > 0) log.add("💀 Bajas Magos: -$mLoss")

            // Scale rewards
            when (difficulty) {
                "EASY" -> {
                    gReward = 4000L
                    wReward = 1500L
                    eReward = 3
                    xpGained = 30
                }
                "MEDIUM" -> {
                    gReward = 12000L
                    wReward = 5000L
                    eReward = 10
                    xpGained = 75
                }
                "HARD" -> {
                    gReward = 35000L
                    wReward = 15000L
                    eReward = 25
                    xpGained = 180
                }
            }

            log.add("💰 Botín Ganado: +$gReward Oro, 🪵 +$wReward Madera, 🔮 +$eReward Esencia Mística")
            log.add("📈 Experiencia del Reino +$xpGained XP.")
        } else {
            log.add("😭 ¡DERROTA CATASTRÓFICA! Tu ejército ha sido flanqueado y derrotado.")

            // Heavy losses (75% to 100%)
            val lossRatio = (0.75f + Math.random() * 0.25f).toFloat()
            val sLoss = (deployedSoldiers * lossRatio).toInt().coerceAtMost(deployedSoldiers)
            val aLoss = (deployedArchers * lossRatio).toInt().coerceAtMost(deployedArchers)
            val mLoss = (deployedMages * lossRatio).toInt().coerceAtMost(deployedMages)

            sSurvivors -= sLoss
            aSurvivors -= aLoss
            mSurvivors -= mLoss

            log.add("💀 Bajas Totales: Soldados -$sLoss, Arqueros -$aLoss, Magos -$mLoss")

            xpGained = 15
            log.add("📈 XP ganada por esfuerzo de combate: +$xpGained XP.")
        }

        // Apply changes to database state
        val finalSoldiers = user.soldiersCount - (deployedSoldiers - sSurvivors)
        val finalArchers = user.archersCount - (deployedArchers - aSurvivors)
        val finalMages = user.magesCount - (deployedMages - mSurvivors)

        var newXp = user.kingdomExp + xpGained
        var newKingdomLevel = user.kingdomLevel
        var requiredXp = newKingdomLevel * 100

        while (newXp >= requiredXp) {
            newXp -= requiredXp
            newKingdomLevel += 1
            requiredXp = newKingdomLevel * 100
        }

        val updatedUser = user.copy(
            goldCoins = user.goldCoins + gReward,
            woodCount = user.woodCount + wReward,
            mysticalEssence = user.mysticalEssence + eReward,
            soldiersCount = finalSoldiers,
            archersCount = finalArchers,
            magesCount = finalMages,
            kingdomLevel = newKingdomLevel,
            kingdomExp = newXp
        )

        dao.saveUserAccount(updatedUser)

        return KingdomBattleResult(
            success = victory,
            log = log,
            survivorsSoldiers = sSurvivors,
            survivorsArchers = aSurvivors,
            survivorsMages = mSurvivors,
            goldReward = gReward,
            woodReward = wReward,
            essenceReward = eReward,
            xpReward = xpGained
        )
    }
}
