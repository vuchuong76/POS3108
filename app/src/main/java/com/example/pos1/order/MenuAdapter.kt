package com.example.pos1.order

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.pos1.order.AppetizerFragment
import com.example.pos1.order.DrinkFragment
import com.example.pos1.order.FoodFragment

class MenuAdapter(fragmentManager: FragmentManager,lifecycle: Lifecycle):FragmentStateAdapter(fragmentManager,lifecycle) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0->{
                DrinkFragment()
            }
            1->{
                FoodFragment()
            }
            else->{
                AppetizerFragment()
            }
        }
    }
}