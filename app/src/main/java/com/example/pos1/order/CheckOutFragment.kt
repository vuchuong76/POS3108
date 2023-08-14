package com.example.pos1.order

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
        binding = FragmentCheckOutBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//vô hiệu hoá nút back
        disableBackButton()


        binding.receive.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {

                val recieveAmount = s.toString().toDoubleOrNull() ?: 0.0
                val lastAmount = orderViewModel.total.value ?: 0.0

                val changeAmount = recieveAmount - lastAmount
                binding.change.text = "$changeAmount $"


            }
        })


        val id = orderViewModel.id
        val tablenum = orderViewModel.selectedTableNumber.value ?: 0
        binding.idTextView.text = "User name: $id"
        binding.tableTextView.text = "Table:$tablenum"
        val adapter = CheckoutAdapter {
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView.adapter = adapter
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.home -> {
                    orderViewModel.cancel()
                    val action =
                        CheckOutFragmentDirections.actionCheckOutFragmentToMenuTabletFragment()
                    findNavController().navigate(action)
                    true
                }

                else -> false
            }
        }

        //lấy ngày tháng
        val currentDate = LocalDate.now()
        val dateString = currentDate.toString()


        binding.cancelButton.setOnClickListener {
            orderViewModel.cancel()
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
                binding.count.text="Total : $totalItemCount items"
                binding.amount.text = " $totalAmount $"
                totalAmount = (totalAmount * 11).roundToInt() / 10.0
                orderViewModel.setAmountAllItems(totalAmount)
                binding.taxAmount.text = "${totalAmount} $"




                // Đặt mặc định discount là 0
                var discount = 0.0
                var total = (totalAmount * 1.1 * 10).roundToInt() / 10.0


                //tính coupon
                // Xử lý logic mã giảm giá khi nút 'apply' được nhấn.
                binding.apply.setOnClickListener {
                    val enteredCode = binding.code.text.toString().trim()

                    if (enteredCode.isEmpty()) {
                        Toast.makeText(context, "Please insert Code!", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    couponViewModel.fetchCouponByCode(enteredCode)

                    lifecycleScope.launch {
                        couponViewModel.couponFlow.collect { state ->
                            when(state) {
                                is CouponViewModel.CouponState.Success -> {
                                    val coupon = state.coupon
                                    discount = coupon.discount // Đảm bảo trường này là Double
                                    binding.coupon.text = "${discount}%"

                                    total = (totalAmount * (1 - discount / 100) * 10).roundToInt() / 10.0
                                    orderViewModel.updateData(total)
                                }
                                CouponViewModel.CouponState.Invalid -> {
                                    Toast.makeText(context, "Code is not correct!", Toast.LENGTH_SHORT).show()
                                }
                                else -> {} // Xử lý cho trạng thái Loading nếu bạn muốn.
                            }
                        }
                    }
                }






                binding.clear.setOnClickListener {
                    discount = 0.0
                    orderViewModel.updateData(totalAmount)
                    binding.coupon.text = "0%"
                    binding.code.setText("")
                }




                orderViewModel.updateData(totalAmount)

                binding.change.text = "${getChangeMoney(total)}$"
                adapter.submitList(it)

                binding.receipt.setOnClickListener {
                    if (total > 0) {
                        val receivedMoney = binding.receive.text.toString().trim().toDoubleOrNull()

                        // Nếu receivedMoney null hoặc nhỏ hơn totalAmount thì báo toast
                        if (receivedMoney == null || receivedMoney < total) {
                            Toast.makeText(context, "Not enough money", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }

                        if (getChangeMoney(total) >= 0) {
                            val staffIdValue = orderViewModel.id
                            val amountValue = total
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
                                val action =
                                    CheckOutFragmentDirections.actionCheckOutFragmentToDetailFragment()
                                findNavController().navigate(action)
                            }
                        }
                    } else Toast.makeText(context, "No item", Toast.LENGTH_SHORT).show()
                }
            }
        }
        orderViewModel.total.observe(viewLifecycleOwner) { newTotal ->
            binding.total.text = String.format("%.1f$", newTotal)
            binding.change.text = String.format("%.1f$", getChangeMoney(newTotal))


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
    fun getChangeMoney(newTotal: Double?): Double {
        val rev = binding.receive.text.toString().trim().toDoubleOrNull() ?: 0.0
        return if (rev == 0.0 || newTotal == null) 0.0 else ((rev - newTotal) * 10).roundToInt() / 10.0
    }



}
