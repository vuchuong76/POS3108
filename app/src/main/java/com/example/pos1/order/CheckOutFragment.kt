package com.example.pos1.order

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pos1.R
import com.example.pos1.UserApplication
import com.example.pos1.coupon.CouponViewModel
import com.example.pos1.coupon.CouponViewModelFactory
import com.example.pos1.databinding.FragmentCheckOutBinding
import com.example.pos1.entity.Coupon
import com.example.pos1.order.adapter.CheckoutAdapter
import com.example.pos1.orlist.OrderListViewModelFactory
import com.example.pos1.orlist.OrderlistViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Date
import kotlin.math.roundToInt


class CheckOutFragment : Fragment() {

    // Lấy view model chung sử dụng activityViewModels và OrderViewModelFactory
    private val orderViewModel: OrderViewModel by activityViewModels() {
        OrderViewModelFactory(
            (activity?.application as UserApplication).orderDatabase.orderDao(),
            (activity?.application as UserApplication).orderDatabase.itemDao(),
            (activity?.application as UserApplication).orderDatabase.tableDao()
        )
    }
    private val orderlistViewModel: OrderlistViewModel by activityViewModels() {
        OrderListViewModelFactory(
            (activity?.application as UserApplication).orderDatabase.orderlistDao(),
            (activity?.application as UserApplication).orderDatabase.orderDao()
        )
    }
    private val couponViewModel: CouponViewModel by activityViewModels() {
        CouponViewModelFactory(
            (activity?.application as UserApplication).orderDatabase.couponDao()
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
//vô hiệu hoá nút back
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Ở đây, không gọi super.handleOnBackPressed() để vô hiệu hoá nút "Back"
                }
            }
        )
        binding.receive.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {

                val recieveAmount = s.toString().toDoubleOrNull() ?: 0.0
                val lastAmount = orderViewModel.amount1.value ?: 0.0

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
        val adapter = CheckoutAdapter {
//                // Hiển thị hộp thoại xác nhận khi người dùng nhấp vào một món hàng

        }


        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView.adapter = adapter

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.home -> {
//                    orderViewModel.cancel()
                    val action = CheckOutFragmentDirections.actionCheckOutFragmentToMenuTabletFragment()
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
//            orderViewModel.cancel()
            //sau khi nhấp tính tiền dẫn đến màn hình Check out
            val action = CheckOutFragmentDirections.actionCheckOutFragmentToMenuTabletFragment()
            findNavController().navigate(action)
        }


        // Theo dõi LiveData allItems từ OrderViewModel để tự động cập nhật giao diện
        orderViewModel.orderForPay.observe(this.viewLifecycleOwner) { items ->
            items.let {
                var totalAmount = 0.0
                var totalItemCount = 0

                for (order in it) {
                    totalAmount += order.quantity * order.price
                    totalItemCount += order.quantity
                }
                orderViewModel.setAmountAllItems(totalAmount)
                binding.amount.text = "$totalItemCount items Amount: $totalAmount $"
                totalAmount = (totalAmount * 11).roundToInt() / 10.0
                binding.taxAmount.text = "Amount: ${totalAmount} $(tax)"
                //tính coupon
                binding.apply.setOnClickListener {
                    // 1. Lấy dữ liệu từ EditText
                    val enteredCode = binding.code.text.toString()

                    // 2. Truy vấn dữ liệu từ cơ sở dữ liệu
                    couponViewModel.fetchCouponByCode(enteredCode)
                    lifecycleScope.launch {
                        couponViewModel.couponFlow.collectLatest { coupon ->
                            // 3. Cập nhật giao diện
                            if (coupon != null) {
                                binding.coupon.text = "${coupon.discount}%"
                            } else {
                                Toast.makeText(context, "Invalid coupon code!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                binding.change.text = "Change: ${getChangeMoney(totalAmount)} $"
                adapter.submitList(it)

                binding.receipt.setOnClickListener {
                    if (totalAmount > 0) {
                        val receivedMoney = binding.receive.text.toString().trim().toIntOrNull()

                        // Nếu receivedMoney null hoặc nhỏ hơn totalAmount thì báo toast
                        if (receivedMoney == null || receivedMoney < totalAmount) {
                            Toast.makeText(context, "Not Enough Money", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }

                        if (getChangeMoney(totalAmount) >= 0) {
                            val staffIdValue = orderViewModel.id.toInt()
                            val amountValue = orderViewModel.amount1.value ?: 0.0
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
    override fun onDestroyView() {
        super.onDestroyView()
        orderViewModel.cancel()
    }

    fun getChangeMoney(total: Double): Double {
        val rev = binding.receive.text.toString().trim().toIntOrNull() ?: 0
        return if (rev == 0) 0.0 else rev - total
    }


}