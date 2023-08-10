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
import kotlinx.coroutines.launch


class CouponViewModel(private val couponDao: CouponDao) : ViewModel() {
    //Thuộc tính allTables là một LiveData kiểu danh sách các Table,
// được lấy từ phương thức getTables() của tableDao.
// Nó sẽ cung cấp danh sách các bảng Table cho các thành phần giao diện người dùng theo thời gian thực.
    val allCoupons: LiveData<List<Coupon>> = couponDao.getAll().asLiveData()

    private fun insertCoupon(coupon: Coupon) {
        viewModelScope.launch {
            couponDao.insert(coupon)
        }
    }

//    fun updateCoupon(code: String, coupon: Int) {
//        val updatedCoupon = getUpdatedCouponEntry(code, coupon)
//        updateCoupon(updatedCoupon)
//    }

    private fun updateCoupon(coupon: Coupon) {
        viewModelScope.launch {
            couponDao.update(coupon)
        }
    }

    //được sử dụng để xóa một bảng Table từ cơ sở dữ liệu.
    fun deleteCoupon(coupon: Coupon) {
        viewModelScope.launch {
            couponDao.delete(coupon)
        }
    }

    //được sử dụng để thêm một bảng Table mới vào cơ sở dữ liệu.
    fun addNewCoupon(startTime: String, finishTime: String) {
        val newCoupon = getNewCouponEntry(startTime, finishTime)
        insertCoupon(newCoupon)
    }

    private fun getNewCouponEntry(code: String, discount: String): Coupon {
        return Coupon(
            code = code,
            discount = discount.toInt()
        )
    }

    //Phương thức retrieveTable(id: Int) lấy thông tin của một bảng Table dựa trên ID.
    fun retrieveCoupon(id: Int): LiveData<Coupon> {
        return couponDao.getCoupon(id).asLiveData()
    }

    // kiểm tra xem thông tin đầu vào (số bàn và sức chứa) có hợp lệ không.
//    fun isEntryValid(code: String, coupon: String): Boolean {
//        if (code.isBlank() || coupon.isBlank() || code.length < 4 ||code.length > 8 || coupon.toInt() == 0) {
//            return false
//        }
//        return true
//    }
    fun isEntryValid(code: EditText, coupon: EditText): Boolean {
        var isValid = true
        if (code.text.isBlank() || code.text.length < 4 || code.text.length > 8) {
            code.error = "Invalid Code"
            isValid = false
        }
        if (coupon.text.isBlank() || coupon.text.toString().toInt()== 0) {
            coupon.error = "Invalid Coupon"
            isValid = false
        }
        return isValid
    }

//        if (staffIdEditText.text.isBlank() || staffIdEditText.text.length < 3) {
//            staffIdEditText.error = "Invalid Staff ID"
//            isValid = false
//        }
//        return isValid
//    }
//
//    private fun getUpdatedCouponEntry(code: String, coupon: Int): Coupon {
//        return Coupon(code, coupon.toInt())
//    }
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