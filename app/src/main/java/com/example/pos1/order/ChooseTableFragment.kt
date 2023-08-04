package com.example.pos1.order

import android.os.Bundle
import android.view.LayoutInflater

import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager

import com.example.pos1.UserApplication
import com.example.pos1.databinding.FragmentChooseTableBinding
import com.example.pos1.table.TableListAdapter
import com.example.pos1.table.TableViewModel
import com.example.pos1.table.TableViewModelFactory


//staff chọn bàn ăn
class ChooseTableFragment : Fragment() {
    private val viewModel: TableViewModel by activityViewModels {
        TableViewModelFactory(
            (activity?.application as UserApplication).orderDatabase.tableDao()
        )
    }
    private val sharedViewModel: OrderViewModel by activityViewModels {
        OrderViewModelFactory(
            (activity?.application as UserApplication).orderDatabase.orderDao()
        )
    }

    private lateinit var binding: FragmentChooseTableBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChooseTableBinding.inflate(inflater, container, false)
        // Báo cho hệ thống rằng Fragment này có menu


        setHasOptionsMenu(true)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val staffId = sharedViewModel.id
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                // these ids should match the item ids from my_fragment_menu.xml file
                com.example.pos1.R.id.logout -> {
                    val action =
                        ChooseTableFragmentDirections.actionChooseTableFragmentToLoginFragment2()
                    findNavController().navigate(action)

                    // by returning 'true' we're saying that the event
                    // is handled and it shouldn't be propagated further
                    true
                }
                else -> false
            }
        }
        binding.idLabelTextView.text = "ID: $staffId"
        // Tạo adapter: Tạo một TableListAdapter và chuyển một lambda function vào constructor của adapter.
        // Lambda function này sẽ được gọi khi một mục trong danh sách bàn được nhấp vào.
        val adapter = ChooseTableAdapter { table->
            val action =
                ChooseTableFragmentDirections.actionChooseTableFragmentToTabletFragment()
            this.findNavController().navigate(action)
            sharedViewModel.setSelectedTableNumber(table.number)
//            sharedViewModel.getOrdersByNumber()
            //gán số bàn


        }

        binding.orderList.setOnClickListener {
            val action =
                ChooseTableFragmentDirections.actionChooseTableFragmentToOrderListFragment()
            findNavController().navigate(action) }
        val layoutManager = GridLayoutManager(context, 4)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter

        // Dữ liệu danh sách bàn được quan sát từ ViewModel thông qua viewModel.allTables.observe.
        // Khi danh sách bàn thay đổi, adapter.submitList
        // được gọi để cập nhật danh sách hiển thị trên giao diện người dùng.
        viewModel.allTables.observe(this.viewLifecycleOwner) { items ->
            items.let {
                adapter.submitList(it)
            }
        }
    }



}
