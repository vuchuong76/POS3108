package com.example.pos1.order

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pos1.UserApplication
import com.example.pos1.databinding.FragmentAppetizerBinding
import com.example.pos1.editmenu.ItemViewModel
import com.example.pos1.editmenu.ItemViewModelFactory
import com.example.pos1.entity.Order
import java.text.SimpleDateFormat
import java.util.Calendar

class AppetizerFragment : Fragment() {
    val calendar = Calendar.getInstance()
    // Định dạng đối tượng Calendar thành chuỗi theo định dạng "HH:mm"
    val timeFormat = SimpleDateFormat("HH:mm")
    val currentTime = timeFormat.format(calendar.time)
    private val viewModel: ItemViewModel by activityViewModels {
        ItemViewModelFactory(
            (activity?.application as UserApplication).orderDatabase.itemDao()
        )
    }
    private val sharedViewModel: OrderViewModel by activityViewModels() {
        OrderViewModelFactory(
            (activity?.application as UserApplication).orderDatabase.orderDao()
        )
    }
    lateinit var order: Order
    private lateinit var binding: FragmentAppetizerBinding
    private lateinit var adapter: FoodDrinkAppetizerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAppetizerBinding.inflate(inflater, container, false)
        return binding.root
    }
    //add item bằng việc click vào item
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = FoodDrinkAppetizerAdapter({ item ->
                //khi click vào item trong menu thì tự add item đó vào order
                sharedViewModel.addNewOrder(
                    itemId = item.id.toInt(),
                    table=sharedViewModel.selectedTableNumber.value?:0,
                    name = item.name,
                    time = currentTime,
                    quantity = 1,
                    price = item.price.toInt(),
                    order_status = "checking",
                    pay_sta="waiting"
                )
//            sharedViewModel.getOrdersByNumber()
        }, "appetizer")


        binding.recyclerView3.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView3.adapter = adapter

        viewModel.allItems.observe(this.viewLifecycleOwner) { items ->
            items.let {
                adapter.setItems(it)
            }
        }
    }
}