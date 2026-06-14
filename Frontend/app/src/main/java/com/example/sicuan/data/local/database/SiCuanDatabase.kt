package com.example.sicuan.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.sicuan.data.local.dao.PlanDao
import com.example.sicuan.data.local.dao.TransactionDao
import com.example.sicuan.data.local.dao.PlanHistoryDao
import com.example.sicuan.data.local.entity.PlanEntity
import com.example.sicuan.data.local.entity.PlanHistoryEntity
import com.example.sicuan.data.local.entity.TransactionEntity

@Database(
    entities = [
        TransactionEntity::class,
        PlanEntity::class,
        PlanHistoryEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class SiCuanDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao

    abstract fun planDao(): PlanDao
    abstract fun planHistoryDao(): PlanHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: SiCuanDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS budgets (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        category TEXT NOT NULL,
                        limitAmount REAL NOT NULL,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS budgets")
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS plans (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        title TEXT NOT NULL,
                        targetAmount REAL NOT NULL,
                        savedAmount REAL NOT NULL,
                        deadlineDateMillis INTEGER NOT NULL,
                        createdAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS plan_history (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        planId INTEGER NOT NULL,
                        amount REAL NOT NULL,
                        type TEXT NOT NULL,
                        dateMillis INTEGER NOT NULL,
                        FOREIGN KEY(planId) REFERENCES plans(id) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_plan_history_planId` ON `plan_history` (`planId`)")
            }
        }

        fun getDatabase(context: Context): SiCuanDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SiCuanDatabase::class.java,
                    "sicuan_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
