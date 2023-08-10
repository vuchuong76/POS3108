package com.example.pos1.orlist

import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pos1.R
import com.example.pos1.UserApplication
import com.example.pos1.databinding.FragmentOrderListBinding
import com.example.pos1.order.OrderViewModel
import com.example.pos1.order.OrderViewModelFactory

class OrderListFragment : Fragment() {
    private val orderViewModel1: OrderlistViewModel by activityViewModels() {
        OrderListViewModelFactory(
            (activity?.application as UserApplication).orderDatabase.orderlistDao(),
            (activity?.application as UserApplication).orderDatabase.orderDao()
        )
    }
    private val sharedViewModel: OrderViewModel by activityViewModels {
        OrderViewModelFactory(
            (activity?.application as UserApplication).orderDatabase.orderDao(),
            (activity?.application as UserApplication).orderDatabase.itemDao(),
            (activity?.application as UserApplication).orderDatabase.tableDao()
        )
    }
    private lateinit var binding: FragmentOrderListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Gắn layout cho fragment này bằng cách sử dụng binding class được tạo ra
        binding = FragmentOrderListBinding.inflate(inflater, container, false)


        // Báo cho hệ thống rằng Fragment này có menu
        setHasOptionsMenu(true)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = OrderlistAdapter { order ->
            sharedViewModel.setSelectedId(order.orId)
            val action = OrderListFragmentDirections.actionOrderListFragmentToDetailFragment()
            findNavController().navigate(action)
        }
        binding.recyclerView1.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView1.adapter = adapter
//quay trở về màn hine Choose Table
        val back: Button = binding.buttonHome1
        back.setOnClickListener {
            val action = OrderListFragmentDirections.actionOrderListFragmentToChooseTableFragment2()
            findNavController().navigate(action)
        }

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.home -> {
                    val action =
                        OrderListFragmentDirections.actionOrderListFragmentToChooseTableFragment2()
                    findNavController().navigate(action)
                    true
                }


                else -> false
            }
        }

        // Theo dõi LiveData allItems từ OrderlistViewModel để tự động cập nhật giao diện
        orderViewModel1.allItems1.observe(this.viewLifecycleOwner) { items ->
            items.let {
                adapter.submitList(it)
            }
        }
    }


}