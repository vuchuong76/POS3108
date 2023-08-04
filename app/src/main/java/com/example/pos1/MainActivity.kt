package com.example.pos1

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.pos1.order.CheckOutFragment



class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    val permissionId = 1000
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