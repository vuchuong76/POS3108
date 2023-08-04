package com.example.pos1.schedule

import androidx.lifecycle.ViewModel
import com.example.pos1.table.TableViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.pos1.dao.RosterDao
import com.example.pos1.dao.TableDao
import com.example.pos1.entity.Roster
import com.example.pos1.entity.Table
import kotlinx.coroutines.launch

class RosterViewModel(private val rosterDao: RosterDao) : ViewModel() {
    //Thuộc tính allTables là một LiveData kiểu danh sách các Table,
// được lấy từ phương thức getTables() của tableDao.
// Nó sẽ cung cấp danh sách các bảng Table cho các thành phần giao diện người dùng theo thời gian thực.
    val allRosters: LiveData<List<Roster>> = rosterDao.getAll().asLiveData()

    private fun insertRoster(roster: Roster) {
        viewModelScope.launch {
            rosterDao.insert(roster)
        }
    }
    fun updateRoster(startTime: Int, finishTime: Int) {
        val updatedRoster = getUpdatedRosterEntry(startTime, finishTime)
        updateRoster(updatedRoster)
    }

    private fun updateRoster(roster: Roster) {
        viewModelScope.launch {
            rosterDao.update(roster)
        }
    }
    //được sử dụng để xóa một bảng Table từ cơ sở dữ liệu.
    fun deleteRoster(roster: Roster) {
        viewModelScope.launch {
            rosterDao.delete(roster)
        }
    }
    //được sử dụng để thêm một bảng Table mới vào cơ sở dữ liệu.
    fun addNewRoster(startTime: String,finishTime: String) {
        val newRoster = getNewRosterEntry(startTime,finishTime)
        insertRoster(newRoster)
    }

    private fun getNewRosterEntry(startTime: String,finishTime: String): Roster {
        return Roster(start_time = startTime.toInt(),
            finish_time = finishTime.toInt()
        )
    }
    //Phương thức retrieveTable(id: Int) lấy thông tin của một bảng Table dựa trên ID.
    fun retrieveRoster(id: Int): LiveData<Roster> {
        return rosterDao.getRosterById(id).asLiveData()
    }
    // kiểm tra xem thông tin đầu vào (số bàn và sức chứa) có hợp lệ không.
    fun isEntryValid(startTime: String,finishTime: String): Boolean {
        if( startTime.isBlank()|| finishTime.isBlank()|| startTime.toInt()>24|| finishTime.toInt()>24||finishTime==startTime)
        {return false}
        return true
    }

    private fun getUpdatedRosterEntry(startTime: Int, finishTime: Int): Roster {
        return Roster(startTime.toInt(), finishTime.toInt())
    }
}

class RosterViewModelFactory(private val rosterDao: RosterDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RosterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RosterViewModel(rosterDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
