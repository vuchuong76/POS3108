package com.example.pos1.table

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pos1.R
import com.example.pos1.UserApplication
import com.example.pos1.databinding.FragmentTableBinding

class TableFragment : Fragment() {
    private val viewModel: TableViewModel by activityViewModels {
        TableViewModelFactory(
            (activity?.application as UserApplication).orderDatabase.tableDao()
        )
    }
    private lateinit  var binding: FragmentTableBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentTableBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    private fun setActionBarTitle(title: String) {
        (activity as? AppCompatActivity)?.supportActionBar?.title = title
    }

    class ItemSpacingDecoration(private val spacing: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            outRect.apply {
                // Đặt khoảng cách cho top, bottom, left, right của mỗi mục
                top = spacing
                bottom = spacing
                left = spacing
                right = spacing
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    //Tạo adapter: Tạo một TableListAdapter và chuyển một lambda function vào constructor của adapter.
    // Lambda function này sẽ được gọi khi một mục trong danh sách bàn được nhấp vào.
        val adapter = TableListAdapter {
            val action =
                TableFragmentDirections.actionTableFragmentToTableDetailFragment(it.number)
            this.findNavController().navigate(action)
        }
        binding.toolbar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.home -> {
                    val action = TableFragmentDirections.actionTableFragmentToAdminAccessFragment()
                    findNavController().navigate(action)
                    true
                }
                else -> false
            }}
      //  Thiết lập RecyclerView: Đặt LayoutManager của RecyclerView là LinearLayoutManager và thiết lập adapter cho RecyclerView.
        val layoutManager = GridLayoutManager(context, 2)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter

        val spacing = resources.getDimensionPixelSize(R.dimen.item_spacing) // Khoảng cách giữa các mục (thay thế bằng kích thước mong muốn)
        val itemSpacingDecoration = ItemSpacingDecoration(spacing)
        binding.recyclerView.addItemDecoration(itemSpacingDecoration)

        //Dữ liệu danh sách bàn được quan sát từ ViewModel thông qua viewModel.allTables.observe.
        // Khi danh sách bàn thay đổi, adapter.submitList
        // được gọi để cập nhật danh sách hiển thị trên giao diện người dùng.
        viewModel.allTables.observe(this.viewLifecycleOwner) { items ->
            items.let {
                adapter.submitList(it)
            }
        }
        binding.fab.setOnClickListener {
            val action = TableFragmentDirections.actionTableFragmentToAddTableFragment(
                "Add Item"
            )
            this.findNavController().navigate(action)
        }
    }

}