package com.aistudio.empireofkings.game.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettings(
    @PrimaryKey val id: String = "local_settings",
    val soundEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val effectsEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val graphicsQuality: String = "ALTA",
    val screenMode: String = "ADAPTABLE"
)
