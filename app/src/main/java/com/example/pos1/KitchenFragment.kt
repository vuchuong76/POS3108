package com.example.pos1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pos1.databinding.FragmentKitchenBinding
import com.example.pos1.entity.Order
import com.example.pos1.order.OrderAdapter
import com.example.pos1.order.OrderViewModel
import com.example.pos1.order.OrderViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class KitchenFragment : Fragment() {
    // Lấy view model chung sử dụng activityViewModels và OrderViewModelFactory
    private val sharedViewModel: OrderViewModel by activityViewModels() {
        OrderViewModelFactory(
            (activity?.application as UserApplication).orderDatabase.orderDao(),
            (activity?.application as UserApplication).orderDatabase.itemDao()
        )
    }
    private lateinit var binding: FragmentKitchenBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Gắn layout cho fragment này bằng cách sử dụng binding class được tạo ra
        binding = FragmentKitchenBinding.inflate(inflater, container, false)

        // Báo cho hệ thống rằng Fragment này có menu
        setHasOptionsMenu(true)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {

                R.id.logout -> {
                    val action = KitchenFragmentDirections.actionKitchenFragmentToLoginFragment()
                    findNavController().navigate(action)
                    true
                }
                else -> false
            }
        }
        binding.stock.setOnClickListener {
            val action = KitchenFragmentDirections.actionKitchenFragmentToStockFragment()
            findNavController().navigate(action)
        }


        // Khởi tạo OrderAdapter và đặt làm adapter cho RecyclerView
        val adapter = OrderAdapter { order ->
            sharedViewModel.serve(order)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView.adapter = adapter


        sharedViewModel.getOrderForKitchen().observe(viewLifecycleOwner) { items ->
            items?.let {
                adapter.submitList(it)
            }
        }
    }





}
