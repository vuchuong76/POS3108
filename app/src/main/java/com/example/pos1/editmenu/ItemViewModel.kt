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
import kotlinx.coroutines.launch

class ItemViewModel(private val itemDao: ItemDao) : ViewModel() {
    val allItems: LiveData<List<Item>> = itemDao.getItems().asLiveData()
//    private val updateMutex = Mutex()
    private fun insertItem(item: Item) {
        viewModelScope.launch {
            itemDao.insert(item)
        }
    }
//    var sellItemCount = 0
//    var updateItemCount = 0

suspend fun sellItem(item: Item) {
//    sellItemCount++
    if (item.stock > 0) {
        val newItem = item.copy(stock = item.stock - 1)
        updateItem(newItem)
    }
}


    private suspend fun updateItem(item: Item) {
//        updateItemCount++
        itemDao.update(item)
    }
    fun updateItem(
        id: Int,
        name: String,
        type: String,
        stock: String,
        price: String,
        image: String
    ) {
        viewModelScope.launch()
        {
            val updatedItem = getUpdatedItemEntry(id, name, type, stock, price, image)
            updateItem(updatedItem)
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

    //update Stock
    fun addStock(item: Item, quantity: Int) {
        viewModelScope.launch {
            val newItem = item.copy(stock = item.stock + quantity)
            itemDao.update(newItem)
        }
    }
    fun minusStock(item: Item, quantity: Int) {
        viewModelScope.launch {
            val newItem = item.copy(stock = item.stock - quantity)
            itemDao.update(newItem)
        }
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
            name.error = "Item name must be longer than 2 characters."
            isValid = false
        }

        if (stock.text.isBlank()) {
            stock.error = "Stock can not be Empty"
            isValid = false
        }
        if (price.text.isBlank()) {
            price.error = "Price can not be Empty"
            isValid = false
        }
        if (image.isBlank()) {
            Toast.makeText(context, "The image cannot be Empty", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }

    fun itemNameExist(name: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val count = itemDao.countItemWithName(name)
            onResult(count > 0)
        }
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
