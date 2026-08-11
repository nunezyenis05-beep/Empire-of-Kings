package com.aistudio.empireofkings.game.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inventory")
data class InventoryItem(
    @PrimaryKey val id: String,
    val name: String,
    val type: String, // Cristal, Fragmento, Vestuario u otro tipo de recurso vigente
    val quantity: Int,
    val rarity: String, // Común, Rara, Épica, Legendaria, Mítica
    val description: String,
    val iconName: String
)

@Entity(tableName = "friends")
data class FriendUser(
    @PrimaryKey val id: String,
    val name: String,
    val level: Int,
    val isOnline: Boolean,
    val status: String, // "En Lobby", "En Batalla", "Desconectado"
    val rank: String,
    val avatarUrl: String = ""
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val senderName: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val channel: String = "GLOBAL" // GLOBAL, CLAN, SQUAD
)
