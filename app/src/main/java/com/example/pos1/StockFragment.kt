package com.example.pos1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pos1.databinding.FragmentMenuListBinding
import com.example.pos1.databinding.FragmentStockBinding
import com.example.pos1.databinding.StockItemBinding
import com.example.pos1.editmenu.ItemListAdapter
import com.example.pos1.editmenu.ItemViewModel
import com.example.pos1.editmenu.ItemViewModelFactory
import com.example.pos1.editmenu.MenuListFragmentDirections
import com.example.pos1.entity.Item


class StockFragment : Fragment() {
    private val viewModel: ItemViewModel by activityViewModels {
        ItemViewModelFactory(
            (activity?.application as UserApplication).orderDatabase.itemDao()
        )
    }
    private lateinit var binding: FragmentStockBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStockBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }
    fun showQuantityDialog(item: Item) {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_edittext, null)
        val editText  = dialogLayout.findViewById<EditText>(R.id.editText)

        builder.setView(dialogLayout)
        builder.setTitle("Nhập số lượng")
        builder.setPositiveButton("OK") { _, _ ->
            val quantity = editText.text.toString().toIntOrNull()
            if (quantity != null) {
                // Xử lý giá trị nhập vào, ví dụ cập nhật trong cơ sở dữ liệu hoặc gì đó khác.
            }
        }
        builder.setNegativeButton("Cancel") { _, _ -> }
        builder.show()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Tạo adapter: Tạo một TableListAdapter và chuyển một lambda function vào constructor của adapter.
        // Lambda function này sẽ được gọi khi một mục trong danh sách bàn được nhấp vào.
        val adapter = StockAdapter(object: StockItemActionListener {
            override fun onAddButtonClick(item: Item) {
                showQuantityDialog(item)
            }

            override fun onMinusButtonClick(item: Item) {
                // Xử lý khi nút Minus được nhấn
            }

            override fun onItemClicked(item: Item) {
                // Xử lý khi item được nhấn
            }
        })
        //  Thiết lập RecyclerView: Đặt LayoutManager của RecyclerView là LinearLayoutManager và thiết lập adapter cho RecyclerView.
        val layoutManager = GridLayoutManager(context, 2)
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
                        StockFragmentDirections.actionStockFragmentToKitchenFragment()
                    findNavController().navigate(action)
                    true
                }

                else -> false
            }
        }
    }
}