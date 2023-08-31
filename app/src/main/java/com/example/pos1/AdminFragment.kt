package com.example.pos1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdminFragment : Fragment() {

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //NavHostFragment) bằng ID của nó. NavHostFragment là một đối tượng đặc biệt mà
        // Navigation Component sử dụng để quản lý các destination.
        val navHostFragment = childFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
//Lấy ra NavController từ NavHostFragment. NavController là trung tâm của Navigation Component,
// nó quản lý điều hướng giữa các destination trong app.
        val bottomNavigationView: BottomNavigationView = view.findViewById(R.id.bottom_navigation)
        //Tìm BottomNavigationView bằng ID của nó trong view.
        // BottomNavigationView là một thành phần của Material Design dùng
        // để hiển thị một thanh điều hướng ở phía dưới màn hình.
        bottomNavigationView.setupWithNavController(navController)
    }
}
