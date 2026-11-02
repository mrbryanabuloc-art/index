package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FeedingLog::class], version = 2, exportSchema = false)
abstract class FeedingDatabase : RoomDatabase() {
    abstract fun feedingDao(): FeedingDao

    companion object {
        @Volatile
        private var INSTANCE: FeedingDatabase? = null

        fun getDatabase(context: Context): FeedingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FeedingDatabase::class.java,
                    "baby_feeding_database.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
