package ict.thei.assignment.db

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update

@Entity
data class Currency(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "rowid") val id: Long,
    val name: String,
    val abbreviation: String,
    val symbol: String
)

data class CurrencyInsert(
    val name: String,
    val abbreviation: String,
    val symbol: String
)

@Dao
interface CurrencyDao {
    @Insert(entity = Currency::class)
    fun insertAll(vararg currency: CurrencyInsert)

    @Delete
    fun delete(currency: Currency)

    @Query("SELECT * FROM currency")
    fun getAll(): List<Currency>

    @Query("SELECT * FROM currency WHERE rowid = :rowId")
    fun get(rowId: Long): Currency?

    @Update
    fun update(vararg currency: Currency)
}
