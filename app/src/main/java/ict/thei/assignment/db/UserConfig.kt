package ict.thei.assignment.db

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update

@Entity
data class UserConfig(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(defaultValue = "") val password: String,
    @ColumnInfo(defaultValue = "false") val hide_income: Boolean,
    @ColumnInfo(defaultValue = "false") val hide_balance: Boolean
)

data class UserConfigInsert(
    val password: String
)

@Dao
interface UserConfigDao {
    @Insert(entity = UserConfig::class)
    fun initialize(uc: UserConfigInsert)

    @Query("SELECT * FROM userconfig")
    fun get(): UserConfig

    @Update
    fun update(settings: UserConfig)
}