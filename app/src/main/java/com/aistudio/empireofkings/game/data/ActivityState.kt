package com.aistudio.empireofkings.game.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Persisted state for the social dance floor. It contains no audio bytes; tracks are replaceable IDs. */
@Entity(tableName = "disco_state")
data class DiscoState(
    @PrimaryKey val id: String = "local_discoteca",
    val selectedTrackId: String = "anthem",
    val selectedEmoteId: String = "throne_dance",
    val emoteCount: Int = 0,
    val updatedAt: Long = 0L
)

/** Local arcade progress and the last settled reward, used for resumable feedback. */
@Entity(tableName = "mini_game_progress")
data class MiniGameProgress(
    @PrimaryKey val id: String = "local_arcade",
    val selectedGameId: String = "imperial_chess",
    val gamesPlayed: Int = 0,
    val gamesWon: Int = 0,
    val lastResult: String = "Aún no hay partidas resueltas.",
    val lastRewardGold: Long = 0L,
    val lastRewardDiamonds: Long = 0L,
    val updatedAt: Long = 0L
)
