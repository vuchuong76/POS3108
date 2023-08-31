package com.example.pos1.order

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
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
import com.example.pos1.DecimalDigitsInputFilter
import com.example.pos1.R
import com.example.pos1.UserApplication
import com.example.pos1.coupon.CouponViewModel
import com.example.pos1.coupon.CouponViewModelFactory
import com.example.pos1.databinding.FragmentCheckOutBinding
import com.example.pos1.order.adapter.CheckoutAdapter
import com.example.pos1.orlist.OrderListViewModelFactory
import com.example.pos1.orlist.OrderlistViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Date
import kotlin.math.roundToInt


@Suppress("DEPRECATION")
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

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//vô hiệu hoá nút back
        disableBackButton()

        //lưu trạng thái khi xoay màn hình
        if (savedInstanceState != null) {
            val discount = savedInstanceState.getDouble("coupon")
            val receive = savedInstanceState.getDouble("receive")

            binding.coupon.text = discount.toString()
            binding.receive.setText(receive.toString())
        }

//textwatcher theo dõi sự thay đổi của edittext
        binding.receive.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(s: Editable?) {

                val recieveAmount = s.toString().toDoubleOrNull() ?: 0.0
                val lastAmount = orderViewModel.lastAmount.value ?: 0.0
//                if (lastAmount < recieveAmount) {
                    val changeAmount = recieveAmount - lastAmount
                    binding.change.text = "${String.format("%.1f", changeAmount)}"
//                }
//                else{
//                    binding.change.text="0.0"
//                }

            }
        })


        val staffName = orderViewModel.staffName
        val tablenum = orderViewModel.selectedTableNumber.value ?: 0
        binding.idTextView.text = "Staff name : $staffName"
        binding.tableTextView.text = "Table : $tablenum"
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
//quan sát orderViewModel.lastAmount
        orderViewModel.lastAmount.observe(viewLifecycleOwner) { lastAmount ->
            binding.lastAmount.text = String.format("%.1f", lastAmount)
            binding.change.text = String.format("%.1f", getChangeMoney(lastAmount))

        }
        // Theo dõi dữ liệu 'orderForPay' từ 'orderViewModel'.
        orderViewModel.orderForPay.observe(this.viewLifecycleOwner) { items ->
            items.let {
                var amount = 0.0
                var totalItemCount = 0

                for (order in it) {
                    amount += order.quantity * order.price
                    totalItemCount += order.quantity
                }
                if (totalItemCount==1){
                    binding.count.text = "Total : $totalItemCount item"
                }
                else{ binding.count.text = "Total : $totalItemCount items" }
                binding.amount.text = "${String.format("%.1f", amount)} "

                val taxAmount = (amount * 11).roundToInt() / 10.0
                orderViewModel.setAmountAllItems(taxAmount)
                binding.taxAmount.text = "${String.format("%.1f", taxAmount)}"

                var discount: Double = binding.coupon.text.toString().toDouble()
                var lastAmount = (amount * 11).roundToInt() / 10.0

                binding.apply.setOnClickListener {
                    val enteredCode = binding.code.text.toString().trim()

                    if (enteredCode.isEmpty()) {
                        Toast.makeText(context, "Please insert Code!", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    // Lấy thông tin giảm giá dựa trên mã giảm giá từ 'couponViewModel'
                    couponViewModel.fetchCouponByCode(enteredCode)

                    // Theo dõi dữ liệu từ 'couponViewModel'
                    lifecycleScope.launch {
                        couponViewModel.couponFlow.collect { state ->
                            when (state) {
                                is CouponViewModel.CouponState.Success -> {
                                    val coupon = state.coupon
                                    discount = coupon.discount
                                    binding.coupon.text = "$discount"

                                    lastAmount =
                                        (taxAmount * (1 - discount / 100) * 10).roundToInt() / 10.0
                                    orderViewModel.updateData(lastAmount)
                                }
                                CouponViewModel.CouponState.Invalid -> {
                                    Toast.makeText(
                                        context,
                                        "Code is not correct!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                else -> {} // Xử lý cho các trạng thái khác (ví dụ: Loading)
                            }
                        }
                    }
                }


                binding.receive.filters = arrayOf<InputFilter>(DecimalDigitsInputFilter(1))




                binding.clear.setOnClickListener {
                    discount = 0.0
                    orderViewModel.updateData(taxAmount)
                    binding.coupon.text = "0"
                    binding.code.setText("")
                    lastAmount = taxAmount
                }




                orderViewModel.updateData(taxAmount)

                binding.change.text = "${String.format("%.1f", getChangeMoney(lastAmount))} "
                adapter.submitList(it)

                binding.receipt.setOnClickListener {
                    if (lastAmount > 0) {
                        val receivedMoney = binding.receive.text.toString().trim().toDoubleOrNull()
                        val amountValue = lastAmount
                        // Nếu receivedMoney null hoặc nhỏ hơn totalAmount thì báo toast
                        if (receivedMoney == null || receivedMoney < lastAmount) {
                            Toast.makeText(context, "Not enough money", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }

                        if (getChangeMoney(lastAmount) >= 0) {
                            val userNameValue = orderViewModel.userName
                            val orId = Date().time.toString()
                            // Sử dụng CoroutineScope để gọi addNewOrderlist và nhận giá trị orId
                            lifecycleScope.launch {
                                orderlistViewModel.addNewOrderlist(
                                    orId = orId,
                                    tbnum = tablenum,
                                    userName = userNameValue,
                                    date = dateString,
                                    amount = amountValue,
                                    discount = discount,
                                    receive = receivedMoney,
                                    change = getChangeMoney(lastAmount)
                                )
                                orderViewModel.setSelectedId(orId)
                                orderViewModel.setLastAmount(amountValue)
                                orderViewModel.setDiscount(discount)
                                orderViewModel.setReceive(receivedMoney)
                                orderViewModel.setChange(getChangeMoney(lastAmount))

                                val action =
                                    CheckOutFragmentDirections.actionCheckOutFragmentToDetailFragment()
                                findNavController().navigate(action)
                            }
                        }
                    } else Toast.makeText(context, "No item", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // Lưu trạng thái mà bạn muốn giữ lại khi xoay màn hình hoặc tái tạo Fragment
        outState.putDouble("coupon", binding.coupon.text.toString().toDoubleOrNull() ?: 0.0)
        outState.putDouble("receive", binding.receive.text.toString().toDoubleOrNull() ?: 0.0)
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
        return if (rev == 0.0 || newTotal == null ) 0.0
        else
            ((rev - newTotal) * 10).roundToInt() / 10.0
    }


}
