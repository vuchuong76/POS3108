package com.example.pos1.kitchen

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pos1.MainActivity
import com.example.pos1.R
import com.example.pos1.UserApplication
import com.example.pos1.databinding.FragmentKitchenBinding
import com.example.pos1.order.OrderViewModel
import com.example.pos1.order.OrderViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder

@Suppress("DEPRECATION")
class KitchenFragment : Fragment() {
    // Lấy view model chung sử dụng activityViewModels và OrderViewModelFactory
    private val sharedViewModel: OrderViewModel by activityViewModels() {
        OrderViewModelFactory(
            (activity?.application as UserApplication).orderDatabase.orderDao(),
            (activity?.application as UserApplication).orderDatabase.itemDao(),
            (activity?.application as UserApplication).orderDatabase.tableDao()
        )
    }
    private lateinit var binding: FragmentKitchenBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Gắn layout cho fragment này bằng cách sử dụng binding class được tạo ra
        binding = FragmentKitchenBinding.inflate(inflater, container, false)

        // Báo cho hệ thống rằng Fragment này có menu
        setHasOptionsMenu(true)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        disableBackButton()
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {

                R.id.logout -> {
                    logOutDialog()
                    true
                }
                R.id.stock -> {
                    val action = KitchenFragmentDirections.actionKitchenFragmentToStockFragment()
                    findNavController().navigate(action)
                    true
                }
                else -> false
            }
        }



        // Khởi tạo OrderAdapter và đặt làm adapter cho RecyclerView
        val adapter = KitchenAdapter(
            onItemClicked = { order ->

            },
            onButtonClicked = { order ->
                sharedViewModel.serve(order)
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView.adapter = adapter


        sharedViewModel.getOrderForKitchen().observe(viewLifecycleOwner) { items ->
            items?.let {
                adapter.submitList(it)
            }
        }
    }
    private fun logOutDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage("Do you really want to log out?")
            .setCancelable(false)
            .setNegativeButton("No") { _, _ -> }
            .setPositiveButton("Yes") { _, _ ->
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
            }
            .show()
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
