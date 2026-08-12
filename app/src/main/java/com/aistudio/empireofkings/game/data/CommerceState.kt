package com.aistudio.empireofkings.game.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Local clan progression. It is intentionally separate from the account so a
 * clan contribution cannot be confused with personal gold or XP. */
@Entity(tableName = "clan_state")
data class ClanState(
    @PrimaryKey val id: String = "local_clan",
    val clanName: String = "IMPERIO DORADO",
    val level: Int = 18,
    val gloryPoints: Long = 72_450L,
    val weeklyPoints: Long = 8_400L,
    val weeklyGoal: Long = 10_000L,
    val contributionGold: Long = 0L,
    val contributionCount: Int = 0,
    val announcement: String = "Guardianes del trono: unidos por el reino.",
    val updatedAt: Long = 0L
)

/** Auditable local demo purchase receipt. No card number or payment secret is stored. */
@Entity(tableName = "payment_transactions")
data class PaymentTransaction(
    @PrimaryKey val id: String,
    val itemName: String,
    val method: String,
    val amountUsdCents: Int,
    val diamondsGranted: Long,
    val goldGranted: Long,
    val status: String = "DEMO_COMPLETED",
    val createdAt: Long = 0L
)
