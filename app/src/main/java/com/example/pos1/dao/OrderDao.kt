package com.example.pos1.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.pos1.entity.Order
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Query("SELECT * FROM order_entity ORDER BY itemId ASC")
    fun getOrders(): Flow<List<Order>>

    @Query("SELECT * FROM order_entity WHERE itemId = :itemId")
    fun getOrderById(itemId: Int): Flow<Order>

    @Query("UPDATE order_entity SET quantity = :newQuantity WHERE itemId = :itemId")
    suspend fun updateOrderQuantity(itemId: Int, newQuantity: Int)

    @Query("SELECT quantity FROM order_entity WHERE itemId = :itemId")
    fun getOrderQuantity(itemId: Int): Flow<Int>

    @Insert
    suspend fun insert(order: Order)

//    @Query(
//        """SELECT *, quantity * price AS total_price FROM order_entity
//    ORDER BY total_price DESC LIMIT 3
//    """
//    )
//    fun getMostValuableOrder(): Flow<List<Order>>

    @Query("SELECT * FROM order_entity WHERE tableNumber = :tableNumber AND `payment_status` = 'waiting'")
    fun getOrderForTableAndStatus(tableNumber: Int): Flow<List<Order>>

    @Query("SELECT * FROM order_entity WHERE tableNumber = :tableNumber AND `payment_status` = 'paying'")
    fun getOrderForPay(tableNumber: Int): Flow<List<Order>>

    @Query("SELECT * FROM order_entity WHERE tableNumber = :tableNumber")
    fun getOrderForSelectedTable(tableNumber: Int): Flow<List<Order>>

    @Query("SELECT * FROM order_entity WHERE orderId = :orId AND `payment_status` = 'payed'")
    fun getOrderByOrId(orId: String): Flow<List<Order>>

    @Query("SELECT * FROM order_entity WHERE `order_status` = 'waiting' AND `payment_status` = 'waiting'")
    fun getOrderForKitchen(): Flow<List<Order>>

    @Query("SELECT * FROM order_entity WHERE payment_status != 'waiting'")
    fun getOrderforChangeColor(): Flow<List<Order>>

    @Query("SELECT * FROM order_entity WHERE tableNumber = :tableNumber AND `payment_status` = 'waiting' AND order_status!='served'")
    fun getOrderforback(tableNumber: Int): Flow<List<Order>>

    @Update
    suspend fun update(order: Order)

    @Delete
    suspend fun delete(order: Order)

    @Query("DELETE FROM order_entity")
    suspend fun deleteAll()

    @Query("DELETE FROM order_entity WHERE tableNumber = :tableNumber AND `payment_status` = 'waiting'")
    suspend fun deleteOrdersByTable(tableNumber: Int)

    @Query("DELETE FROM order_entity WHERE tableNumber = :tableNumber AND `payment_status` = 'waiting' AND order_status!='served'")
    suspend fun deleteOrdersByTable1(tableNumber: Int)


}

