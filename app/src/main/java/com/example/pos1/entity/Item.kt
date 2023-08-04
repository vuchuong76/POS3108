package com.example.pos1.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String = "",
    val type: String = "",
    val stock: Int = 0,
    val price: Double = 0.0,
    val image: String? = null
)

