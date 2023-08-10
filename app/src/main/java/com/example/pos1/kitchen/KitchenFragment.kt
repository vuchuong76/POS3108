package com.example.pos1.kitchen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pos1.R
import com.example.pos1.UserApplication
import com.example.pos1.databinding.FragmentKitchenBinding
import com.example.pos1.order.adapter.OrderAdapter
import com.example.pos1.order.OrderViewModel
import com.example.pos1.order.OrderViewModelFactory

class KitchenFragment : Fragment() {
    // Lấy view model chung sử dụng activityViewModels và OrderViewModelFactory
    private val sharedViewModel: OrderViewModel by activityViewModels() {
        OrderViewModelFactory(
            (activity?.application as UserApplication).orderDatabase.orderDao(),
            (activity?.application as UserApplication).orderDatabase.itemDao(),
            (activity?.application as UserApplication).orderDatabase.tableDao()
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
        val adapter = OrderAdapter(
            onItemClicked = { order ->

            },
            onButtonClicked = { order ->
                sharedViewModel.serve(order)
                // Xử lý sự kiện khi button được nhấn
                // Ví dụ:
                // sharedViewModel.someFunction(order)
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView.adapter = adapter


        sharedViewModel.getOrderForKitchen().observe(viewLifecycleOwner) { items ->
            items?.let {
                adapter.submitList(it)
            }
        }
    }





}
