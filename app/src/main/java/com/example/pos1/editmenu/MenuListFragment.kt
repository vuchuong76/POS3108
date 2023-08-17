package com.example.pos1.editmenu

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
import com.example.pos1.databinding.FragmentMenuListBinding

class MenuListFragment : Fragment() {
    private val viewModel: ItemViewModel by activityViewModels {
        ItemViewModelFactory(
            (activity?.application as UserApplication).orderDatabase.itemDao()
        )
    }
    private lateinit var binding: FragmentMenuListBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMenuListBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Tạo adapter: Tạo một TableListAdapter và chuyển một lambda function vào constructor của adapter.
        // Lambda function này sẽ được gọi khi một mục trong danh sách bàn được nhấp vào.
        val adapter = ItemListAdapter {
            val action =
                MenuListFragmentDirections.actionMenuListFragmentToItemDetailFragment4(it.id)
            this.findNavController().navigate(action)
        }
        //  Thiết lập RecyclerView: Đặt LayoutManager của RecyclerView là LinearLayoutManager và thiết lập adapter cho RecyclerView.
        val layoutManager = GridLayoutManager(context, 1)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter

        //Dữ liệu danh sách bàn được quan sát từ ViewModel thông qua viewModel.allTables.observe.
        // Khi danh sách bàn thay đổi, adapter.submitList
        // được gọi để cập nhật danh sách hiển thị trên giao diện người dùng.
        viewModel.allItems.observe(this.viewLifecycleOwner) { items ->
            items.let {
                adapter.submitList(it)
            }
        }
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.home -> {
                    val action =
                        MenuListFragmentDirections.actionMenuListFragmentToAdminAccessFragment()
                    findNavController().navigate(action)
                    true
                }

                else -> false
            }
        }
        binding.floatingActionButton.setOnClickListener {
            val action = MenuListFragmentDirections.actionMenuListFragmentToNewItemFragment(
                "Add Item"
            )
            this.findNavController().navigate(action)
        }
    }
}