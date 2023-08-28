package com.example.pos1

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdminFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check if the NavHostFragment already exists, if not, create it
        if (childFragmentManager.findFragmentById(R.id.container) == null) {
            val navHostFragment = NavHostFragment.create(R.navigation.nav_graph)
            childFragmentManager.beginTransaction()
                .add(R.id.container, navHostFragment)
                .commit()
        }

        // Set up BottomNavigationView with NavController
        val navHostFragment = childFragmentManager.findFragmentById(R.id.container) as? NavHostFragment
        navHostFragment?.let {
            val navController = it.navController
            val bottomNav = view.findViewById<BottomNavigationView>(R.id.bottomNav)
            NavigationUI.setupWithNavController(bottomNav, navController)
        }
    }
}
