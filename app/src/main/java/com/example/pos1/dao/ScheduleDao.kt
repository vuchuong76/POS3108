package com.example.pos1.dao

import androidx.compose.ui.text.style.BaselineShift
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.pos1.entity.Roster
import com.example.pos1.entity.Schedule
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM schedule_table ORDER BY date ASC")
    fun getAll(): Flow<List<Schedule>>

    @Query("SELECT * FROM schedule_table WHERE date = :date")
    fun getAllByDate(date:String): Flow<List<Schedule>>

    @Query("SELECT * FROM schedule_table WHERE employee = :employee")
    fun getAllByName(employee:String): Flow<List<Schedule>>

    @Query("SELECT COUNT(*) FROM schedule_table WHERE employee=:employee AND date =:date AND shift=:shift")
    suspend fun countSchedule(employee: String,date: String,shift: String):Int

    @Query("SELECT * FROM schedule_table WHERE id = :id")
    fun getScheduleById(id: Int): Flow<Schedule>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(schedule: Schedule)

    @Update
    suspend fun update(schedule: Schedule)

    @Delete
    suspend fun delete(schedule: Schedule)

    @Query("DELETE FROM SCHEDULE_TABLE")
    suspend fun deleteAll()

}
