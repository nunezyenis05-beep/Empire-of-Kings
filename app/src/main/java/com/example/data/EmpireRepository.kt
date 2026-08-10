package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class EmpireRepository(private val dao: EmpireDao) {

    val userAccount: Flow<UserAccount?> = dao.getUserAccount()
    val weapons: Flow<List<WeaponItem>> = dao.getAllWeapons()
    val inventory: Flow<List<InventoryItem>> = dao.getInventory()
    val friends: Flow<List<FriendUser>> = dao.getFriends()
    val kingdomState: Flow<KingdomStateEntity?> = dao.getKingdomState()

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
                clanName = "IMPERIO IMPERIAL"
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

    suspend fun buyDiamonds(amount: Long, coinsBonus: Long = 0) = withContext(Dispatchers.IO) {
        val user = dao.getUserAccount()
        // Helper to update current player user record
    }

    suspend fun sendChatMessage(sender: String, message: String, channel: String) {
        dao.insertChatMessage(ChatMessage(senderName = sender, message = message, channel = channel))
    }

    suspend fun saveKingdomState(state: KingdomGameState) = withContext(Dispatchers.IO) {
        dao.saveKingdomState(KingdomStateEntity.fromGameState(state))
    }
}
