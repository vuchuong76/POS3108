package com.example.pos1.order

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater

import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pos1.MainActivity

import com.example.pos1.UserApplication
import com.example.pos1.databinding.FragmentChooseTableBinding
import com.example.pos1.order.adapter.ChooseTableAdapter
import com.example.pos1.table.TableViewModel
import com.example.pos1.table.TableViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder


//staff chọn bàn ăn
@Suppress("DEPRECATION")
class ChooseTableFragment : Fragment() {
    private val viewModel: TableViewModel by activityViewModels {
        TableViewModelFactory(
            (activity?.application as UserApplication).orderDatabase.tableDao()
        )
    }
    private val sharedViewModel: OrderViewModel by activityViewModels {
        OrderViewModelFactory(
            (activity?.application as UserApplication).orderDatabase.orderDao(),
            (activity?.application as UserApplication).orderDatabase.itemDao(),
            (activity?.application as UserApplication).orderDatabase.tableDao()
        )
    }

    private lateinit var binding: FragmentChooseTableBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChooseTableBinding.inflate(inflater, container, false)
        // Báo cho hệ thống rằng Fragment này có menu


        setHasOptionsMenu(true)
        return binding.root
    }
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        disableBackButton()
        val userName = sharedViewModel.staffName
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                com.example.pos1.R.id.logout -> {
                   logOutDialog()

                    true
                }
                com.example.pos1.R.id.history -> {
                    val action =
                        ChooseTableFragmentDirections.actionChooseTableFragmentToOrderListFragment()
                    findNavController().navigate(action)

                    true
                }
                else -> false
            }
        }
        binding.idLabelTextView.text = "Staff name: $userName"
        // Tạo adapter: Tạo một TableListAdapter và chuyển một lambda function vào constructor của adapter.
        // Lambda function này sẽ được gọi khi một mục trong danh sách bàn được nhấp vào.
        val adapter = ChooseTableAdapter { table->
            val action =
                ChooseTableFragmentDirections.actionChooseTableFragmentToMenuTabletFragment()
            this.findNavController().navigate(action)
            sharedViewModel.setSelectedTableNumber(table.number)
//            sharedViewModel.getOrdersByNumber()
            //gán số bàn


        }

        val layoutManager = GridLayoutManager(context, 4)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter

//đổi màu bàn nào có order
        sharedViewModel.orderForTable1.observe(this.viewLifecycleOwner) { items ->
            if (items.isNullOrEmpty()) {
                sharedViewModel.updateTableStatus(sharedViewModel.selectedTableNumber.value ?: 0, 0)
            } else {
                sharedViewModel.updateTableStatus(sharedViewModel.selectedTableNumber.value ?: 0, 1)
            }
        }
        // Dữ liệu danh sách bàn được quan sát từ ViewModel thông qua viewModel.allTables.observe.
        // Khi danh sách bàn thay đổi, adapter.submitList
        // được gọi để cập nhật danh sách hiển thị trên giao diện người dùng.
        viewModel.allTables.observe(this.viewLifecycleOwner) { items ->
            items.let {
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

