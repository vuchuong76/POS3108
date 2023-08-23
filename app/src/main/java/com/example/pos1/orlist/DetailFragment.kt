package com.example.pos1.orlist

import android.annotation.SuppressLint
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

@Suppress("DEPRECATION")
class DetailFragment : Fragment() {
    private val orderViewModel: OrderViewModel by activityViewModels() {
        OrderViewModelFactory(
            (activity?.application as UserApplication).orderDatabase.orderDao(),
            (activity?.application as UserApplication).orderDatabase.itemDao(),
            (activity?.application as UserApplication).orderDatabase.tableDao(),
        )
    }
    private lateinit var binding: FragmentDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }


    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        disableBackButton()
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

        // Theo dõi LiveData allItems từ OrderViewModel để tự động cập nhật giao diện
        orderViewModel.orderById.observe(this.viewLifecycleOwner) { items ->
            items.let {
                var totalAmount = 0.0
                var totalItemCount = 0
                val orid =orderViewModel.selectedId.value
                val lastAmount =orderViewModel.lastAmount1.value
                val discount =orderViewModel.discount.value
                val receive =orderViewModel.receive.value
                val change =orderViewModel.change.value

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
                binding.amount.text = "${String.format("%.1f", totalAmount)}$"
                binding.lastAmount.text = "$lastAmount $"
                binding.discount.text = "$discount %"
                binding.receive.text = "$receive $"
                binding.change.text = "$change $"
                binding.count.text = "$totalItemCount items"
                binding.date.text= "Date : $date"
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