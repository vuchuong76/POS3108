package com.example.pos1.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey
    val staffId: String,
    @ColumnInfo(name="Password")
    val password:String,
    @ColumnInfo(name="Staffname")
    val staffname:String,
    @ColumnInfo(name="Age")
    val age:Int,
    @ColumnInfo(name="Position")
    val position:String,
    @ColumnInfo(name="Tel")
    val tel:String,
    @ColumnInfo(name="Address")
    val address:String
)