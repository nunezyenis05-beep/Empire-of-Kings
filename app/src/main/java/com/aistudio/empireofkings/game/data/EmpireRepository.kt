package com.aistudio.empireofkings.game.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlin.random.Random
import java.util.UUID
import kotlin.math.round

class EmpireRepository(private val dao: EmpireDao) {

    val userAccount: Flow<UserAccount?> = dao.getUserAccount()
    val weapons: Flow<List<WeaponItem>> = dao.getAllWeapons()
    val inventory: Flow<List<InventoryItem>> = dao.getInventory()
    val wardrobeItems: Flow<List<WardrobeItem>> = dao.getWardrobeItems()
    val friends: Flow<List<FriendUser>> = dao.getFriends()
    val settings: Flow<AppSettings?> = dao.getSettings()
    val discoState: Flow<DiscoState?> = dao.getDiscoState()
    val miniGameProgress: Flow<MiniGameProgress?> = dao.getMiniGameProgress()
    val clanState: Flow<ClanState?> = dao.getClanState()
    val paymentTransactions: Flow<List<PaymentTransaction>> = dao.getPaymentTransactions()
    val catalogAdminStates: Flow<List<CatalogAdminState>> = dao.getCatalogAdminStates()

    fun getChatMessages(channel: String): Flow<List<ChatMessage>> = dao.getChatMessages(channel)

    suspend fun initializeCatalogAdminStates() = withContext(Dispatchers.IO) {
        val known = dao.getCatalogAdminStates().first().map { it.itemId }.toSet()
        EquipmentCatalog.all.map { it.id }.filterNot(known::contains).chunked(InventoryRules.MAX_CATALOG_BATCH).forEach { ids ->
            dao.saveCatalogAdminStates(ids.map { CatalogAdminState(it) })
        }
    }

    suspend fun assignCatalogItems(itemIds: List<String>): String = withContext(Dispatchers.IO) {
        if (!CatalogAdminActions.validateBatch(itemIds)) return@withContext "Lote inválido: máximo ${InventoryRules.MAX_CATALOG_BATCH} elementos únicos."
        val definitions = itemIds.distinct().mapNotNull { id -> EquipmentCatalog.all.firstOrNull { it.id == id } }
        if (definitions.size != itemIds.size || definitions.any { !CatalogAdminActions.canAssign(it) }) return@withContext "Solo elementos asignables pueden entregarse."
        val states = itemIds.map { dao.getCatalogAdminState(it) ?: CatalogAdminState(it) }
        if (states.any { it.availability != CatalogAvailability.ASSIGNABLE.name }) return@withContext "El estado del catálogo no permite esta entrega."
        val existing = dao.getInventoryCount()
        if (existing + itemIds.size > InventoryRules.MAX_PLAYER_SLOTS) return@withContext "Se alcanzó el límite local de ${InventoryRules.MAX_PLAYER_SLOTS} espacios."
        val existingIds = itemIds.mapNotNull { dao.getInventoryItem("inventory_$it")?.id }.toSet()
        if (existingIds.isNotEmpty()) return@withContext "Algunos elementos ya están asignados localmente."
        dao.insertInventoryItems(definitions.map { definition ->
            InventoryItem("inventory_${definition.id}", definition.label, definition.slot, 1, "Común", "Elemento individual del catálogo; asignable por creador/admin.", "catalog_${definition.slot.lowercase()}")
        })
        "Asignados ${itemIds.size} elementos al jugador local."
    }

    suspend fun publishCatalogItems(itemIds: List<String>): String = updateCatalogStates(itemIds, CatalogAvailability.PUBLISHED_DIAMOND_SALE)

    suspend fun retireCatalogItems(itemIds: List<String>): String = updateCatalogStates(itemIds, CatalogAvailability.RETIRED)

