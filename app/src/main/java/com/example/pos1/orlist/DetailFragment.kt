package com.example.pos1.orlist

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pos1.R
import com.example.pos1.UserApplication
import com.example.pos1.databinding.FragmentDetailBinding
import com.example.pos1.order.adapter.CheckoutAdapter
import com.example.pos1.order.OrderViewModel
import com.example.pos1.order.OrderViewModelFactory

class DetailFragment : Fragment() {
    // Lấy view model chung sử dụng activityViewModels và OrderViewModelFactory
    private val orderViewModel: OrderViewModel by activityViewModels() {
        OrderViewModelFactory(
            (activity?.application as UserApplication).orderDatabase.orderDao(),
            (activity?.application as UserApplication).orderDatabase.itemDao(),
            (activity?.application as UserApplication).orderDatabase.tableDao(),
        )
    }
    private val orderListViewModel: OrderlistViewModel by activityViewModels() {
        OrderListViewModelFactory(
            (activity?.application as UserApplication).orderDatabase.orderlistDao(),
            (activity?.application as UserApplication).orderDatabase.orderDao()
        )
    }
    private lateinit var binding: FragmentDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Gắn layout cho fragment này bằng cách sử dụng binding class được tạo ra
        binding = FragmentDetailBinding.inflate(inflater, container, false)


        // Báo cho hệ thống rằng Fragment này có menu
        setHasOptionsMenu(true)
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        disableBackButton()

        // Khởi tạo OrderAdapter và đặt làm adapter cho RecyclerView
        val adapter = CheckoutAdapter {
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView.adapter = adapter
        binding.toolbar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.home -> {
                    val action = DetailFragmentDirections.actionDetailFragmentToChooseTableFragment("")
                    findNavController().navigate(action)
                    true
                }
                else -> false
            }}

//Gọi món B3
        // Theo dõi LiveData allItems từ OrderViewModel để tự động cập nhật giao diện
        orderViewModel.orderById.observe(this.viewLifecycleOwner) { items ->
            items.let {
                var totalAmount = 0.0
                var totalItemCount = 0
                var orid =orderViewModel.selectedId.value
                var lastAmount =orderViewModel.lastAmount1.value
                var receive =orderViewModel.receive.value
                var change =orderViewModel.change.value

                var table=0
                var date=""
                for (order in it) {
                    totalAmount += order.quantity * order.price*1.1
                    totalItemCount += order.quantity
                    table   = order.tableNumber
                    date=order.date
                }
                binding.idTextView.text="Order ID: $orid"
                binding.tableTextView.text="Table: $table"
//                binding.amount.text = "Total Amount(tax 10%): $totalAmount $"

                binding.amount.text = "Total Amount(tax 10%):${String.format("%.1f", totalAmount)}$"

                binding.lastAmount.text = "Last Total Amount: $lastAmount $"
                binding.receive.text = "Receive: $receive $"
                binding.change.text = "Change: $change $"
                binding.count.text = "$totalItemCount items"
                binding.date.text= "$date"
                adapter.submitList(it)
            }
        }
    }
    private fun disableBackButton() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {}
            }
        )
    }





}