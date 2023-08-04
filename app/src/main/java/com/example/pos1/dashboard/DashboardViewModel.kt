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

class DashboardViewModel(private val orderlistDao: OrderlistDao) : ViewModel() {
    val allItems1: LiveData<List<Orderlist>> = orderlistDao.getAll().asLiveData()
}
class DashboardViewModelFactory(private val orderlistDao: OrderlistDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(orderlistDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}