    private suspend fun updateCatalogStates(itemIds: List<String>, target: CatalogAvailability): String = withContext(Dispatchers.IO) {
        if (!CatalogAdminActions.validateBatch(itemIds)) return@withContext "Lote inválido: máximo ${InventoryRules.MAX_CATALOG_BATCH} elementos únicos."
        val definitions = itemIds.distinct().mapNotNull { id -> EquipmentCatalog.all.firstOrNull { it.id == id } }
        if (definitions.size != itemIds.size) return@withContext "El catálogo contiene un ID desconocido."
        if (definitions.any { definition ->
                when (target) {
                    CatalogAvailability.PUBLISHED_DIAMOND_SALE -> !CatalogAdminActions.canPublishForDiamondSale(definition)
                    CatalogAvailability.RETIRED -> !CatalogAdminActions.canRetire(definition)
                    CatalogAvailability.ASSIGNABLE -> false
                }
            }) return@withContext "La transición solicitada no es válida para el estado actual."
        val current = itemIds.map { dao.getCatalogAdminState(it) ?: CatalogAdminState(it) }
        if (target == CatalogAvailability.PUBLISHED_DIAMOND_SALE && current.any { it.availability != CatalogAvailability.ASSIGNABLE.name }) return@withContext "Solo elementos asignables pueden publicarse."
        if (target == CatalogAvailability.RETIRED && current.any { it.availability == CatalogAvailability.RETIRED.name }) return@withContext "El elemento ya está retirado."
        dao.saveCatalogAdminStates(current.map { it.copy(availability = target.name, publishedForDiamondSale = target == CatalogAvailability.PUBLISHED_DIAMOND_SALE) })
        "${target.name}: ${itemIds.size} elementos actualizados."
    }


