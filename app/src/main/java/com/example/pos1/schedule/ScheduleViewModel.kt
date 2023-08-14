import android.util.Log
import android.widget.Toast
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
import com.example.pos1.entity.Order

class ScheduleViewModel(private val scheduleDao: ScheduleDao) : ViewModel() {

    val allSchedules: LiveData<List<Schedule>> = scheduleDao.getAll().asLiveData()

    // Để lưu trữ giá trị của selectedEmployee
    private val _selectedEmployee = MutableLiveData<String?>()
    val selectedEmployee: LiveData<String?> get() = _selectedEmployee

    //chọn ngày để lọc schedule
    private val _selectedDate = MutableLiveData<String?>(null)
    val selectedDate: LiveData<String?> get()=_selectedDate

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
//    val orderForTable: LiveData<List<Order>> = _selectedTableNumber.switchMap { tableNumber ->
//        orderDao.getOrderForTableAndStatus(tableNumber).asLiveData()
//    }
//    val schedules: LiveData<List<Schedule>> = Transformations.switchMap(selectedDate) { date ->
//        if (date == null) {
//            // Nếu không có ngày nào được chọn, trả về tất cả các lịch trình
//            allSchedules
//        } else {
//            // Nếu có ngày được chọn, trả về các lịch trình cho ngày đó
//            getScheduleByDate(date)
//        }
//    }


    fun insertSchedule(schedule: Schedule) {
        viewModelScope.launch {
            scheduleDao.insert(schedule)
        }
    }

    fun updateSchedule(schedule: Schedule) {
        viewModelScope.launch {
            scheduleDao.update(schedule)
        }
    }

    fun deleteSchedule(schedule: Schedule) {
        viewModelScope.launch {
            scheduleDao.delete(schedule)
        }
    }

    // Cập nhật giá trị của selectedEmployee
    fun setSelectedEmployee(employee: String?) {
        _selectedEmployee.value = employee
    }

    // Thêm tham số employee vào phương thức addNewSchedule()
    fun addNewSchedule(date: String, shift: String, employee: String) {
        val newSchedule = Schedule(date = date, shift = shift, employee = employee)
        insertSchedule(newSchedule)
    }
    fun getScheduleById(id: Long): LiveData<Schedule> {
        return scheduleDao.getScheduleById(id.toInt()).asLiveData()
    }

    fun getScheduleByDate(date: String): LiveData<List<Schedule>> {
        return scheduleDao.getAllByDate(date).asLiveData()
    }
    fun getScheduleByName(name: String): LiveData<List<Schedule>> {
        return scheduleDao.getAllByName(name).asLiveData()
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
