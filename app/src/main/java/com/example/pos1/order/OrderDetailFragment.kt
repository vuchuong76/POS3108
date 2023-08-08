package com.example.pos1.order

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pos1.R

import com.example.pos1.UserApplication
import com.example.pos1.databinding.FragmentOrderDetailBinding
import com.example.pos1.entity.Order
import com.example.pos1.table.TableViewModel
import com.example.pos1.table.TableViewModelFactory


import com.google.android.material.dialog.MaterialAlertDialogBuilder

import java.text.SimpleDateFormat
import java.util.Calendar

class OrderDetailFragment : Fragment() {
    // Lấy view model chung sử dụng activityViewModels và OrderViewModelFactory
    private val sharedViewModel: OrderViewModel by activityViewModels() {
        OrderViewModelFactory(
            (activity?.application as UserApplication).orderDatabase.orderDao()  ,
            (activity?.application as UserApplication).orderDatabase.itemDao()
        )
    }
    private val tableViewModel: TableViewModel by activityViewModels() {
        TableViewModelFactory(
            (activity?.application as UserApplication).orderDatabase.tableDao()
        )
    }
    private lateinit var tableAdapter: ChooseTableAdapter


//    // Lấy thời gian hiện tại sử dụng Calendar và định dạng thành "HH:mm"
//    val calendar = Calendar.getInstance()
//    val timeFormat = SimpleDateFormat("HH:mm")
//    val currentTime = timeFormat.format(calendar.time)

    private lateinit var binding: FragmentOrderDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Gắn layout cho fragment này bằng cách sử dụng binding class được tạo ra
        binding = FragmentOrderDetailBinding.inflate(inflater, container, false)


        // Báo cho hệ thống rằng Fragment này có menu
        setHasOptionsMenu(true)
        return binding.root
    }

    // Hiển thị hộp thoại xác nhận xóa một món hàng trong đơn hàng
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

        // Khởi tạo và liên kết các thành phần giao diện từ layout
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

        // Khởi tạo OrderAdapter và đặt làm adapter cho RecyclerView
        val adapter = OrderAdapter { order ->
            // Hiển thị hộp thoại xác nhận khi người dùng nhấp vào một món hàng
            showConfirmationDialog(order)
        }
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
//                        orders?.size == 0||
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
                            // chuyển các món thành trạng thái paying để qua màn hình trả tiền
                            sharedViewModel.pay()

                            // sau khi nhấp tính tiền dẫn đến màn hình Check out
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
//Gọi món B3
        // Theo dõi LiveData allItems từ OrderViewModel để tự động cập nhật giao diện
        sharedViewModel.orderForTable.observe(this.viewLifecycleOwner) { items ->
            var totalAmount = 0
            var totalItemCount = 0
            if (items != null && items.isNotEmpty()) {
                for (order in items) {
                    totalAmount += order.quantity * order.price
                    totalItemCount += order.quantity
                }
                binding.amount.text = "$totalItemCount items Amount: $totalAmount $"
                adapter.submitList(items)
            } else {
                // Set your default values or other logic here if items is null or empty
                binding.amount.text = "0 items Amount: 0 $"
                adapter.submitList(emptyList())
            }

            val tableId = sharedViewModel.selectedTableNumber.value!! // Cung cấp ID của bảng bạn muốn cập nhật.
            tableViewModel.updateTableStatusIfNotEmpty(tableId, items)
        }

    }

}
