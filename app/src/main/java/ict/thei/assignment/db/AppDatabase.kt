package ict.thei.assignment.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

private const val DB_NAME = "app.db"

@Database(
    entities = [Currency::class, Category::class, Account::class, UserConfig::class, Record::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun currencyDao(): CurrencyDao
    abstract fun categoryDao(): CategoryDao
    abstract fun accountDao(): AccountDao
    abstract fun userConfigDao(): UserConfigDao
    abstract fun recordDao(): RecordDao

    companion object {
        fun newInstance(context: Context) =
            Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
                .allowMainThreadQueries()
                .enableMultiInstanceInvalidation().build()

    }
}