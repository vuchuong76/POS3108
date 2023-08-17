package com.example.pos1.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.pos1.entity.Coupon
import kotlinx.coroutines.flow.Flow

@Dao
interface CouponDao {
    @Query("SELECT * FROM coupon ORDER BY id ASC")
    fun getAll(): Flow<List<Coupon>>

    @Query("SELECT COUNT(*) FROM coupon WHERE code=:code")
    suspend fun countCouponByCode(code: String):Int

    @Query("SELECT * FROM coupon WHERE id = :id")
    fun getCoupon(id: Int): Flow<Coupon>
    @Query("SELECT * FROM coupon WHERE code = :code")
    fun getCouponByCode(code: String): Flow<Coupon>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(coupon: Coupon)

    @Update
    suspend fun update(coupon: Coupon)

    @Delete
    suspend fun delete(coupon: Coupon)

    @Query("DELETE FROM coupon")
    suspend fun deleteAll()
}