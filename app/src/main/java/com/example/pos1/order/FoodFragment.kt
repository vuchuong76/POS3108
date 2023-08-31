package com.example.pos1.order

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pos1.UserApplication
import com.example.pos1.databinding.FragmentFoodBinding
import com.example.pos1.editmenu.ItemViewModel
import com.example.pos1.editmenu.ItemViewModelFactory
import com.example.pos1.entity.Order
import com.example.pos1.order.adapter.FoodDrinkAppetizerAdapter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar

class FoodFragment : Fragment() {
    private val viewModel: ItemViewModel by activityViewModels {
        ItemViewModelFactory(
            (activity?.application as UserApplication).orderDatabase.itemDao()
        )
    }

    val calendar = Calendar.getInstance()

    // Định dạng đối tượng Calendar thành chuỗi theo định dạng "HH:mm"
    @SuppressLint("SimpleDateFormat")
    val timeFormat = SimpleDateFormat("HH:mm")
    val currentTime = timeFormat.format(calendar.time)
    private val sharedViewModel: OrderViewModel by activityViewModels() {
        OrderViewModelFactory(
            (activity?.application as UserApplication).orderDatabase.orderDao(),
            (activity?.application as UserApplication).orderDatabase.itemDao(),
            (activity?.application as UserApplication).orderDatabase.tableDao()
        )
    }
    lateinit var order: Order
    private lateinit var binding: FragmentFoodBinding
    private lateinit var adapter: FoodDrinkAppetizerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFoodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = FoodDrinkAppetizerAdapter({ item ->
            if (!viewModel.isStockAvailable(item)) {
                Toast.makeText(context,
                        "Out of stock", Toast.LENGTH_SHORT).show()
            } else {
                lifecycleScope.launch {
                    viewModel.sellItem(item)
                    sharedViewModel.addNewOrder(
                        itemId = item.id,
                        table = sharedViewModel.selectedTableNumber.value ?: 0,
                        name = item.name,
                        time = currentTime,
                        quantity = 1,
                        price = item.price,
                        order_status = "checking",
                        pay_sta = "waiting"
                    )
                }
            }
        }, "Food")

        binding.recyclerView1.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView1.adapter = adapter

        viewModel.allItems.observe(this.viewLifecycleOwner) { items ->
            items.let {
                adapter.setItems(it)
            }
        }
    }
}
