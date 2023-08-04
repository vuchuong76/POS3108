package com.example.pos1.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_number")
data class Table(
    @PrimaryKey @ColumnInfo(name = "table_number")
    val number: Int,
    @ColumnInfo(name="Capacity")
    val capacity:Int ,
    @ColumnInfo(name="Status")
    var status:Int=0
)
