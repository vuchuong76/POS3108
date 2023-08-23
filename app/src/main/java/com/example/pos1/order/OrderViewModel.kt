package com.example.pos1.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.pos1.dao.ItemDao
import com.example.pos1.dao.OrderDao
import com.example.pos1.dao.TableDao
import com.example.pos1.entity.DishQuantity
import com.example.pos1.entity.Item
import com.example.pos1.entity.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class OrderViewModel(
    private val orderDao: OrderDao,
    private val itemDao: ItemDao,
    private val tableDao: TableDao
) : ViewModel() {
    //    lấy 3 order nhiều doanh thu nhất
    val topDishes: LiveData<List<DishQuantity>> = orderDao.getTopDishes().asLiveData()



    private val _amount: MutableLiveData<Int> = MutableLiveData(0)
    val amount: LiveData<Int> = _amount

    private val _amount1: MutableLiveData<Double> = MutableLiveData(0.0)

    var userName: String = ""


    // LiveData foodsForTable sẽ tự động cập nhật khi giá trị của _tableNumber thay đổi
    private val _selectedTableNumber = MutableLiveData<Int>()
    val selectedTableNumber: LiveData<Int> = _selectedTableNumber
    private val _selectedId = MutableLiveData<String>()
    val selectedId: LiveData<String> = _selectedId

    private val _lastAmount = MutableLiveData<Double>()
    val lastAmount: LiveData<Double> get() = _lastAmount

 private val _lastAmount1 = MutableLiveData<Double>()
    val lastAmount1: LiveData<Double> = _lastAmount1


    fun updateData(newData: Double) {
        _lastAmount.value = newData
    }

    // chứa danh sách đơn hàng cho tableNumber và trạng thái thanh toán pay_sta= waiting.
    val orderForTable: LiveData<List<Order>> = _selectedTableNumber.switchMap { tableNumber ->
        orderDao.getOrderForTableAndStatus(tableNumber).asLiveData()
    }
    //danh sách order trong bàn
    val orderForTable1: LiveData<List<Order>> = _selectedTableNumber.switchMap { tableNumber ->
        orderDao.getOrderForSelectedTable(tableNumber).asLiveData()
    }


    //    danh sách order đang ở trạng thái tính tiền paying
    val orderForPay: LiveData<List<Order>> = _selectedTableNumber.switchMap { tableNumber ->
        orderDao.getOrderForPay(tableNumber).asLiveData()
    }

    val orderById: LiveData<List<Order>> = _selectedId.switchMap { orderId ->
        orderDao.getOrderByOrId(orderId).asLiveData()
    }


    fun updateTableStatus(tableNumber: Int, status: Int) {
    viewModelScope.launch {
        tableDao.updateTableStatus(tableNumber, status)
    }
}
    //danh sách order đi vào bếp
    fun getOrderForKitchen(): LiveData<List<Order>> {
        val flow: Flow<List<Order>> = orderDao.getOrderForKitchen()
        return flow.asLiveData()
    }
    fun setAmountAllItems(value: Double) {
        _amount1.value = value
    }

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

    private val _receive = MutableLiveData<Double>()
    val receive: LiveData<Double> = _receive
  private val _discount = MutableLiveData<Double>()
    val discount: LiveData<Double> = _discount

    private val _change = MutableLiveData<Double>()
    val change: LiveData<Double> = _change

    // Các hàm cài đặt giá trị
    fun setLastAmount(value: Double) {
        _lastAmount1.value = value
    }
    fun setDiscount(value: Double) {
        _discount.value = value
    }

    fun setReceive(value: Double) {
        _receive.value = value
    }

    fun setChange(value: Double) {
        _change.value = value
    }


    fun backItem(item: Item, order: Order) {
        viewModelScope.launch {
            val newItem = item.copy(stock = item.stock + order.quantity)

            itemDao.update(newItem)

        }
    }
    fun delete(order: Order) {
        viewModelScope.launch {
            orderDao.delete(order)
        }
    }
    fun insert(order: Order) {
        viewModelScope.launch {
            orderDao.insert(order)
        }
    }
    /**
     * Xóa một đơn hàng khỏi cơ sở dữ liệu và cập nhật tổng số lượng và tổng giá trị.
     */
    fun deleteOrderAndUpdateTotals(order: Order) {
        viewModelScope.launch {
            val itemId: Int = order.itemId
            val item =
                itemDao.getItemById(itemId)  // giả sử bạn có một phương thức như vậy trong itemDao
            if (item != null) {
                backItem(item, order)
            }
            orderDao.delete(order)
        }
    }

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            val tableNumber = _selectedTableNumber.value
            if (tableNumber != null) {
                val orders = orderDao.getOrderforback(tableNumber).firstOrNull() ?: emptyList()
                // Bản đồ để thu thập tất cả các thay đổi cần thiết
                val stockChanges = mutableMapOf<Int, Int>()

                for (order in orders) {

                    val itemId = order.itemId
                    val change = stockChanges.getOrDefault(itemId, 0) + order.quantity
                    stockChanges[itemId] = change

                    orderDao.delete(order)
                }

                // Áp dụng tất cả các thay đổi
                for ((itemId, change) in stockChanges) {
                    val item = itemDao.getItemById(itemId)
                    if (item != null) {
                        val newItem = item.copy(stock = item.stock + change)
                        itemDao.update(newItem)
                    }
                }
            }
        }
    }


    /**
     * Thêm một đơn hàng mới vào cơ sở dữ liệu hoặc cập nhật số lượng nếu đơn hàng đã tồn tại.
     */

    suspend fun addNewOrder(
        itemId: Int,
        table: Int,
        name: String,
        time: String,
        quantity: Int,
        price: Double,
        order_status: String,
        pay_sta: String
    ) {
        val tableNumber = selectedTableNumber.value ?: 0

        // Tìm kiếm order đã tồn tại dựa trên các thông tin cung cấp (itemId, tableNumber, etc.)
        val existingOrder = orderDao.findExistingOrder(itemId, tableNumber, name, price, order_status, pay_sta)

        if (existingOrder != null) {
            // Cập nhật quantity cho order đã tồn tại
            existingOrder.quantity += quantity
            orderDao.update(existingOrder)
        } else {
            // Thêm một order mới nếu không tìm thấy order đã tồn tại
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
        price: Double,
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
class OrderViewModelFactory(private val orderDao: OrderDao, private val itemDao: ItemDao, private val tableDao: TableDao) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OrderViewModel(orderDao, itemDao,tableDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
