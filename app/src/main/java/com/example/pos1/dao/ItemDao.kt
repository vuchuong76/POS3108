package com.example.pos1.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.pos1.entity.Item
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM items ORDER BY name ASC")
    fun getItems(): Flow<List<Item>>

    @Query("SELECT * FROM items WHERE id = :id")
    fun getItem(id: Int): Flow<Item>
    @Query("SELECT * FROM items WHERE id = :id")
    suspend fun getItemById(id: Int): Item?
    @Query("SELECT COUNT(*) FROM items WHERE name= :name")
    suspend fun countItemWithName(name: String):Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Item)

    @Update
    suspend fun update(item: Item)

    @Delete
    suspend fun delete(item: Item)

    @Query("DELETE FROM items")
    suspend fun deleteAll()
}
