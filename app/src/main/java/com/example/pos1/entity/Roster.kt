package com.example.pos1.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Roster")
data class Roster(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "Start Time")
    val start_time: Int=0,
    @ColumnInfo(name = "Finish Time")
    val finish_time: Int=0
)