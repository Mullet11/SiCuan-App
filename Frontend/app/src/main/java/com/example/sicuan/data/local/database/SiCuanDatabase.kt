package com.example.sicuan.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.sicuan.data.local.dao.TransactionDao
import com.example.sicuan.data.local.entity.TransactionEntity

@Database(
    entities = [TransactionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SiCuanDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: SiCuanDatabase? = null

        fun getDatabase(context: Context): SiCuanDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SiCuanDatabase::class.java,
                    "sicuan_database"
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}