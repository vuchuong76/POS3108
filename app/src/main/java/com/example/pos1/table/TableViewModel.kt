package com.example.pos1.table

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.pos1.dao.TableDao
import com.example.pos1.entity.Order
import com.example.pos1.entity.Table
import kotlinx.coroutines.launch

class TableViewModel(private val tableDao: TableDao) : ViewModel() {
//Thuộc tính allTables là một LiveData kiểu danh sách các Table,
// được lấy từ phương thức getTables() của tableDao.
// Nó sẽ cung cấp danh sách các bảng Table cho các thành phần giao diện người dùng theo thời gian thực.
    val allTables: LiveData<List<Table>> = tableDao.getTables().asLiveData()

    private val _duplicateUserEvent = MutableLiveData<Unit>()
    val duplicateUserEvent: LiveData<Unit> get() = _duplicateUserEvent

    private fun insertTable(table: Table) {
        viewModelScope.launch {
            try {
                tableDao.insert(table)
            } catch (e: SQLiteConstraintException) {
                _duplicateUserEvent.value = Unit
            }
        }
    }
//    fun addTable(number: Int, capacity: Int) {
//        val table = Table(number = number, capacity = capacity)
//
//        viewModelScope.launch {
//            tableDao.insertTableAndOrder(table, orderDao)
//        }
//    }
fun tableNumberExists(number: Int, onResult: (Boolean) -> Unit) {
    viewModelScope.launch {
        val count = tableDao.countTableWithNumber(number)
        onResult(count > 0)
    }
}
    fun updateTable(tableNumber: String, capacity: String) {
        val updatedTable = getUpdatedTableEntry(tableNumber, capacity)
        updateTable(updatedTable)
    }
    fun updateTableStatusIfNotEmpty(tableNumber: Int, orders: List<Order>) {
        viewModelScope.launch {
            if (orders.isNotEmpty()) {
                tableDao.updateTableStatus(tableNumber, 1)
            }
            else{
                tableDao.updateTableStatus(tableNumber, 0)

            }
        }
    }

    private fun updateTable(table: Table) {
        viewModelScope.launch {
            tableDao.update(table)
        }
    }
//được sử dụng để xóa một bảng Table từ cơ sở dữ liệu.
    fun deleteTable(table: Table) {
        viewModelScope.launch {
            tableDao.delete(table)
        }
    }
//được sử dụng để thêm một bảng Table mới vào cơ sở dữ liệu.
    fun addNewTable(tableNumber: String,capacity: String) {
        val newTable = getNewTableEntry(tableNumber,capacity)
        insertTable(newTable)
    }

    private fun getNewTableEntry(tableNumber: String,capacity:String): Table {
        return Table(tableNumber.toInt(),
        capacity.toInt()
            )
    }
    //Phương thức retrieveTable(id: Int) lấy thông tin của một bảng Table dựa trên ID.
    fun retrieveTable(id: Int): LiveData<Table> {
        return tableDao.getTableByNumber(id).asLiveData()
    }
// kiểm tra xem thông tin đầu vào (số bàn và sức chứa) có hợp lệ không.
    fun isEntryValid(tableNumber: String,capacity:String): Boolean {
        if( tableNumber.isBlank()|| capacity.isBlank()||tableNumber== "0"||capacity=="0")
        {return false}
    return true
    }

    private fun getUpdatedTableEntry(tableNumber: String, capacity: String): Table {
        return Table(tableNumber.toInt(), capacity.toInt())
    }
}
class TableViewModelFactory(private val tableDao: TableDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TableViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TableViewModel(tableDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
