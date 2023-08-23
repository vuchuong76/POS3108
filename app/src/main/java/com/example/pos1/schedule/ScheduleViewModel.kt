
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.pos1.dao.ScheduleDao
import com.example.pos1.entity.Schedule
import kotlinx.coroutines.launch
import androidx.lifecycle.switchMap

class ScheduleViewModel(private val scheduleDao: ScheduleDao) : ViewModel() {

    val allSchedules: LiveData<List<Schedule>> = scheduleDao.getAll().asLiveData()


    //chọn ngày để lọc schedule
    private val _selectedDate = MutableLiveData<String?>(null)
    fun selectDate(date: String?) {
        _selectedDate.value = date
    }

    val schedules: LiveData<List<Schedule>> = _selectedDate.switchMap { date ->
        if (date == null) {
            // Nếu không có ngày nào được chọn, trả về tất cả các lịch trình
            allSchedules
        } else {
            // Nếu có ngày được chọn, trả về các lịch trình cho ngày đó
            getScheduleByDate(date)
        }
    }

    fun scheduleExist(employee: String,date: String,shift: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val count = scheduleDao.countSchedule(employee,date,shift)
            onResult(count > 0)
        }
    }

    fun insertSchedule(schedule: Schedule) {
        viewModelScope.launch {
            scheduleDao.insert(schedule)
        }
    }


    fun deleteSchedule(schedule: Schedule) {
        viewModelScope.launch {
            scheduleDao.delete(schedule)
        }
    }

    // Cập nhật giá trị của selectedEmployee

    // Thêm tham số employee vào phương thức addNewSchedule()
    fun addNewSchedule(date: String, shift: String, employee: String) {
        val newSchedule = Schedule(date = date, shift = shift, employee = employee)
        insertSchedule(newSchedule)
    }

    fun getScheduleByDate(date: String): LiveData<List<Schedule>> {
        return scheduleDao.getAllByDate(date).asLiveData()
    }



    val errorMessage = MutableLiveData<String?>()

    fun isEntryValid(date: String, shift: String, employee: String): Boolean {
        if (date.isBlank()) {
            errorMessage.value = "The date cannot be empty!"
            return false
        }
        if (shift.isBlank()) {
            errorMessage.value = "The shift cannot be empty!"
            return false
        }
        if (employee.isBlank()) {
            errorMessage.value = "Staff name cannot be empty!"
            return false
        }
        errorMessage.value = null // Nếu tất cả các trường đều hợp lệ, đặt errorMessage về null
        return true
    }


}

class ScheduleViewModelFactory(private val scheduleDao: ScheduleDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScheduleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ScheduleViewModel(scheduleDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
