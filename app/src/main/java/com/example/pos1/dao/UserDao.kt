package com.example.pos1.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.pos1.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * from user ORDER BY userName ASC")
    fun getAll():Flow<List<User>>

    @Query("SELECT * from user WHERE userName = :userName")
    fun getByStaffId(userName: String): Flow<User>

    @Query("SELECT * FROM User WHERE userName = :userName AND password = :password")
    suspend fun loginByIdAndPassword(userName: String, password: String): User?
    @Query("SELECT * FROM User WHERE userName = :userName")
    suspend fun loginById(userName: String): User?


    @Query("SELECT COUNT(*) FROM user WHERE userName = :userName")
    suspend fun countStaffWithId(userName: String): Int

    // Chỉ định khi xung đột là BỎ QUA, khi người dùng cố gắng thêm một
    // Mục hiện có vào cơ sở dữ liệu.
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: User)

    @Update
    suspend fun update(user: User)

    @Delete
    suspend fun delete(user: User)

    @Query("SELECT staffname FROM user")
    fun getAllUserNames(): LiveData<List<String>>

}