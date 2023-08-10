package com.example.pos1.order

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pos1.R

import com.example.pos1.UserApplication
import com.example.pos1.databinding.FragmentOrderDetailBinding
import com.example.pos1.entity.Order


import com.google.android.material.dialog.MaterialAlertDialogBuilder

import java.text.SimpleDateFormat
import java.util.Calendar

class OrderDetailFragment : Fragment() {
    // Lấy view model chung sử dụng activityViewModels và OrderViewModelFactory
    private val sharedViewModel: OrderViewModel by activityViewModels() {
        OrderViewModelFactory(
            (activity?.application as UserApplication).orderDatabase.orderDao(),
            (activity?.application as UserApplication).orderDatabase.itemDao(),
            (activity?.application as UserApplication).orderDatabase.tableDao()
        )
    }
    private lateinit var binding: FragmentOrderDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderDetailBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    private fun showConfirmationDialog(order: Order) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.delete_question))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                sharedViewModel.deleteOrderAndUpdateTotals(order)
            }
            .show()
    }

    private fun showConfirmationDialogAll() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.delete_question_all))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                sharedViewModel.deleteAll()
            }
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = sharedViewModel.id
        val tablenum = sharedViewModel.selectedTableNumber.value ?: 0
        binding.idTextView.text = "STAFF ID: $id"
        binding.tableTextView.text = "Table:$tablenum"

        binding.buttonCheck.setOnClickListener {
            sharedViewModel.updateStatusFromCheckingToWaiting()
        }
        binding.deleteAll.setOnClickListener {
            showConfirmationDialogAll()
        }
        val adapter = OrderAdapter(
            onItemClicked = { order ->

            },
            onButtonClicked = { order ->
                showConfirmationDialog(order)
                // Xử lý sự kiện khi button được nhấn
                // Ví dụ:
                // sharedViewModel.someFunction(order)
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView.adapter = adapter

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.home -> {
                    val action =
                        TabletFragmentDirections.actionTabletFragmentToChooseTableFragment()
                    findNavController().navigate(action)
                    true
                }

                R.id.money -> {
                    val orders = sharedViewModel.orderForTable.value
                    if (
                        orders == null) {
                        Toast.makeText(context, "No orders available", Toast.LENGTH_SHORT).show()
                        return@setOnMenuItemClickListener false
                    } else {
                        var allServed = true
                        for (order in orders) {
                            if (order.order_status != "served") {
                                allServed = false
                                break
                            }
                        }
                        if (!allServed) {
                            Toast.makeText(
                                context,
                                "Have an item not yet served",
                                Toast.LENGTH_SHORT
                            ).show()
                            false
                        } else {
                            sharedViewModel.pay()
                            val action =
                                TabletFragmentDirections.actionTabletFragmentToCheckOutFragment()
                            findNavController().navigate(action)
                            true
                        }
                    }
                }

                else -> false
            }
        }
        sharedViewModel.orderForTable.observe(this.viewLifecycleOwner) { items ->
            if (items.isNullOrEmpty()) {
                sharedViewModel.updateTableStatus(tablenum, 0)
            } else {
                sharedViewModel.updateTableStatus(tablenum, 1)
            }
            var totalAmount = 0
            var totalItemCount = 0
            if (items != null && items.isNotEmpty()) {
                for (order in items) {
                    totalAmount += order.quantity * order.price
                    totalItemCount += order.quantity
                }
                binding.amount.text = "Total Amount: $totalAmount $"
                binding.count.text = "$totalItemCount items"
                adapter.submitList(items)
            } else {
                // Set your default values or other logic here if items is null or empty
                binding.amount.text = "Total Amount: 0 $"
                binding.count.text = "0 item"

                adapter.submitList(emptyList())
            }
        }


    }

}
