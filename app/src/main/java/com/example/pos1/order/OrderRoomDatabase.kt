package com.example.pos1.order

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pos1.dao.CouponDao
import com.example.pos1.dao.ItemDao
import com.example.pos1.dao.OrderDao
import com.example.pos1.dao.OrderlistDao
import com.example.pos1.dao.RosterDao
import com.example.pos1.dao.ScheduleDao
import com.example.pos1.entity.Table
import com.example.pos1.dao.TableDao
import com.example.pos1.dao.UserDao
import com.example.pos1.entity.Coupon
import com.example.pos1.entity.Item
import com.example.pos1.entity.Order
import com.example.pos1.entity.Orderlist
import com.example.pos1.entity.Roster
import com.example.pos1.entity.Schedule
import com.example.pos1.entity.User

@Database(entities = [Order::class, Table::class, Item::class, Orderlist::class, User::class, Roster::class, Schedule::class, Coupon::class], version = 1, exportSchema = false)
abstract class OrderRoomDatabase : RoomDatabase() {
    abstract fun orderDao(): OrderDao
    abstract fun tableDao(): TableDao
    abstract fun itemDao(): ItemDao
    abstract fun orderlistDao(): OrderlistDao
    abstract fun userDao(): UserDao
    abstract fun rosterDao(): RosterDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun couponDao(): CouponDao

    companion object {
        @Volatile
        private var INSTANCE: OrderRoomDatabase? = null

        fun getDatabase(context: Context): OrderRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OrderRoomDatabase::class.java,
                    "database"
                )  .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}