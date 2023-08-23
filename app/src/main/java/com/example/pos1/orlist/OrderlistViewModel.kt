package com.example.pos1.orlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.pos1.dao.OrderDao
import com.example.pos1.dao.OrderlistDao
import com.example.pos1.entity.Order
import com.example.pos1.entity.Orderlist
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class OrderlistViewModel(private val orderlistDao: OrderlistDao, private val orderDao: OrderDao) :
    ViewModel() {

    val allItems1: LiveData<List<Orderlist>> = orderlistDao.getAll().asLiveData()

//    private val _selectedOrId = MutableLiveData<String>()
//    val selectedOrId: LiveData<String> = _selectedOrId
    suspend fun addNewOrderlist(
        orId: String,
        tbnum: Int,
        userName: String,
        date: String,
        amount: Double,
        discount:Double,
        receive: Double,
        change: Double
    ) {
        val newOrderlist = getNewOrderListEntry(orId, tbnum, userName, date, amount,discount, receive, change)
        insertOrderList(newOrderlist)

        val list: List<Order> = orderDao.getOrderForPay(tbnum).firstOrNull() ?: emptyList()
        for (item in list) {
            val oder: Order = item
            oder.date = date
            oder.orderId = orId
            oder.pay_sta = "payed"
            orderDao.update(item)
        }
    }
    private fun insertOrderList(orderlist: Orderlist) {
        viewModelScope.launch {
            orderlistDao.insert(orderlist)
        }
    }

    private fun getNewOrderListEntry(
        orId: String,
        tbnum: Int,
        userName: String,
        date: String,
        amount: Double,
        discount: Double,
        receive: Double,
        change: Double,
    ): Orderlist {
        return Orderlist(
            orId = orId,
            tbnum = tbnum,
            userName = userName,
            date = date,
            amount = amount,
            discount=discount,
            receive = receive,
            change = change
        )
    }
}

class OrderListViewModelFactory(
    private val orderlistDao: OrderlistDao,
    private val orderDao: OrderDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderlistViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OrderlistViewModel(orderlistDao, orderDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}