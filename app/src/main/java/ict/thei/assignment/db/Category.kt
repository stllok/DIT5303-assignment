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
data class Category(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "rowid") val id: Long,
    val name: String
)

data class CategoryInsert(
    val name: String,
)

@Dao
interface CategoryDao {
    @Insert(entity = Category::class)
    fun insertAll(vararg category: CategoryInsert)

    @Delete
    fun delete(category: Category)

    @Query("SELECT * FROM category")
    fun getAll(): List<Category>

    @Query("SELECT * FROM category WHERE rowid = :rowId")
    fun get(rowId: Int): Category?

    @Update
    fun update(vararg category: Category)
}
