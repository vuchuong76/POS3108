package com.example.pos1.editmenu

import android.content.Context
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.pos1.dao.ItemDao
import com.example.pos1.entity.Item
import com.example.pos1.entity.Order
import kotlinx.coroutines.launch

class ItemViewModel(private val itemDao: ItemDao) : ViewModel() {
    //Thuộc tính allTables là một LiveData kiểu danh sách các Table,
// được lấy từ phương thức getTables() của tableDao.
// Nó sẽ cung cấp danh sách các bảng Table cho các thành phần giao diện người dùng theo thời gian thực.
    val allItems: LiveData<List<Item>> = itemDao.getItems().asLiveData()

    private fun insertItem(item: Item) {
        viewModelScope.launch {
            itemDao.insert(item)
        }
    }

    fun sellItem(item: Item) {
        if (item.stock > 0) {
            // Decrease the quantity by 1
            val newItem = item.copy(stock = item.stock - 1)
            updateItem(newItem)
        }
    }

    fun updateItem(
        id: Int,
        name: String,
        type: String,
        stock: String,
        price: String,
        image: String
    ) {
        val updatedItem = getUpdatedItemEntry(id, name, type, stock, price, image)
        updateItem(updatedItem)
    }

    fun updateItem(item: Item) {
        viewModelScope.launch {
            itemDao.update(item)
        }
    }
    fun isStockAvailable(item: Item): Boolean {
        return (item.stock > 0)
    }
    //được sử dụng để xóa một bảng Table từ cơ sở dữ liệu.
    fun deleteItem(item: Item) {
        viewModelScope.launch {
            itemDao.delete(item)
        }
    }

    //được sử dụng để thêm một bảng Table mới vào cơ sở dữ liệu.
    fun addNewItem(name: String, type: String, stock: String, price: String, image: String) {
        val newItem = getNewItemEntry(name, type, stock, price, image)
        insertItem(newItem)
    }

    private fun getNewItemEntry(
        name: String,
        type: String,
        stock: String,
        price: String,
        image: String
    ): Item {
        return Item(
            name = name,
            type = type,
            stock = stock.toInt(),
            price = price.toDouble(),
            image = image
        )
    }

    //Phương thức retrieveTable(id: Int) lấy thông tin của một bảng Item dựa trên ID.
    fun retrieveItem(id: Int): LiveData<Item> {
        return itemDao.getItem(id).asLiveData()
    }

    // kiểm tra xem thông tin đầu vào (số bàn và sức chứa) có hợp lệ không.
    fun isEntryValid(
        name: EditText,
        type: String,
        stock: EditText,
        price: EditText,
        image: String,
        context: Context
    ): Boolean {
        var isValid = true

        if (name.text.isBlank() || name.text.length < 3) {
            name.error = "Invalid Name"
            isValid = false
        }

        if (stock.text.isBlank()) {
            stock.error = "Invalid Stock"
            isValid = false
        }
        if (price.text.isBlank()) {
            price.error = "Invalid Price"
            isValid = false
        }
        if (image.isBlank()) {
            Toast.makeText(context, "Invalid Image URL", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }

    private fun getUpdatedItemEntry(
        id: Int,
        name: String,
        type: String,
        stock: String,
        price: String,
        image: String
    ): Item {
        return Item(id, name, type, stock.toInt(), price.toDouble(), image)
    }
}

class ItemViewModelFactory(private val itemDao: ItemDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ItemViewModel(itemDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
