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

    @Query("SELECT * from user ORDER BY staffId ASC")
    fun getAll():Flow<List<User>>

    @Query("SELECT * from user WHERE staffId = :staffId")
    fun getByStaffId(staffId: Int): Flow<User>

    @Query("SELECT * FROM User WHERE staffId = :staffId AND password = :password")
    suspend fun loginByIdAndPassword(staffId: Int, password: String): User?
    @Query("SELECT * FROM User WHERE staffId = :staffId")
    suspend fun loginById(staffId: Int): User?


    @Query("SELECT COUNT(*) FROM user WHERE staffId = :staffId")
    suspend fun countStaffWithId(staffId: Int): Int

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