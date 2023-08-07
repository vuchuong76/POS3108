package com.example.pos1.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kupon")
data class Kupon(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val code: String = "",
    val discount: Int=0,
)
