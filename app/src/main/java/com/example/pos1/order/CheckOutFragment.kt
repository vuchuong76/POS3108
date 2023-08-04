package com.example.pos1.order

import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pos1.R
import com.example.pos1.UserApplication
import com.example.pos1.databinding.FragmentCheckOutBinding
import com.example.pos1.entity.Orderlist
import com.example.pos1.orlist.OrderListViewModelFactory
import com.example.pos1.orlist.OrderlistViewModel
import com.example.pos1.utils.Constants.logger
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.Date
import java.util.concurrent.TimeUnit


class CheckOutFragment : Fragment() {

    // Lấy view model chung sử dụng activityViewModels và OrderViewModelFactory
    private val orderViewModel: OrderViewModel by activityViewModels() {
        OrderViewModelFactory(
            (activity?.application as UserApplication).orderDatabase.orderDao()
        )
    }
    private val orderlistViewModel: OrderlistViewModel by activityViewModels() {
        OrderListViewModelFactory(
            (activity?.application as UserApplication).orderDatabase.orderlistDao(),
            (activity?.application as UserApplication).orderDatabase.orderDao()
        )
    }

    private lateinit var binding: FragmentCheckOutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Gắn layout cho fragment này bằng cách sử dụng binding class được tạo ra
        binding = FragmentCheckOutBinding.inflate(inflater, container, false)

        // Báo cho hệ thống rằng Fragment này có menu
        setHasOptionsMenu(true)
        return binding.root
    }
//    private fun showConfirmationDialog() {
//        MaterialAlertDialogBuilder(requireContext())
//            .setTitle(getString(android.R.string.dialog_alert_title))
//            .setMessage("Do you want to delete this table?")
//            .setCancelable(false)
//            .setNegativeButton("No") { _, _ -> }
//            .setPositiveButton("Yes") { _, _ ->
//                deleteTable()
//            }
//            .show()
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        orderViewModel.getOrdersForPay()
        binding.recieve.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {

                val recieveAmount = s.toString().toIntOrNull() ?: 0
                val lastAmount = orderViewModel.amount1.value ?: 0

                val changeAmount = recieveAmount - lastAmount
                binding.change.text = "Change: $changeAmount $"


            }
        })

        // Khởi tạo và liên kết các thành phần giao diện từ layout

        val id = orderViewModel.id
        val tablenum = orderViewModel.selectedTableNumber.value ?: 0
        binding.idTextView.text = "STAFF ID: $id"
        binding.tableTextView.text = "Table:$tablenum"


        // Khởi tạo OrderAdapter và đặt làm adapter cho RecyclerView
        val adapter = OrderAdapter { order ->
//                // Hiển thị hộp thoại xác nhận khi người dùng nhấp vào một món hàng
            orderViewModel.deleteOrderAndUpdateTotals(order)

        }


        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView.adapter = adapter

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.home -> {
                    val action = CheckOutFragmentDirections.actionCheckOutFragmentToTabletFragment()
                    findNavController().navigate(action)
                    true
                }

                else -> false
            }
        }


        //lấy ngày tháng
        val currentDate = LocalDate.now()
        val dateString = currentDate.toString()
        //xuất hóa đơn và thêm vào orderlist

        binding.cancelButton.setOnClickListener {
            orderViewModel.cancel()


            //sau khi nhấp tính tiền dẫn đến màn hình Check out
            val action = CheckOutFragmentDirections.actionCheckOutFragmentToTabletFragment()
            findNavController().navigate(action)
        }


        // Theo dõi LiveData allItems từ OrderViewModel để tự động cập nhật giao diện
        orderViewModel.orderForPay.observe(this.viewLifecycleOwner) { items ->
            items.let {
                var totalAmount = 0
                var totalItemCount = 0

                for (order in it) {
                    totalAmount += order.quantity * order.price
                    totalItemCount += order.quantity
                }
                orderViewModel.setAmountAllItems(totalAmount)
                binding.amount.text = "$totalItemCount items Amount: $totalAmount $"

                binding.change.text = "Change: ${getChangeMoney(totalAmount)} $"
                adapter.submitList(it)

//                binding.receipt.setOnClickListener {
//                    if (totalAmount > 0) {
//                        if (binding.recieve.text?.toString().isNullOrEmpty().not()) {
//                            if (getChangeMoney(totalAmount) >= 0) {
//                                val staffIdValue = orderViewModel.id.toInt()
//                                val amountValue = orderViewModel.amount1.value ?: 0
//                                val a = Date().time.toString()
//                                // Sử dụng CoroutineScope để gọi addNewOrderlist và nhận giá trị orId
//                                lifecycleScope.launch {
//                                    orderlistViewModel.addNewOrderlist(
//                                        orId = a,
//                                        tbnum = tablenum,
//                                        staffId = staffIdValue,
//                                        date = dateString,
//                                        amount = amountValue,
//                                        status = "payed",
//                                        payment = "money"
//                                    )
//                                    orderViewModel.setSelectedId(a)
//                                    val action =
//                                        CheckOutFragmentDirections.actionCheckOutFragmentToDetailFragment()
//                                    findNavController().navigate(action)
//                                }
//                            } else Toast.makeText(context, "Not Enough Money", Toast.LENGTH_SHORT)
//                                .show()
//
//                        } else Toast.makeText(context, "Not Enough Money", Toast.LENGTH_SHORT)
//                            .show()
//                    } else Toast.makeText(context, "No item", Toast.LENGTH_SHORT).show()
//                }
                binding.receipt.setOnClickListener {
                    if (totalAmount > 0) {
                        val receivedMoney = binding.recieve.text.toString().trim().toIntOrNull()

                        // Nếu receivedMoney null hoặc nhỏ hơn totalAmount thì báo toast
                        if (receivedMoney == null || receivedMoney < totalAmount) {
                            Toast.makeText(context, "Not Enough Money", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }

                        if (getChangeMoney(totalAmount) >= 0) {
                            val staffIdValue = orderViewModel.id.toInt()
                            val amountValue = orderViewModel.amount1.value ?: 0
                            val a = Date().time.toString()
                            // Sử dụng CoroutineScope để gọi addNewOrderlist và nhận giá trị orId
                            lifecycleScope.launch {
                                orderlistViewModel.addNewOrderlist(
                                    orId = a,
                                    tbnum = tablenum,
                                    staffId = staffIdValue,
                                    date = dateString,
                                    amount = amountValue,
                                    status = "payed",
                                    payment = "money"
                                )
                                orderViewModel.setSelectedId(a)
                                val action = CheckOutFragmentDirections.actionCheckOutFragmentToDetailFragment()
                                findNavController().navigate(action)
                            }
                        }
                    } else Toast.makeText(context, "No item", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun getChangeMoney(total: Int): Int {
        val rev = binding.recieve.text.toString().trim().toIntOrNull() ?: 0
        return if (rev == 0) 0 else rev - total
    }


}