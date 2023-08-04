package com.example.pos1.orlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.pos1.dao.OrderDao
import com.example.pos1.dao.OrderlistDao
import com.example.pos1.entity.Item
import com.example.pos1.entity.Order
import com.example.pos1.entity.Orderlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class OrderlistViewModel(private val orderlistDao: OrderlistDao, private  val orderDao: OrderDao) : ViewModel() {

    val allItems1: LiveData<List<Orderlist>> = orderlistDao.getAll().asLiveData()


     suspend fun addNewOrderlist(
         orId:String,
        tbnum: Int,
        staffId: Int,
        date: String,
        amount: Int,
        status: String,
        payment: String
    ) {
        val newOrderlist = getNewOrderListEntry(orId,tbnum, staffId, date, amount, status, payment)
        insertOrderList(newOrderlist)

         val list :List<Order> =orderDao.getOrderForPay(tbnum).firstOrNull() ?: emptyList()
         for (item in list) {
             var oder:Order = item
             oder.date=date
             oder.orderId = orId
             oder.pay_sta="payed"
             orderDao.update(item)
         }
    }

//    fun payed() {
//        viewModelScope.launch(Dispatchers.IO) {
//            val tableNumber = tb.value
//            if (tableNumber != null) {
//                val orders = orderDao.getOrderForPay(tableNumber).firstOrNull() ?: emptyList()
//                for (order in orders) {
//                    if (order.pay_sta == "paying") {
//                        // Tạo một bản sao của đơn hàng và cập nhật trạng thái
//                        val updatedOrder = order.copy(pay_sta = "payed")
//                        orderDao.update(updatedOrder)
//                    }
//                }
//            }
//        }
//    }
    private fun insertOrderList(orderlist: Orderlist) {
        viewModelScope.launch {
            orderlistDao.insert(orderlist)
        }
    }


    private fun getNewOrderListEntry(orId:String, tbnum: Int,
                                 staffId: Int,
                                 date: String,
                                 amount: Int,
                                 status: String,
                                 payment: String): Orderlist {
        return Orderlist(
            orId=orId,
            tbnum=tbnum,
            staffId=staffId,
            date=date,
            amount=amount,
            status=status,
            payment=payment
        )
    }
    fun updateOrderlist(orderlist: Orderlist) {
        viewModelScope.launch {
            orderlistDao.update(orderlist)
        }
    }
    fun deleteOrderlist(orderlist: Orderlist){
        viewModelScope.launch {
            orderlistDao.delete(orderlist)
        }
    }
}
class OrderListViewModelFactory(private val orderlistDao: OrderlistDao, private  val orderDao: OrderDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderlistViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OrderlistViewModel(orderlistDao,orderDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}