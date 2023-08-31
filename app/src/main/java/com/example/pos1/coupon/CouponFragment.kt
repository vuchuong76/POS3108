package com.example.pos1.coupon

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager

import com.example.pos1.R
import com.example.pos1.UserApplication
import com.example.pos1.databinding.FragmentCouponBinding
import com.example.pos1.entity.Coupon
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

@Suppress("DEPRECATION")
class CouponFragment : Fragment() {
    private lateinit var binding: FragmentCouponBinding

    private val viewModel: CouponViewModel by activityViewModels {
        CouponViewModelFactory(
            (activity?.application as UserApplication).orderDatabase.couponDao()
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCouponBinding.inflate(inflater, container, false)


        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.floatingActionButton.setOnClickListener {
            val action = CouponFragmentDirections.actionCouponFragmentToAddCouponFragment(
            )
            this.findNavController().navigate(action)
        }
        val adapter = CouponAdapter (
            onItemClicked={ coupon ->
            showConfirmationDialog(coupon)
        }
        )
        viewModel.allCoupons.observe(this.viewLifecycleOwner) { items ->
            items.let {
                adapter.submitList(it)
            }
        }

        //  Thiết lập RecyclerView
        val layoutManager = GridLayoutManager(context, 1)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {

                R.id.back -> {
                    findNavController().navigateUp()
                    true
                    // by returning 'true' we're saying that the event
                    // is handled and it shouldn't be propagated further
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav?.visibility = View.GONE // Ẩn hoặc hiển thị dựa vào điều kiện cụ thể của bạn
    }

    override fun onPause() {
        super.onPause()

        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav?.visibility = View.VISIBLE // Đảm bảo nó được hiển thị trở lại khi rời khỏi Fragment (nếu cần)
    }
    private fun showConfirmationDialog(coupon: Coupon) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.delete_question))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                viewModel.deleteCoupon(coupon)
            }
            .show()
    }
}