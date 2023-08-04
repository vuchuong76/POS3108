package com.example.pos1.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.pos1.entity.Roster
import com.example.pos1.entity.Table
import kotlinx.coroutines.flow.Flow
@Dao
interface RosterDao {
    @Query("SELECT * FROM roster ORDER BY id ASC")
    fun getAll(): Flow<List<Roster>>

    @Query("SELECT * FROM roster WHERE id = :id")
    fun getRosterById(id: Int): Flow<Roster>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(roster: Roster)

    @Update
    suspend fun update(roster: Roster)

    @Delete
    suspend fun delete(roster: Roster)

    @Query("DELETE FROM Roster")
    suspend fun deleteAll()

    @Query("SELECT `Start Time`AND `Finish Time` FROM roster")
    fun getAllUserNames(): LiveData<List<String>>
}