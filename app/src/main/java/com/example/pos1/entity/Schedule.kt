package com.example.pos1.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedule_table")
data class Schedule(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String,
    val shift: String,
    val employee: String
)