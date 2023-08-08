package com.example.pos1.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.pos1.UserApplication
import com.example.pos1.databinding.FragmentMenuBinding

import com.google.android.material.tabs.TabLayoutMediator


class MenuFragment : Fragment() {
    //    private val args: MenuFragmentArgs by navArgs()
    private lateinit var binding: FragmentMenuBinding
    private val sharedViewModel: OrderViewModel by activityViewModels() {
        OrderViewModelFactory(
            (activity?.application as UserApplication).orderDatabase.orderDao(),
            (activity?.application as UserApplication).orderDatabase.itemDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMenuBinding.inflate(inflater, container, false)

//        setActionBarTitle("Menu")
        // Báo cho hệ thống rằng Fragment này có menu
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //hiển thị menu
        val adapter = MenuAdapter(requireActivity().supportFragmentManager, lifecycle)
        binding.pager.adapter = adapter


        // Thiết lập TabLayout và ViewPager
        TabLayoutMediator(binding.tab, binding.pager) { tab, pos ->
            when (pos) {
                0 -> {
                    tab.text = "Drink"
                }

                1 -> {
                    tab.text = "Food"
                }

                2 -> {
                    tab.text = "Appetizer"
                }
            }
        }.attach()
    }


}

