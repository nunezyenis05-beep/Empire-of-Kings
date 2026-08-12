package com.aistudio.empireofkings.game.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EmpireDao {
    @Query("SELECT * FROM user_account WHERE id = 'local_player'")
    fun getUserAccount(): Flow<UserAccount?>

    @Query("SELECT * FROM user_account WHERE id = 'local_player' LIMIT 1")
    suspend fun getUserAccountSnapshot(): UserAccount?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserAccount(user: UserAccount)

    @Query("SELECT * FROM weapons ORDER BY isMysticalPremium DESC, level DESC")
    fun getAllWeapons(): Flow<List<WeaponItem>>

    @Query("SELECT * FROM weapons WHERE id = :weaponId LIMIT 1")
    suspend fun getWeapon(weaponId: String): WeaponItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeapons(weapons: List<WeaponItem>)

    @Query("UPDATE weapons SET isEquipped = CASE WHEN id = :weaponId THEN 1 ELSE 0 END")
    suspend fun equipWeapon(weaponId: String)

    @Update
    suspend fun updateWeapon(weapon: WeaponItem)

    @Transaction
    suspend fun saveUserAndWeapon(user: UserAccount, weapon: WeaponItem) {
        saveUserAccount(user)
        updateWeapon(weapon)
    }

    @Query("SELECT * FROM inventory ORDER BY rarity DESC")
    fun getInventory(): Flow<List<InventoryItem>>

    @Query("SELECT COUNT(*) FROM inventory")
    suspend fun getInventoryCount(): Int

    /** Removes only the legacy five-item inventory seed rows from existing installs. */
    @Query("DELETE FROM inventory WHERE id GLOB 'item_[1-5]'")
    suspend fun removeLegacyInventorySeedRows()

    /** Removes retired wardrobe resources from existing installs without touching other cosmetics. */
    @Query("DELETE FROM wardrobe_items WHERE id LIKE 'crown' || '_' || 'eternity'")
    suspend fun removeRetiredWardrobeRows()

    @Query("SELECT * FROM inventory WHERE id = :itemId LIMIT 1")
    suspend fun getInventoryItem(itemId: String): InventoryItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventoryItems(items: List<InventoryItem>)

    @Query("SELECT * FROM catalog_admin_state")
    fun getCatalogAdminStates(): Flow<List<CatalogAdminState>>

    @Query("SELECT * FROM catalog_admin_state WHERE itemId = :itemId LIMIT 1")
    suspend fun getCatalogAdminState(itemId: String): CatalogAdminState?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCatalogAdminStates(states: List<CatalogAdminState>)

    @Query("UPDATE catalog_admin_state SET availability = :availability, publishedForDiamondSale = :published WHERE itemId = :itemId")
    suspend fun updateCatalogAdminState(itemId: String, availability: String, published: Boolean)

    @Query("SELECT * FROM wardrobe_items ORDER BY isEquipped DESC, slot ASC, rarity DESC, name ASC")
    fun getWardrobeItems(): Flow<List<WardrobeItem>>

    @Query("SELECT * FROM wardrobe_items WHERE id = :itemId LIMIT 1")
    suspend fun getWardrobeItem(itemId: String): WardrobeItem?

    @Query("SELECT COUNT(*) FROM wardrobe_items")
    suspend fun getWardrobeCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWardrobeItems(items: List<WardrobeItem>)

    @Query("UPDATE wardrobe_items SET isEquipped = CASE WHEN id = :itemId THEN 1 ELSE 0 END WHERE slot = :slot")
    suspend fun equipWardrobeItem(itemId: String, slot: String)

    @Update
    suspend fun updateInventoryItem(item: InventoryItem)

    @Transaction
    suspend fun saveUserAndInventoryItem(user: UserAccount, item: InventoryItem) {
        saveUserAccount(user)
        updateInventoryItem(item)
    }

    @Query("SELECT * FROM friends")
    fun getFriends(): Flow<List<FriendUser>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriends(friends: List<FriendUser>)

    @Query("SELECT * FROM chat_messages WHERE channel = :channel ORDER BY timestamp ASC LIMIT 50")
    fun getChatMessages(channel: String): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(msg: ChatMessage)

    @Query("SELECT * FROM app_settings WHERE id = 'local_settings' LIMIT 1")
    fun getSettings(): Flow<AppSettings?>

    @Query("SELECT * FROM app_settings WHERE id = 'local_settings' LIMIT 1")
    suspend fun getSettingsSnapshot(): AppSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: AppSettings)

    @Query("SELECT * FROM disco_state WHERE id = 'local_discoteca' LIMIT 1")
    fun getDiscoState(): Flow<DiscoState?>

    @Query("SELECT * FROM disco_state WHERE id = 'local_discoteca' LIMIT 1")
    suspend fun getDiscoStateSnapshot(): DiscoState?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDiscoState(state: DiscoState)

    @Query("SELECT * FROM mini_game_progress WHERE id = 'local_arcade' LIMIT 1")
    fun getMiniGameProgress(): Flow<MiniGameProgress?>

    @Query("SELECT * FROM mini_game_progress WHERE id = 'local_arcade' LIMIT 1")
    suspend fun getMiniGameProgressSnapshot(): MiniGameProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMiniGameProgress(progress: MiniGameProgress)

    @Transaction
    suspend fun saveUserAndMiniGameProgress(user: UserAccount, progress: MiniGameProgress) {
        saveUserAccount(user)
        saveMiniGameProgress(progress)
    }

    @Query("SELECT * FROM clan_state WHERE id = 'local_clan' LIMIT 1")
    fun getClanState(): Flow<ClanState?>

    @Query("SELECT * FROM clan_state WHERE id = 'local_clan' LIMIT 1")
    suspend fun getClanStateSnapshot(): ClanState?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveClanState(state: ClanState)

    @Query("SELECT * FROM payment_transactions ORDER BY createdAt DESC LIMIT 20")
    fun getPaymentTransactions(): Flow<List<PaymentTransaction>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertPaymentTransaction(transaction: PaymentTransaction)

    @Transaction
    suspend fun saveUserAndClan(user: UserAccount, clan: ClanState) {
        saveUserAccount(user)
        saveClanState(clan)
    }

    @Transaction
    suspend fun saveUserAndPayment(user: UserAccount, transaction: PaymentTransaction) {
        saveUserAccount(user)
        insertPaymentTransaction(transaction)
    }
}
