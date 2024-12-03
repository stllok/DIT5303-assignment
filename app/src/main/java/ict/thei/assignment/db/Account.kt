package ict.thei.assignment.db

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Update

@Entity(
    foreignKeys = [ForeignKey(
        entity = Currency::class,
        parentColumns = arrayOf("rowid"),
        childColumns = arrayOf("currencyId"),
        onDelete = ForeignKey.RESTRICT
    )]
)
data class Account(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "rowid") val id: Long,
    val name: String,
    val countTotalBalance: Boolean,
    val currencyId: Long
)

data class AccountAndCurrency(
    @Embedded val account: Account,
    @Relation(
        parentColumn = "id",
        entityColumn = "currencyId"
    )
    val currency: Currency
)

data class AccountInsert(
    val name: String,
    val countTotalBalance: Boolean = true,
    val currencyId: Long
)

@Dao
interface AccountDao {
    @Insert(entity = Account::class)
    fun insertAll(vararg accounts: AccountInsert)

    @Delete
    fun delete(account: Account)

    @Query("SELECT * FROM account")
    fun getAll(): List<Account>

    @Query("SELECT * FROM account WHERE rowid = :rowId")
    fun get(rowId: Long): Account?

    @Update
    fun update(account: Account)

    @Query("SELECT SUM(r.amount) FROM account a " +
            "INNER JOIN record r " +
            "ON a.rowid = r.accountId " +
            "WHERE a.rowid = :accountId")
    fun totalBalance(accountId: Long): Long
}