package com.example.pos1.coupon

import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.pos1.dao.CouponDao
import com.example.pos1.dao.RosterDao
import com.example.pos1.entity.Coupon
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class CouponViewModel(private val couponDao: CouponDao) : ViewModel() {
    //Thuộc tính allTables là một LiveData kiểu danh sách các Table,
// được lấy từ phương thức getTables() của tableDao.
// Nó sẽ cung cấp danh sách các bảng Table cho các thành phần giao diện người dùng theo thời gian thực.
    val allCoupons: LiveData<List<Coupon>> = couponDao.getAll().asLiveData()
    sealed class CouponState {
        object Loading : CouponState()
        data class Success(val coupon: Coupon) : CouponState()
        object Invalid : CouponState()
    }

    private val _couponFlow = MutableSharedFlow<CouponState>()
    val couponFlow: SharedFlow<CouponState> get() = _couponFlow

    fun fetchCouponByCode(code: String) = viewModelScope.launch {
        couponDao.getCouponByCode(code).collect { coupon ->
            _couponFlow.emit(if (coupon != null) CouponState.Success(coupon) else CouponState.Invalid)
        }
    }



    private fun insertCoupon(coupon: Coupon) {
        viewModelScope.launch {
            couponDao.insert(coupon)
        }
    }

    //được sử dụng để xóa một bảng Table từ cơ sở dữ liệu.
    fun deleteCoupon(coupon: Coupon) {
        viewModelScope.launch {
            couponDao.delete(coupon)
        }
    }

    //được sử dụng để thêm một bảng Table mới vào cơ sở dữ liệu.
    fun addNewCoupon(code: String, discount: Double) {
        val newCoupon = getNewCouponEntry(code, discount)
        insertCoupon(newCoupon)
    }

    private fun getNewCouponEntry(code: String, discount: Double): Coupon {
        return Coupon(
            code = code,
            discount = discount
        )
    }

    //Phương thức retrieveTable(id: Int) lấy thông tin của một bảng Table dựa trên ID.
    fun retrieveCoupon(id: Int): LiveData<Coupon> {
        return couponDao.getCoupon(id).asLiveData()
    }


    fun isEntryValid(code: EditText, coupon: EditText): Boolean {
        var isValid = true
        if (code.text.isBlank() || code.text.length < 4 || code.text.length > 8) {
            code.error = "The length of the code must be between 4 and 8 characters."
            isValid = false
        }
        if (coupon.text.isBlank() || coupon.text.toString().toInt()<= 0||coupon.text.toString().toInt()>=100) {
            coupon.error = "The value of the coupon must be between 0 and 100."
            isValid = false
        }
        return isValid
    }
    fun codeExist(code: String,onResult:(Boolean)->Unit){
        viewModelScope.launch {
            val count= couponDao.countCouponByCode(code)
            onResult(count>0)
        }
    }

}

class CouponViewModelFactory(private val couponDao: CouponDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CouponViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CouponViewModel(couponDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}