package com.example.pos1.order

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pos1.R
import com.example.pos1.UserApplication
import com.example.pos1.databinding.FragmentMenuTabletBinding
import com.example.pos1.entity.Order
import com.example.pos1.order.adapter.MenuAdapter
import com.example.pos1.order.adapter.OrderAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.math.roundToInt

@Suppress("DEPRECATION")
class MenuTabletFragment : Fragment() {
    private lateinit var binding: FragmentMenuTabletBinding
    private val sharedViewModel: OrderViewModel by activityViewModels() {
        OrderViewModelFactory(
            (activity?.application as UserApplication).orderDatabase.orderDao(),
            (activity?.application as UserApplication).orderDatabase.itemDao(),
            (activity?.application as UserApplication).orderDatabase.tableDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuTabletBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Menu section (from MenuFragment)
        val menuAdapter = MenuAdapter(childFragmentManager, lifecycle)
        binding.pager.adapter = menuAdapter

        TabLayoutMediator(binding.tab, binding.pager) { tab, pos ->
            when (pos) {
                0 -> tab.text = "Drink"
                1 -> tab.text = "Food"
                2 -> tab.text = "Appetizer"
            }
        }.attach()
            val staffName = sharedViewModel.staffName
            val tablenum = sharedViewModel.selectedTableNumber.value ?: 0
            binding.idTextView.text = "Staff name: $staffName"
            binding.tableTextView.text = "Table:$tablenum"

            binding.buttonCheck.setOnClickListener {
                sharedViewModel.updateStatusFromCheckingToWaiting()
            }
            binding.deleteAll.setOnClickListener {
                showConfirmationDialogAll()
            }

            val orderAdapter = OrderAdapter(
                onItemClicked = { order -> },
                onButtonClicked = { order ->
                    showConfirmationDialog(order)
                }
            )

            binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
            binding.recyclerView.adapter = orderAdapter

            binding.toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.home -> {
                        val action = MenuTabletFragmentDirections.actionMenuTabletFragmentToChooseTableFragment("")
                        findNavController().navigate(action)
                        true
                    }
                    R.id.money -> {
                        val orders = sharedViewModel.orderForTable.value
                        if (orders == null) {
                            Toast.makeText(context, "No orders available", Toast.LENGTH_SHORT).show()
                            false
                        } else {
                            var allServed = true
                            for (order in orders) {
                                if (order.order_status != "served") {
                                    allServed = false
                                    break
                                }
                            }
                            if (!allServed) {
                                Toast.makeText(context, "Have an item not yet served", Toast.LENGTH_SHORT).show()
                                false
                            } else {
                                sharedViewModel.pay()
                                val action = MenuTabletFragmentDirections.actionMenuTabletFragmentToCheckOutFragment()
                                findNavController().navigate(action)
                                true
                            }
                        }
                    }
                    else -> false
                }
            }

        sharedViewModel.orderForCheck.observe(this.viewLifecycleOwner) { orders ->
            if (orders.isNotEmpty()) {
//                binding.buttonCheck.visibility = View.VISIBLE
                binding.buttonCheck.alpha = 1.0f
                binding.buttonCheck.isEnabled = true
            } else {
//                binding.buttonCheck.visibility = View.GONE
                binding.buttonCheck.alpha = 0.3f
                binding.buttonCheck.isEnabled = false
            }
        }
        sharedViewModel.orderForDeleteAll.observe(this.viewLifecycleOwner) { orders ->
            if (orders.isNotEmpty()) {
//                binding.deleteAll.visibility = View.VISIBLE
                binding.deleteAll.alpha = 1.0f
                binding.deleteAll.isEnabled = true
            } else {
//                binding.deleteAll.visibility = View.GONE
                binding.deleteAll.alpha = 0.3f
                binding.deleteAll.isEnabled = false

            }
        }
        sharedViewModel.orderForTable.observe(this.viewLifecycleOwner) { items ->
            var totalAmount = 0.0
            var totalItemCount = 0
            if (items != null && items.isNotEmpty()) {
                for (order in items) {
                    totalAmount += order.quantity * order.price
                    totalItemCount += order.quantity
                }
                totalAmount = (totalAmount * 10).roundToInt() / 10.0
                binding.amount.text = "Total Amount: $totalAmount $"
                binding.count.text = "$totalItemCount items"
            } else {
                binding.amount.text = "Total Amount: 0 $"
                binding.count.text = "0 item"
            }
            orderAdapter.submitList(items)
        }
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
}
