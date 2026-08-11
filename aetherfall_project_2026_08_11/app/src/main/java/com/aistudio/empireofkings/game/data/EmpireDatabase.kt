package com.aistudio.empireofkings.game.data

import android.content.Context
import androidx.room.Database
import androidx.room.migration.Migration
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        UserAccount::class,
        WeaponItem::class,
        InventoryItem::class,
        FriendUser::class,
        ChatMessage::class,
        AppSettings::class,
        WardrobeItem::class,
        DiscoState::class,
        MiniGameProgress::class,
        ClanState::class,
        PaymentTransaction::class
    ],
    // Schema 4 remains the supported previous on-device format; v5 is additive.
    // Previous database version = 4
    version = 5,
    exportSchema = false
)
abstract class EmpireDatabase : RoomDatabase() {
    abstract fun empireDao(): EmpireDao

    companion object {
        /** Adds the persistent settings row without destroying Room game progress. */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """CREATE TABLE IF NOT EXISTS app_settings (
                        id TEXT NOT NULL,
                        soundEnabled INTEGER NOT NULL,
                        musicEnabled INTEGER NOT NULL,
                        effectsEnabled INTEGER NOT NULL,
                        notificationsEnabled INTEGER NOT NULL,
                        hapticsEnabled INTEGER NOT NULL,
                        graphicsQuality TEXT NOT NULL,
                        screenMode TEXT NOT NULL,
                        PRIMARY KEY(id)
                    )""".trimIndent()
                )
                database.execSQL(
                    """INSERT OR IGNORE INTO app_settings
                        (id, soundEnabled, musicEnabled, effectsEnabled, notificationsEnabled,
                         hapticsEnabled, graphicsQuality, screenMode)
                        VALUES ('local_settings', 1, 1, 1, 1, 1, 'ALTA', 'ADAPTABLE')""".trimIndent()
                )
            }
        }

        /** Adds profile fields and the cosmetic wardrobe without deleting progress. */
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE user_account ADD COLUMN avatarPreset TEXT NOT NULL DEFAULT 'king_warrior'")
                database.execSQL("ALTER TABLE user_account ADD COLUMN profileBio TEXT NOT NULL DEFAULT 'Guardián del Trono Imperial.'")
                database.execSQL("ALTER TABLE user_account ADD COLUMN presenceStatus TEXT NOT NULL DEFAULT 'En Lobby'")
                database.execSQL(
                    """CREATE TABLE IF NOT EXISTS wardrobe_items (
                        id TEXT NOT NULL,
                        name TEXT NOT NULL,
                        slot TEXT NOT NULL,
                        rarity TEXT NOT NULL,
                        description TEXT NOT NULL,
                        iconName TEXT NOT NULL,
                        avatarPreset TEXT NOT NULL,
                        isOwned INTEGER NOT NULL,
                        isEquipped INTEGER NOT NULL,
                        PRIMARY KEY(id)
                    )""".trimIndent()
                )
            }
        }

        /** Adds resumable Disco and Games progress without deleting existing accounts or loot. */
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """CREATE TABLE IF NOT EXISTS disco_state (
                        id TEXT NOT NULL,
                        selectedTrackId TEXT NOT NULL,
                        selectedEmoteId TEXT NOT NULL,
                        emoteCount INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL,
                        PRIMARY KEY(id)
                    )""".trimIndent()
                )
                database.execSQL(
                    """CREATE TABLE IF NOT EXISTS mini_game_progress (
                        id TEXT NOT NULL,
                        selectedGameId TEXT NOT NULL,
                        gamesPlayed INTEGER NOT NULL,
                        gamesWon INTEGER NOT NULL,
                        lastResult TEXT NOT NULL,
                        lastRewardGold INTEGER NOT NULL,
                        lastRewardDiamonds INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL,
                        PRIMARY KEY(id)
                    )""".trimIndent()
                )
            }
        }

        /** Adds clan progression and demo purchase receipts without touching game rows. */
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """CREATE TABLE IF NOT EXISTS clan_state (
                        id TEXT NOT NULL,
                        clanName TEXT NOT NULL,
                        level INTEGER NOT NULL,
                        gloryPoints INTEGER NOT NULL,
                        weeklyPoints INTEGER NOT NULL,
                        weeklyGoal INTEGER NOT NULL,
                        contributionGold INTEGER NOT NULL,
                        contributionCount INTEGER NOT NULL,
                        announcement TEXT NOT NULL,
                        updatedAt INTEGER NOT NULL,
                        PRIMARY KEY(id)
                    )""".trimIndent()
                )
                database.execSQL(
                    """CREATE TABLE IF NOT EXISTS payment_transactions (
                        id TEXT NOT NULL,
                        itemName TEXT NOT NULL,
                        method TEXT NOT NULL,
                        amountUsdCents INTEGER NOT NULL,
                        diamondsGranted INTEGER NOT NULL,
                        goldGranted INTEGER NOT NULL,
                        status TEXT NOT NULL,
                        createdAt INTEGER NOT NULL,
                        PRIMARY KEY(id)
                    )""".trimIndent()
                )
            }
        }

        @Volatile
        private var INSTANCE: EmpireDatabase? = null

        fun getInstance(context: Context): EmpireDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EmpireDatabase::class.java,
                    "empire_of_kings_db"
                )
                    // Versions 1-5 are covered by explicit additive migrations. Do not silently
                    // erase a player's account and progress when opening a release build.
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
