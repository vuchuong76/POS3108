package com.example.pos1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.pos1.order.CheckOutFragment



class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
//    val permissionId = 1000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        val navHostFragment= supportFragmentManager
            .findFragmentById(R.id.fragment)as NavHostFragment
        navController=navHostFragment.navController


    }










    override fun onBackPressed() {
        // Lấy fragment đang hiển thị
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment)

        // Kiểm tra nếu fragment đang hiển thị là CheckOutFragment
        if (fragment is CheckOutFragment) {
            // Không cho phép hoạt động mặc định của nút back bằng cách không làm gì
            return
        }

        // Nếu không phải CheckOutFragment, thì gọi phương thức của lớp cha để xử lý bình thường
        super.onBackPressed()
    }
}