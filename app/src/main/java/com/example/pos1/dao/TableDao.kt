package com.example.pos1.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.pos1.entity.Table
import kotlinx.coroutines.flow.Flow

@Dao
interface TableDao {

    @Query("SELECT * FROM table_number ORDER BY table_number ASC")
    fun getTables(): Flow<List<Table>>


    @Query("SELECT * FROM table_number WHERE table_number = :tableNumber")
    fun getTableByNumber(tableNumber: Int): Flow<Table>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(table: Table)

    @Query("SELECT COUNT(*) FROM table_number WHERE table_number = :number")
    suspend fun countTableWithNumber(number: Int): Int
    @Query("UPDATE table_number SET status = :status WHERE table_number = :tableNumber")
    suspend fun updateTableStatus(tableNumber: Int, status: Int)

    @Update
    suspend fun update(table: Table)

    @Delete
    suspend fun delete(table: Table)

    @Query("DELETE FROM table_number")
    suspend fun deleteAll()

}
