package com.example.pos1.User

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.widget.EditText
import android.widget.Toast
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
    var userName: String = ""
    var staffName: String = ""
    val allItems: LiveData<List<User>> = userDao.getAll().asLiveData()
    fun getAllUserNames(): LiveData<List<String>> {
        return userDao.getAllUserNames()
    }
private val _duplicateUserEvent = MutableLiveData<Unit>()
//    val duplicateUserEvent: LiveData<Unit> get() = _duplicateUserEvent

    private fun insertUser(user: User) {
        viewModelScope.launch {
            try {
                userDao.insert(user)
            } catch (e: SQLiteConstraintException) {
                _duplicateUserEvent.value = Unit
            }
        }
    }
    fun userNameExists(userName: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val count = userDao.countStaffWithId(userName)
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
        userName: String,
        password: String,
        staffname: String,
        age: Int,
        position: String,
        tel: String,
        address: String
    ) {
        val newUser = getNewUserEntry(userName, password, staffname, age, position, tel, address)
        insertUser(newUser)
    }
    private fun getNewUserEntry(
        userName: String,
        password: String,
        staffname: String,
        age: Int,
        position: String,
        tel: String,
        address: String
    ): User {
        return User(
            userName = userName,
            password = password,
            staffname = staffname,
            age = age,
            position = position,
            tel = tel,
            address = address
        )
    }
    fun updateUser(
        userName: String,
        password: String,
        staffname: String,
        age: String,
        position:String,
        tel: String,
        address: String
    ) {
        val updatedItem = getUpdatedItemEntry(userName, password, staffname, age,position,tel,address)
        updateUser(updatedItem)
    }
    //nhận dạng giá trị trả về trong userdetail
    fun retrieveItem(id: String): LiveData<User> {
        return userDao.getByStaffId(id).asLiveData()
    }

    fun isEntryValid(
        userNameEditText: EditText,
        passwordEditText: EditText,
        staffnameEditText: EditText,
        ageEditText: EditText,
        positionEditText: String,
        telEditText: EditText,
        addressEditText: EditText
    ): Boolean {
        var isValid = true

        if (userNameEditText.text.isBlank() || userNameEditText.text.length < 3) {
            userNameEditText.error = "Username must be longer than 3 characters\n"
            isValid = false
        }

        if (passwordEditText.text.isBlank() || passwordEditText.text.length < 3) {
            passwordEditText.error = "Password must be longer than 3 characters"
            isValid = false
        }

        if (staffnameEditText.text.isBlank() || staffnameEditText.text.length < 2) {
            staffnameEditText.error = "Staff Name must be longer than 2 characters"
            isValid = false
        }

        if (ageEditText.text.isBlank() || ageEditText.text.toString().toInt() <16||ageEditText.text.toString().toInt()>150) {
            ageEditText.error = "The age must between 16 and 150"
            isValid = false
        }

        if (telEditText.text.isBlank() || telEditText.text.length < 8) {
            telEditText.error = "Telephone Number must be longer than 8 characters"
            isValid = false
        }

        if (addressEditText.text.isBlank() || addressEditText.text.length < 2) {
            addressEditText.error = "Address must be longer than 2 characters"
            isValid = false
        }

        return isValid
    }

    fun blank(
        userNameEditText: EditText,
        passwordEditText: EditText,
        staffnameEditText: EditText,
        ageEditText: EditText,
        positionEditText: String,
        telEditText: EditText,
        addressEditText: EditText,
        context:Context
    ): Boolean {
        var backPossible = false
        if (userNameEditText.text.isBlank() &&addressEditText.text.isBlank() && addressEditText.text.isBlank()
            && telEditText.text.isBlank() && ageEditText.text.isBlank()
            && staffnameEditText.text.isBlank() && passwordEditText.text.isBlank()) {
            backPossible = true
        }
        return backPossible
    }

    private fun getUpdatedItemEntry(
        userName: String,
        password: String,
        staffname: String,
        age: String,
        position: String,
        tel: String,
        address: String
    ): User {
        return User(
            userName = userName,
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