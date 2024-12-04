package ict.thei.assignment.db

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update

@Entity(
    foreignKeys = [ForeignKey(
        entity = Account::class,
        parentColumns = arrayOf("rowid"),
        childColumns = arrayOf("accountId"),
        onDelete = ForeignKey.RESTRICT
    ), ForeignKey(
        entity = Category::class,
        parentColumns = arrayOf("rowid"),
        childColumns = arrayOf("categoryId"),
        onDelete = ForeignKey.RESTRICT
    )]
)
data class Record(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "rowid") val id: Long,
    val name: String,
    val description: String,
    val amount: Long,
    val recordAt: Long,
    val accountId: Long,
    val categoryId: Long
)

data class RecordInsert(
    val name: String,
    val description: String,
    val amount: Long,
    val accountId: Long,
    val categoryId: Long,
    val recordAt: Long = System.currentTimeMillis(),
)

@Dao
interface RecordDao {
    @Insert(entity = Record::class)
    fun insertAll(vararg records: RecordInsert)

    @Delete
    fun delete(records: Record)

    @Update
    fun update(records: Record)

//    @Transaction
//    @Query("SELECT * FROM record")
//    fun getAllRecordAndAccount(): List<RecordAndAccount>
//
//    @Transaction
//    @Query("SELECT * FROM record WHERE rowid = :rowId")
//    fun getAllRecordAndAccount(rowId: Long): RecordAndAccount

    @Query("SELECT * FROM record ORDER BY recordAt DESC")
    fun getAll(): List<Record>

    @Query("SELECT * FROM record WHERE rowid = :rowId")
    fun get(rowId: Long): Record?

    @Query("SELECT * FROM record WHERE accountId = :accountId AND strftime('%m', recordAt) = :month")
    fun getAccountAndMonth(accountId: Long, month: Int): List<Record>

    @Query("SELECT * FROM record WHERE accountId = :accountId")
    fun getAccount(accountId: Long): List<Record>

    @Query("SELECT * FROM record WHERE strftime('%m', recordAt) = :month")
    fun getMonth(month: Int): List<Record>

}