package com.example.pos1.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "order_entity",
)
data class Order(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "itemId")
    val itemId: Int = 0,
    @ColumnInfo(name = "orderId")
    var orderId: String = "",
    @ColumnInfo(name = "tableNumber")
    val tableNumber: Int,
    @ColumnInfo(name = "name")
    val name: String = "",
    @ColumnInfo(name = "time")
    val time: String = "",
    @ColumnInfo(name = "date")
    var date: String = "",
    @ColumnInfo(name = "quantity")
    var quantity: Int = 0,
    @ColumnInfo(name = "price")
    val price: Int = 0,
    @ColumnInfo(name = "order_status")
    val order_status: String="",
    @ColumnInfo(name = "payment_status")
    var pay_sta: String="",
)