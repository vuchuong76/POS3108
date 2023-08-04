package com.example.pos1.order

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.pos1.dao.OrderDao
import com.example.pos1.entity.Order
import com.example.pos1.entity.Orderlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class OrderViewModel(private val orderDao: OrderDao) : ViewModel() {
    //danh sách order để đổi màu bàn
    //lấy 3 order nhiều doanh thu nhất
//    val mostValuableOrder: LiveData<List<Order>> = orderDao.getMostValuableOrder().asLiveData()

    private val _countItem: MutableLiveData<Int> = MutableLiveData(0)
    val countItem: LiveData<Int> = _countItem

    private val _amount: MutableLiveData<Int> = MutableLiveData(0)
    val amount: LiveData<Int> = _amount

    //tính tiền order có trạng thái  paying
    private val _countItem1: MutableLiveData<Int> = MutableLiveData(0)
    val countItem1: LiveData<Int> = _countItem1

    private val _amount1: MutableLiveData<Int> = MutableLiveData(0)
    val amount1: LiveData<Int> = _amount1

    var id: String = ""



    // LiveData foodsForTable sẽ tự động cập nhật khi giá trị của _tableNumber thay đổi
    private val _selectedTableNumber = MutableLiveData<Int>()
    val selectedTableNumber: LiveData<Int> = _selectedTableNumber
    private val _selectedId = MutableLiveData<String>()
    val selectedId: LiveData<String> = _selectedId

    //mỗi khi _selectedTableNumber thay đổi, foodsForTable sẽ tự động cập nhật và
    // chứa danh sách đơn hàng cho bàn có số tableNumber và trạng thái thanh toán pay_sta.
    val orderForTable: LiveData<List<Order>> = _selectedTableNumber.switchMap { tableNumber ->
        orderDao.getOrderForTableAndStatus(tableNumber).asLiveData()
    }

    //    danh sách order đang ở trạng thái tính tiền paying
    val orderForPay: LiveData<List<Order>> = _selectedTableNumber.switchMap { tableNumber ->
        orderDao.getOrderForPay(tableNumber).asLiveData()
    }
    val orderById: LiveData<List<Order>> = _selectedId.switchMap { orderId ->
        orderDao.getOrderByOrId(orderId).asLiveData()
    }

    fun getOrderForKitchen(): LiveData<List<Order>> {
        val flow: Flow<List<Order>> = orderDao.getOrderForKitchen()
        return flow.asLiveData()
    }

    fun setAmountAllItems(value: Int) {
        _amount1.value = value
    }

//    private val _orderForPay = MutableLiveData<List<Order>>()
//    val orderForPay: LiveData<List<Order>>
//        get() = _orderForPay


    //chuyển trạng thái phục vụ order từ checking thành waiting
    fun updateStatusFromCheckingToWaiting() {
        viewModelScope.launch(Dispatchers.IO) {
            val tableNumber = _selectedTableNumber.value
            if (tableNumber != null) {
                val orders =

                    orderDao.getOrderForTableAndStatus(tableNumber).firstOrNull() ?: emptyList()
                for (order in orders) {
                    if (order.order_status == "checking") {
                        // Tạo một bản sao của đơn hàng và cập nhật trạng thái
                        val updatedOrder = order.copy(order_status = "waiting")
                        orderDao.update(updatedOrder)
                    }
                }
            }
        }
    }

//    fun updateOrId(orId: Int){
//        viewModelScope.launch(Dispatchers.IO) {
//            val tableNumber = _selectedTableNumber.value
//            if (tableNumber != null) {
//                val orders = orderDao.getOrderForTableAndStatus(tableNumber).firstOrNull() ?: emptyList()
//                for (order in orders) {
//                    if (order.orderId == 0) {
//                        // Tạo một bản sao của đơn hàng và cập nhật trạng thái
//                        val updatedOrder = order.copy(orderId = orId )
//                        orderDao.update(updatedOrder)
//                    }
//                }
//            }
//        }
//        }

    //chuyển sang trạng thái trả tiền
    fun pay() {
        viewModelScope.launch(Dispatchers.IO) {
            val tableNumber = _selectedTableNumber.value
            if (tableNumber != null) {
                val orders =
                    orderDao.getOrderForTableAndStatus(tableNumber).firstOrNull() ?: emptyList()
                for (order in orders) {
                    if (order.pay_sta == "waiting") {
                        // Tạo một bản sao của đơn hàng và cập nhật trạng thái
                        val updatedOrder = order.copy(pay_sta = "paying")
                        orderDao.update(updatedOrder)
                    }
                }
            }
        }
    }
    fun cancel() {
        viewModelScope.launch(Dispatchers.IO) {
            val tableNumber = _selectedTableNumber.value
            if (tableNumber != null) {
                // Sử dụng coroutine async để thực hiện tác vụ truy vấn cơ sở dữ liệu không chính đồng
                val orders = async(Dispatchers.IO) {
                    orderDao.getOrderForPay(tableNumber).firstOrNull() ?: emptyList()
                }.await() // Sử dụng await để đợi kết quả trả về từ coroutine

                for (order in orders) {
                    if (order.pay_sta == "paying") {
                        // Tạo một bản sao của đơn hàng và cập nhật trạng thái
                        val updatedOrder = order.copy(pay_sta = "waiting")
                        orderDao.update(updatedOrder)
                    }
                }
            }
        }
    }

    fun serve(order: Order) {
        viewModelScope.launch(Dispatchers.IO) {
            // Tạo một bản sao của đơn hàng và cập nhật trạng thái
            val updatedOrder = order.copy(order_status = "served")
            orderDao.update(updatedOrder)
        }
    }

    //Cập nhật giá trị của LiveData _selectedTableNumber khi người dùng chọn một số bàn cụ thể.
    fun setSelectedTableNumber(tableNumber: Int) {
        _selectedTableNumber.value = tableNumber
    }

    fun setSelectedId(orderId: String) {
        _selectedId.value = orderId
    }

    /**
     * Xóa một đơn hàng khỏi cơ sở dữ liệu và cập nhật tổng số lượng và tổng giá trị.
     */
    fun deleteOrderAndUpdateTotals(order: Order) {
        viewModelScope.launch {
            orderDao.delete(order)
        }
    }
    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            val tableNumber = _selectedTableNumber.value
            if (tableNumber != null) {
//                orderDao.deleteOrdersByTable(tableNumber)
                orderDao.deleteOrdersByTable1(tableNumber)
            }
        }
    }


    /**
     * Thêm một đơn hàng mới vào cơ sở dữ liệu hoặc cập nhật số lượng nếu đơn hàng đã tồn tại.
     */
    fun addNewOrder(
        itemId: Int,
        table: Int,
        name: String,
        time: String,
        quantity: Int,
        price: Int,
        order_status: String,
        pay_sta: String
    ) {

        viewModelScope.launch {
            // Lấy giá trị của tableNumber hoặc 0 nếu chưa được đặt
            val tableNumber = selectedTableNumber.value ?: 0

            // Tạo đơn hàng mới với tableNumber đã được đặt
            val newOrder = getNewOrderEntry(
                itemId,
                tableNumber,
                name,
                time,
                quantity,
                price,
                order_status,
                pay_sta
            )
            orderDao.insert(newOrder)

        }
    }

    // Hàm tạo đơn hàng mới
    private fun getNewOrderEntry(
        itemId: Int,
        table: Int,
        name: String,
        time: String,
        quantity: Int,
        price: Int,
        order_status: String,
        pay_sta: String
    ): Order {
        // Lấy giá trị của tableNumber hoặc 0 nếu chưa được đặt
        val tableNumber = selectedTableNumber.value ?: 0

        // Trả về đối tượng Order mới với tableNumber đã được đặt
        return Order(
            itemId = itemId,
            tableNumber = tableNumber,
            name = name,
            time = time,
            quantity = quantity,
            price = price,
            order_status = order_status,
            pay_sta = pay_sta
        )
    }


}

/**
 * Lớp Factory để khởi tạo đối tượng [OrderViewModel].
 */
class OrderViewModelFactory(private val orderDao: OrderDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OrderViewModel(orderDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
