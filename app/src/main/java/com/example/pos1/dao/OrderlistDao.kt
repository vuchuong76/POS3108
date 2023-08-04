package com.example.pos1.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.pos1.entity.Orderlist
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderlistDao {
    @Query("SELECT * FROM `orderlist` ORDER BY orId DESC")
    fun getAll(): Flow<List<Orderlist>>
    @Query("SELECT * FROM `orderlist` WHERE orId = :orId")
    fun getOrderByOrId(orId: Int): Flow<Orderlist>

    @Insert
    suspend fun insert(orderlist: Orderlist)

    @Update
    suspend fun update(orderlist: Orderlist)

    @Delete
    suspend fun delete(orderlist: Orderlist)

    @Query("DELETE FROM `orderlist`")
    suspend fun deleteAll()
}