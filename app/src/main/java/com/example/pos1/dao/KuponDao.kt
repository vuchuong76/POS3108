package com.example.pos1.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.pos1.entity.Item
import com.example.pos1.entity.Kupon
import kotlinx.coroutines.flow.Flow

@Dao
interface KuponDao {
    @Query("SELECT * FROM kupon ORDER BY id ASC")
    fun getAll(): Flow<List<Kupon>>

    @Query("SELECT * FROM kupon WHERE id = :id")
    fun getKupon(id: Int): Flow<Kupon>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(kupon: Kupon)

    @Update
    suspend fun update(kupon: Kupon)

    @Delete
    suspend fun delete(kupon: Kupon)

    @Query("DELETE FROM kupon")
    suspend fun deleteAll()
}