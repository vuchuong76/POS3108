package com.example.pos1.User

import android.database.sqlite.SQLiteConstraintException
import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.pos1.dao.UserDao
import com.example.pos1.entity.User
import kotlinx.coroutines.launch

class UserViewModel(private val userDao: UserDao): ViewModel() {

    val allItems: LiveData<List<User>> = userDao.getAll().asLiveData()
    fun getAllUserNames(): LiveData<List<String>> {
        return userDao.getAllUserNames()
    }
//    private fun insertUser(user: User) {
//        viewModelScope.launch {
//            userDao.insert(user)
//        }
//    }
private val _duplicateUserEvent = MutableLiveData<Unit>()
    val duplicateUserEvent: LiveData<Unit> get() = _duplicateUserEvent

    private fun insertUser(user: User) {
        viewModelScope.launch {
            try {
                userDao.insert(user)
            } catch (e: SQLiteConstraintException) {
                _duplicateUserEvent.value = Unit
            }
        }
    }

    fun staffIdExists(staffId: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val count = userDao.countStaffWithId(staffId)
            onResult(count > 0)
        }
    }



    private fun updateUser(user: User) {
        viewModelScope.launch {
            userDao.update(user)
        }
    }
    fun deleteUser(user: User) {
        viewModelScope.launch {
            userDao.delete(user)
        }
    }
    fun addNewUser(
        staffId: Int,
        password: String,
        staffname: String,
        age: Int,
        position: String,
        tel: String,
        address: String
    ) {
        val newUser = getNewUserEntry(staffId, password, staffname, age, position, tel, address)
        insertUser(newUser)
    }
    private fun getNewUserEntry(
        staffId: Int,
        password: String,
        staffname: String,
        age: Int,
        position: String,
        tel: String,
        address: String
    ): User {
        return User(
            staffId = staffId.toInt(),
            password = password,
            staffname = staffname,
            age = age.toInt(),
            position = position,
            tel = tel,
            address = address
        )
    }
    fun updateUser(
        staffId: Int,
        password: String,
        staffname: String,
        age: String,
        position:String,
        tel: String,
        address: String
    ) {
        val updatedItem = getUpdatedItemEntry(staffId, password, staffname, age,position,tel,address)
        updateUser(updatedItem)
    }
    //nhận dạng giá trị trả về trong userdetail
    fun retrieveItem(id: Int): LiveData<User> {
        return userDao.getByStaffId(id).asLiveData()
    }

    fun isEntryValid(
        staffIdEditText: EditText,
        passwordEditText: EditText,
        staffnameEditText: EditText,
        ageEditText: EditText,
        positionEditText: String,
        telEditText: EditText,
        addressEditText: EditText
    ): Boolean {
        var isValid = true

        if (staffIdEditText.text.isBlank() || staffIdEditText.text.length < 3) {
            staffIdEditText.error = "Invalid Staff ID"
            isValid = false
        }

        if (passwordEditText.text.isBlank() || passwordEditText.text.length < 3) {
            passwordEditText.error = "Invalid Password"
            isValid = false
        }

        if (staffnameEditText.text.isBlank() || staffnameEditText.text.length < 2) {
            staffnameEditText.error = "Invalid Staff Name"
            isValid = false
        }

        if (ageEditText.text.isBlank() || ageEditText.text.length < 2) {
            ageEditText.error = "Invalid Age"
            isValid = false
        }

        if (telEditText.text.isBlank() || telEditText.text.length < 8) {
            telEditText.error = "Invalid Telephone Number"
            isValid = false
        }

        if (addressEditText.text.isBlank() || addressEditText.text.length < 4) {
            addressEditText.error = "Invalid Address"
            isValid = false
        }

        return isValid
    }

    private fun getUpdatedItemEntry(
        staffId: Int,
        password: String,
        staffname: String,
        age: String,
        position: String,
        tel: String,
        address: String
    ): User {
        return User(
            staffId = staffId,
            password = password,
            staffname = staffname,
            age = age.toInt(),
            position = position,
            tel = tel,
            address = address
        )
    }

    class UserViewModelFactory(
        private val userDao: UserDao
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return UserViewModel(userDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}