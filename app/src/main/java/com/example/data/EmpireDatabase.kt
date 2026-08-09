package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserAccount::class,
        WeaponItem::class,
        InventoryItem::class,
        FriendUser::class,
        ChatMessage::class
    ],
    version = 1,
    exportSchema = false
)
abstract class EmpireDatabase : RoomDatabase() {
    abstract fun empireDao(): EmpireDao

    companion object {
        @Volatile
        private var INSTANCE: EmpireDatabase? = null

        fun getInstance(context: Context): EmpireDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EmpireDatabase::class.java,
                    "empire_of_kings_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
