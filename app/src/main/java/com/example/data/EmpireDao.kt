package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EmpireDao {
    @Query("SELECT * FROM user_account WHERE id = 'local_player'")
    fun getUserAccount(): Flow<UserAccount?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserAccount(user: UserAccount)

    @Query("SELECT * FROM weapons ORDER BY isMysticalPremium DESC, level DESC")
    fun getAllWeapons(): Flow<List<WeaponItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeapons(weapons: List<WeaponItem>)

    @Query("UPDATE weapons SET isEquipped = CASE WHEN id = :weaponId THEN 1 ELSE 0 END")
    suspend fun equipWeapon(weaponId: String)

    @Update
    suspend fun updateWeapon(weapon: WeaponItem)

    @Query("SELECT * FROM inventory ORDER BY rarity DESC")
    fun getInventory(): Flow<List<InventoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventoryItems(items: List<InventoryItem>)

    @Query("SELECT * FROM friends")
    fun getFriends(): Flow<List<FriendUser>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriends(friends: List<FriendUser>)

    @Query("SELECT * FROM chat_messages WHERE channel = :channel ORDER BY timestamp ASC LIMIT 50")
    fun getChatMessages(channel: String): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(msg: ChatMessage)

    @Query("SELECT * FROM kingdom_state WHERE id = 'local_kingdom'")
    fun getKingdomState(): Flow<KingdomStateEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveKingdomState(state: KingdomStateEntity)
}