    suspend fun initializeSeedDataIfNeeded() = withContext(Dispatchers.IO) {
        // Remove the five retired inventory seed rows from both new and existing installs.
        // The cleanup is deliberately ID-pattern based so no retired item metadata remains
        // in the app while the remaining inventory stays untouched.
        dao.removeLegacyInventorySeedRows()
        dao.removeRetiredWardrobeRows()

        // Seed account data only once. Cosmetic defaults are backfilled independently
        // so an upgrade from schema 2 also receives the real wardrobe flow.
        val needsAccountSeed = dao.getUserAccountSnapshot() == null
        if (needsAccountSeed) dao.saveUserAccount(
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
        if (needsAccountSeed) {
            dao.saveSettings(AppSettings())
            dao.saveDiscoState(DiscoState(updatedAt = System.currentTimeMillis()))
            dao.saveMiniGameProgress(MiniGameProgress(updatedAt = System.currentTimeMillis()))

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

        // Seed exactly fifteen individual selectable items per player category.
        // The rest of the 200-slot capacity remains blank until an admin assigns an item.
        val seededCatalogItems = EquipmentCatalog.all
            .filter { it.slot in InventoryRules.selectableCategories }
            .groupBy { it.slot }
            .values.flatMap { it.take(InventoryRules.INITIAL_ITEMS_PER_CATEGORY) }
            .map { definition ->
                InventoryItem(
                    id = "inventory_${definition.id}",
                    name = definition.label,
                    type = definition.slot,
                    quantity = 1,
                    rarity = "Común",
                    description = "Elemento individual del catálogo; asignable por creador/admin.",
                    iconName = "catalog_${definition.slot.lowercase()}"
                )
            }
        dao.insertInventoryItems(seededCatalogItems.take(InventoryRules.MAX_PLAYER_SLOTS))

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

        // Upgrade path: older local databases receive the same bounded starter set,
        // while never exceeding the 200 total-slot contract.
        if (dao.getInventoryCount() < InventoryRules.INITIAL_ITEMS_PER_CATEGORY * InventoryRules.selectableCategories.size) {
            val upgradeItems = EquipmentCatalog.all
                .filter { it.slot in InventoryRules.selectableCategories }
                .groupBy { it.slot }
                .values.flatMap { it.take(InventoryRules.INITIAL_ITEMS_PER_CATEGORY) }
                .map { definition ->
                    InventoryItem("inventory_${definition.id}", definition.label, definition.slot, 1, "Común", "Elemento individual del catálogo; asignable por creador/admin.", "catalog_${definition.slot.lowercase()}")
                }
            dao.insertInventoryItems(upgradeItems.take(InventoryRules.MAX_PLAYER_SLOTS))
        }

        if (dao.getWardrobeCount() == 0) {
            dao.insertWardrobeItems(initialWardrobeItems())
        }
        // Temporary real-GLB integration test: keep it visible on existing installs too.
        if (dao.getWardrobeItem("fox_ears_test") == null) {
            dao.insertWardrobeItems(listOf(
                WardrobeItem(
                    id = "fox_ears_test",
                    name = "Orejas de Zorro — PRUEBA 3D",
                    slot = "Orejas",
                    rarity = "Prueba",
                    description = "Accesorio GLB real combinado con Maya para comprobar escala, posición y carga dentro del juego.",
                    iconName = "catalog_orejas",
                    avatarPreset = "maya_fox_ears_test"
                )
            ))
        }
        // These rows are independently backfilled for schema-3 upgrades and test fixtures.
        if (dao.getDiscoStateSnapshot() == null) {
            dao.saveDiscoState(DiscoState(updatedAt = System.currentTimeMillis()))
        }
        if (dao.getMiniGameProgressSnapshot() == null) {
            dao.saveMiniGameProgress(MiniGameProgress(updatedAt = System.currentTimeMillis()))
        }
        if (dao.getClanStateSnapshot() == null) {
            dao.saveClanState(ClanState(updatedAt = System.currentTimeMillis()))
        }
    }

    private fun initialWardrobeItems(): List<WardrobeItem> = listOf(
        WardrobeItem("outfit_royal", "Atuendo del Trono", "Atuendo", "Mítica", "Armadura ceremonial azul y oro del soberano.", "ic_royal_outfit", "king_warrior", isEquipped = true),
        WardrobeItem("outfit_night", "Guardia Nocturna", "Atuendo", "Épica", "Tela de obsidiana para incursiones nocturnas.", "ic_night_outfit", "king_warrior"),
        WardrobeItem("armor_azur", "Coraza de Fuego Azur", "Armadura", "Legendaria", "Placas reforzadas con runas de energía.", "ic_azur_armor", "king_warrior", isEquipped = true),
        WardrobeItem("cape_crown", "Capa de la Corona", "Capa", "Mítica", "Capa real con ribetes de luz dorada.", "ic_crown_cape", "king_warrior", isEquipped = true),
        WardrobeItem("cape_shadow", "Capa de Sombra", "Capa", "Rara", "Una silueta ligera para el sigilo.", "ic_shadow_cape", "king_warrior"),
        WardrobeItem("crown_iron", "Diadema de Hierro", "Corona", "Rara", "Distintivo de los capitanes del muro.", "ic_iron_crown", "king_warrior"),
        WardrobeItem("amulet_sun", "Amuleto Solar", "Accesorio", "Legendaria", "Cristal solar que acompaña al avatar.", "ic_sun_amulet", "king_warrior", isEquipped = true)
    )

    suspend fun saveUser(user: UserAccount) = dao.saveUserAccount(user)

    suspend fun saveSettings(settings: AppSettings) = dao.saveSettings(settings)

    suspend fun updateUsername(username: String): Boolean = withContext(Dispatchers.IO) {
        val cleanName = username.trim().take(24)
        if (cleanName.isBlank()) return@withContext false
        val user = dao.getUserAccountSnapshot() ?: return@withContext false
        dao.saveUserAccount(user.copy(username = cleanName))
        true
    }

    suspend fun updateAvatarPreset(avatarPreset: String): Boolean = withContext(Dispatchers.IO) {
        if (avatarPreset !in AvatarCatalog.validIds) return@withContext false
        val user = dao.getUserAccountSnapshot() ?: return@withContext false
        dao.saveUserAccount(user.copy(avatarPreset = avatarPreset))
        true
    }

    suspend fun updateProfile(username: String, bio: String, presence: String, avatarPreset: String): Boolean = withContext(Dispatchers.IO) {
        val cleanName = username.trim().take(24)
        val cleanBio = bio.trim().take(120)
        val cleanPresence = presence.trim().take(24)
        if (cleanName.isBlank() || cleanBio.isBlank() || cleanPresence.isBlank() || avatarPreset !in AvatarCatalog.validIds) return@withContext false
        val user = dao.getUserAccountSnapshot() ?: return@withContext false
        dao.saveUserAccount(user.copy(username = cleanName, profileBio = cleanBio, presenceStatus = cleanPresence, avatarPreset = avatarPreset))
        true
    }

    suspend fun equipWardrobeItem(itemId: String): Boolean = withContext(Dispatchers.IO) {
        val item = dao.getWardrobeItem(itemId) ?: return@withContext false
        if (!item.isOwned) return@withContext false
        dao.equipWardrobeItem(item.id, item.slot)

        // An outfit is both a wardrobe choice and the avatar preset used by the
        // online room. Keep the two persisted sources aligned after equipping;
        // otherwise the UI would show the new outfit while reconnecting with the
        // previous avatar preset.
        if (item.slot == "Atuendo") {
            val preset = item.avatarPreset.takeIf { it in AvatarCatalog.validIds }
            if (preset != null) {
                dao.getUserAccountSnapshot()?.let { user ->
                    dao.saveUserAccount(user.copy(avatarPreset = preset))
                }
            }
        }
        true
    }

    suspend fun equipWeapon(weaponId: String) {
        if (dao.getWeapon(weaponId) != null) dao.equipWeapon(weaponId)
    }

    suspend fun upgradeWeapon(weapon: WeaponItem): Boolean = withContext(Dispatchers.IO) {
        // Read the latest rows so a stale Compose snapshot can never spend resources twice.
        val currentUser = dao.getUserAccountSnapshot() ?: return@withContext false
        val currentWeapon = dao.getWeapon(weapon.id) ?: return@withContext false
        if (currentWeapon.level >= 15) return@withContext false

        val diamondCost = if (currentWeapon.isMysticalPremium) 20L * currentWeapon.level else 0L
        val goldCost = if (!currentWeapon.isMysticalPremium) 500L * currentWeapon.level else 0L
        val essenceCost = 15 * currentWeapon.level
        if (currentUser.coronasDiamonds < diamondCost ||
            currentUser.goldCoins < goldCost ||
            currentUser.mysticalEssence < essenceCost
        ) return@withContext false

        val newLevel = currentWeapon.level + 1
        val updatedUser = currentUser.copy(
            coronasDiamonds = currentUser.coronasDiamonds - diamondCost,
            goldCoins = currentUser.goldCoins - goldCost,
            mysticalEssence = currentUser.mysticalEssence - essenceCost
        )
        val updatedWeapon = currentWeapon.copy(
            level = newLevel,
            damage = (currentWeapon.damage + 2).coerceAtMost(120),
            accuracy = (currentWeapon.accuracy + 1).coerceAtMost(100),
            auraEffectUnlocked = currentWeapon.auraEffectUnlocked || newLevel >= 5,
            floatRunesUnlocked = currentWeapon.floatRunesUnlocked || newLevel >= 10,
            goldenMasteryUnlocked = currentWeapon.goldenMasteryUnlocked || newLevel >= 15
        )
        // The upgrade currency remains part of the account balance; it is no longer
        // duplicated as a visible inventory resource.
        dao.saveUserAndWeapon(updatedUser, updatedWeapon)
        true
    }

    suspend fun spinRoulette(): String = withContext(Dispatchers.IO) {
        val user = dao.getUserAccountSnapshot() ?: return@withContext "No hay un perfil local disponible."
        if (user.coronasDiamonds < ROULETTE_COST_DIAMONDS) {
            return@withContext "Necesitas $ROULETTE_COST_DIAMONDS Diamantes para girar."
        }

        val rewardItem = dao.getInventoryItem("item_6")
        val reward = Random.nextInt(3)
        val updatedUser = user.copy(
            coronasDiamonds = user.coronasDiamonds - ROULETTE_COST_DIAMONDS,
            goldCoins = user.goldCoins + if (reward == 1) 10_000L else 0L,
            mysticalEssence = user.mysticalEssence + if (reward == 0) 100 else 0
        )
        when (reward) {
            0 -> {
                // Keep the existing upgrade economy without recreating a retired
                // inventory row for the account-level upgrade currency.
                dao.saveUserAccount(updatedUser)
                "¡Premio! +100 energía de mejora."
            }
            1 -> {
                dao.saveUserAccount(updatedUser)
                "¡Premio! 10,000 Oro Imperial."
            }
            else -> {
                val updatedFragments = (rewardItem ?: InventoryItem(
                    "item_6", "Fragmentos de Arma Mística", "Fragmento", 0, "Épica",
                    "Fragmentos obtenidos en la Ruleta Mística.", "ic_weapon_fragment"
                )).copy(quantity = (rewardItem?.quantity ?: 0) + 10)
                dao.saveUserAndInventoryItem(updatedUser, updatedFragments)
                "¡Premio! Fragmentos de Arma Mística x10."
            }
        }
    }

    suspend fun buyDiamonds(amount: Long, coinsBonus: Long = 0) {
        addCurrencies(coinsBonus.coerceAtLeast(0), amount.coerceAtLeast(0))
    }

    /** Completes only allowlisted local demo packs and writes the balance + receipt atomically. */
    suspend fun completeDemoPurchase(itemName: String, amountUsd: Double, method: String): String = withContext(Dispatchers.IO) {
        val cleanMethod = method.trim().takeIf { it in PAYMENT_METHODS }
            ?: return@withContext "Método no disponible en la demo."
        val cents = round(amountUsd * 100.0).toInt()
        val pack = PACKS_CENTS[cents] ?: return@withContext "Pack no disponible en la demo."
        val user = dao.getUserAccountSnapshot() ?: return@withContext "No hay un perfil local disponible."
        val now = System.currentTimeMillis()
        val receipt = PaymentTransaction(
            id = "demo_${UUID.randomUUID()}",
            itemName = itemName.trim().take(80).ifBlank { "Pack Imperial" },
            method = cleanMethod,
            amountUsdCents = cents,
            diamondsGranted = pack.first,
            goldGranted = pack.second,
            createdAt = now
        )
        dao.saveUserAndPayment(
            user.copy(coronasDiamonds = user.coronasDiamonds + pack.first, goldCoins = user.goldCoins + pack.second),
            receipt
        )
        "¡Pago DEMO registrado vía $cleanMethod! +${pack.first} Diamantes y +${pack.second} Oro."
    }

    /** A clan contribution spends personal gold and increases only clan progress. */
    suspend fun contributeToClan(goldCost: Long = 500L): String = withContext(Dispatchers.IO) {
        val safeCost = goldCost.coerceIn(1L, 50_000L)
        val user = dao.getUserAccountSnapshot() ?: return@withContext "No hay un perfil local disponible."
        val clan = dao.getClanStateSnapshot() ?: ClanState()
        if (user.goldCoins < safeCost) return@withContext "Necesitas ${safeCost} Oro para contribuir."
        val updatedClan = clan.copy(
            gloryPoints = clan.gloryPoints + safeCost,
            weeklyPoints = (clan.weeklyPoints + safeCost).coerceAtMost(clan.weeklyGoal),
            contributionGold = clan.contributionGold + safeCost,
            contributionCount = clan.contributionCount + 1,
            updatedAt = System.currentTimeMillis()
        )
        dao.saveUserAndClan(user.copy(goldCoins = user.goldCoins - safeCost), updatedClan)
        "Contribución confirmada: -${safeCost} Oro, +${safeCost} Gloria del clan."
    }

    suspend fun addCurrencies(gold: Long = 0L, diamonds: Long = 0L): Boolean = withContext(Dispatchers.IO) {
        if (gold < 0L || diamonds < 0L) return@withContext false
        val user = dao.getUserAccountSnapshot() ?: return@withContext false
        dao.saveUserAccount(
            user.copy(
                coronasDiamonds = user.coronasDiamonds + diamonds,
                goldCoins = user.goldCoins + gold
            )
        )
        true
    }

    suspend fun recordBattleResult(victory: Boolean, kills: Int): Boolean = withContext(Dispatchers.IO) {
        val user = dao.getUserAccountSnapshot() ?: return@withContext false
        dao.saveUserAccount(
            user.copy(
                goldCoins = user.goldCoins + if (victory) 15_000L else 0L,
                coronasDiamonds = user.coronasDiamonds + if (victory) 50L else 0L,
                totalWins = user.totalWins + if (victory) 1 else 0,
                totalKills = user.totalKills + kills.coerceAtLeast(0)
            )
        )
        true
    }

    suspend fun saveDiscoSelection(trackId: String? = null, emoteId: String? = null): Boolean = withContext(Dispatchers.IO) {
        val current = dao.getDiscoStateSnapshot() ?: DiscoState()
        val cleanTrack = trackId?.takeIf { it in DISCO_TRACK_IDS } ?: current.selectedTrackId
        val cleanEmote = emoteId?.takeIf { it in DISCO_EMOTE_IDS } ?: current.selectedEmoteId
        dao.saveDiscoState(current.copy(
            selectedTrackId = cleanTrack,
            selectedEmoteId = cleanEmote,
            emoteCount = if (emoteId != null && emoteId in DISCO_EMOTE_IDS) {
                current.emoteCount + 1
            } else {
                current.emoteCount
            },
            updatedAt = System.currentTimeMillis()
        ))
        true
    }

    suspend fun settleMiniGame(gameId: String, won: Boolean, rewardGold: Long, rewardDiamonds: Long): MiniGameProgress = withContext(Dispatchers.IO) {
        val cleanGame = gameId.takeIf { it in MINI_GAME_IDS } ?: "imperial_chess"
        // Rewards come from the local UI, so clamp both sides before persisting them.
        val safeGold = rewardGold.coerceIn(0L, 50_000L)
        val safeDiamonds = rewardDiamonds.coerceIn(0L, 500L)
        val current = dao.getMiniGameProgressSnapshot() ?: MiniGameProgress()
        val updated = current.copy(
            selectedGameId = cleanGame,
            gamesPlayed = current.gamesPlayed + 1,
            gamesWon = current.gamesWon + if (won) 1 else 0,
            lastResult = if (won) "Victoria en $cleanGame" else "Partida completada en $cleanGame",
            lastRewardGold = safeGold,
            lastRewardDiamonds = safeDiamonds,
            updatedAt = System.currentTimeMillis()
        )
        val user = dao.getUserAccountSnapshot()
        if (user != null) {
            dao.saveUserAndMiniGameProgress(
                user.copy(
                    coronasDiamonds = user.coronasDiamonds + safeDiamonds,
                    goldCoins = user.goldCoins + safeGold
                ),
                updated
            )
        } else {
            dao.saveMiniGameProgress(updated)
        }
        updated
    }

    suspend fun saveSelectedMiniGame(gameId: String): Boolean = withContext(Dispatchers.IO) {
        val cleanGame = gameId.takeIf { it in MINI_GAME_IDS } ?: return@withContext false
        val current = dao.getMiniGameProgressSnapshot() ?: MiniGameProgress()
        dao.saveMiniGameProgress(current.copy(selectedGameId = cleanGame, updatedAt = System.currentTimeMillis()))
        true
    }

    suspend fun sendChatMessage(sender: String, message: String, channel: String): Boolean = withContext(Dispatchers.IO) {
        val cleanSender = sender.trim().take(24)
        val cleanMessage = message.trim().take(200)
        val cleanChannel = channel.trim().uppercase()
        if (cleanSender.isBlank() || cleanMessage.isBlank() || cleanChannel !in CHAT_CHANNELS) {
            return@withContext false
        }
        dao.insertChatMessage(
            ChatMessage(
                senderName = cleanSender,
                message = cleanMessage,
                channel = cleanChannel
            )
        )
        true
    }

    private companion object {
        val DISCO_TRACK_IDS = setOf("anthem", "thrones", "lions", "sanctuary")
        val DISCO_EMOTE_IDS = setOf("throne_dance", "rune_spin", "blue_fire", "royal_wave")
        val MINI_GAME_IDS = setOf("imperial_chess", "rune_cards", "lion_domino")
        val PAYMENT_METHODS = setOf("CubaPay", "Zelle", "PayPal", "Card")
        val CHAT_CHANNELS = setOf("GLOBAL", "CLAN", "SQUAD")
        val PACKS_CENTS = mapOf(499 to (500L to 50_000L), 999 to (1_200L to 50_000L), 2_499 to (3_500L to 50_000L), 4_999 to (8_000L to 50_000L))
        const val ROULETTE_COST_DIAMONDS = 25L
    }
}
