package com.example.pos1.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orderlist")
data class Orderlist(
    @PrimaryKey
    val orId: String = "",
    @ColumnInfo(name = "tbnum")
    val tbnum: Int = 0,
    @ColumnInfo(name = "staffId")
    val staffId: Int = 0,
    @ColumnInfo(name = "Date")
    val date: String = "",
    @ColumnInfo(name = "Amount")
    val amount: Double = 0.0,
    @ColumnInfo(name = "Status")
    val status: String = "",
    @ColumnInfo(name = "Payment method")
    val payment: String = ""
)

