package com.example.pos1.table

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pos1.MainActivity
import com.example.pos1.R
import com.example.pos1.UserApplication
import com.example.pos1.databinding.FragmentTableBinding
import com.example.pos1.entity.Table
import com.example.pos1.order.OrderViewModel
import com.example.pos1.order.OrderViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder

@Suppress("DEPRECATION")
class TableFragment : Fragment() {
    private val viewModel: TableViewModel by activityViewModels {
        TableViewModelFactory(
            (activity?.application as UserApplication).orderDatabase.tableDao()
        )
    }

    private lateinit var binding: FragmentTableBinding
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
        val adapter = TableListAdapter { table ->
            showConfirmationDialog(table)
        }
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
            val action = TableFragmentDirections.actionTableToAddTableFragment(
            )
            this.findNavController().navigate(action)
        }

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.logout -> {
                   logOutDialog()
                    true
                }
                else -> false
            }
        }
    }

    private fun showConfirmationDialog(table: Table) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage("Do you want to delete this Table?")
            .setCancelable(false)
            .setNegativeButton("No") { _, _ -> }
            .setPositiveButton("Yes") { _, _ ->
                deleteTable(table)
            }
            .show()
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
    private fun deleteTable(table: Table) {
        if (table.status == 1) {
            Toast.makeText(context, "This table is currently in use", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.deleteTable(table)
        }


    }
}