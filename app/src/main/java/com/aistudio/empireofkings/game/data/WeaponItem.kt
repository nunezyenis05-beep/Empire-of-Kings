package com.aistudio.empireofkings.game.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weapons")
data class WeaponItem(
    @PrimaryKey val id: String,
    val name: String,
    val category: String, // Pistola, Escopeta, Subfusil, Rifle, Sniper, Pesada, Mágica, Arcos, Cuerpo a Cuerpo
    val isMysticalPremium: Boolean = false,
    val level: Int = 1, // 1 to 15
    val damage: Int,
    val fireRate: Int,
    val range: Int,
    val accuracy: Int,
    val costGold: Long = 2000L,
    val costDiamonds: Long = 0L,
    val isEquipped: Boolean = false,
    val auraEffectUnlocked: Boolean = false, // Unlocked at Lv 5
    val floatRunesUnlocked: Boolean = false, // Unlocked at Lv 10
    val goldenMasteryUnlocked: Boolean = false, // Unlocked at Lv 15
    val description: String
)
