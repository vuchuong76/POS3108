package com.example.pos1.coupon

import android.content.Context
import android.os.Bundle
import android.text.InputFilter
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.pos1.DecimalDigitsInputFilter
import com.example.pos1.R
import com.example.pos1.UserApplication
import com.example.pos1.databinding.FragmentAddCouponBinding

@Suppress("DEPRECATION")
class AddCouponFragment : Fragment() {
    private val viewModel: CouponViewModel by activityViewModels {
        CouponViewModelFactory(
            (activity?.application as UserApplication).orderDatabase.couponDao()
        )
    }
    private var _binding: FragmentAddCouponBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddCouponBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addBt1.setOnClickListener {
            addNewCoupon()

        }
        binding.coupon.filters = arrayOf<InputFilter>(DecimalDigitsInputFilter(0))


        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {

                R.id.back -> {
                    val action =
                        AddCouponFragmentDirections.actionAddCouponFragmentToCouponFragment()
                    findNavController().navigate(action)
                    true
                    // by returning 'true' we're saying that the event
                    // is handled and it shouldn't be propagated further
                }

                else -> false
            }
        }
    }

    private fun addNewCoupon() {
        if (isEntryValid()) {
            val codeInput = binding.code.text.toString()
            viewModel.codeExist(codeInput) { exist ->
                if (!exist) {
                    viewModel.addNewCoupon(
                        codeInput,
                        binding.coupon.text.toString().toDouble()
                    )
                    val action =
                        AddCouponFragmentDirections.actionAddCouponFragmentToCouponFragment()
                    findNavController().navigate(action)
                }
                else{
                    Toast.makeText(context, "Coupon Code is already exist", Toast.LENGTH_SHORT).show()
                }

            }
        }
            else{
                Toast.makeText(context, "Invalid Coupon", Toast.LENGTH_SHORT).show()
            }
        }

    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.code,
            binding.coupon
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Hide keyboard.
        val inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as
                    InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            requireActivity().currentFocus?.windowToken,
            0
        )
        _binding = null
    }

}