package com.example.pos1.schedule

import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.pos1.dao.RosterDao
import com.example.pos1.entity.Roster
import kotlinx.coroutines.launch

class RosterViewModel(private val rosterDao: RosterDao) : ViewModel() {
    val allRosters: LiveData<List<Roster>> = rosterDao.getAll().asLiveData()

    private fun insertRoster(roster: Roster) {
        viewModelScope.launch {
            rosterDao.insert(roster)
        }
    }

    fun rosterExist(startTime: String,finishTime: String,onResult:(Boolean)->Unit){
        viewModelScope.launch {
            val count=rosterDao.rosterByTime(startTime,finishTime)
            onResult(count>0)
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

    // kiểm tra xem thông tin đầu vào (số bàn và sức chứa) có hợp lệ không.
    fun isEntryValid(startTime: EditText,finishTime: EditText): Boolean {
            var isValid = true
            if (startTime.text.isBlank() || startTime.text.toString().toInt() > 24) {
                startTime.error = "Start time must be between 0 and 24."
                isValid = false
            }

            if (finishTime.text.isBlank() || finishTime.text.toString().toInt() > 24) {
                finishTime.error = "Finish time must be between 0 and 24"
                isValid = false
            }
            if (finishTime == startTime) {
                startTime.error = "Start and finish time must be different"
                isValid = false
            }
            return isValid
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
