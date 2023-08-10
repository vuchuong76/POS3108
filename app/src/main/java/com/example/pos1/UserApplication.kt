package com.example.pos1

import android.app.Application

class UserApplication : Application() {
    // Using by lazy so the database is only created when needed
    // rather than when the application starts
    val orderDatabase: OrderRoomDatabase by lazy { OrderRoomDatabase.getDatabase(this) }
}