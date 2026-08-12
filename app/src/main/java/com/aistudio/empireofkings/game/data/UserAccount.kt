package com.aistudio.empireofkings.game.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_account")
data class UserAccount(
    @PrimaryKey val id: String = "local_player",
    val username: String = "KING_PLAYER",
    val level: Int = 99,
    val title: String = "ELITE REY DE GUERRA",
    val goldCoins: Long = 1250000L,
    val coronasDiamonds: Long = 500L,
    val mysticalEssence: Int = 450,
    val avatarRes: String = "ic_king_avatar",
    val battlePassLevel: Int = 42,
    val clanName: String = "IMPERIO DORADO",
    val totalWins: Int = 128,
    val totalKills: Int = 1450,
    val isVip: Boolean = true,
    val avatarPreset: String = "king_warrior",
    val profileBio: String = "Guardián del Trono Imperial.",
    val presenceStatus: String = "En Lobby"
)
