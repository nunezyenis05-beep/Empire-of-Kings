package com.aistudio.empireofkings.game.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A saved cosmetic loadout entry.  Cosmetics are deliberately separate from
 * weapons and inventory consumables so an outfit choice can be restored after
 * process death without spending currency or mutating the item count.
 */
@Entity(tableName = "wardrobe_items")
data class WardrobeItem(
    @PrimaryKey val id: String,
    val name: String,
    val slot: String, // Atuendo, Armadura, Capa, Corona, Accesorio
    val rarity: String,
    val description: String,
    val iconName: String,
    val avatarPreset: String = "king_warrior",
    val isOwned: Boolean = true,
    val isEquipped: Boolean = false
)
