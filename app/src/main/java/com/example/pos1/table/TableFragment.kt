package com.example.pos1.table

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pos1.R
import com.example.pos1.UserApplication
import com.example.pos1.databinding.FragmentTableBinding

@Suppress("DEPRECATION")
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
    ): View {

        binding = FragmentTableBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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