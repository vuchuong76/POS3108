package com.example.pos1.table

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.pos1.R
import com.example.pos1.UserApplication
import com.example.pos1.databinding.FragmentTableDetailBinding
import com.example.pos1.entity.Table
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class TableDetailFragment : Fragment() {
    lateinit var table: Table
    private val args: TableDetailFragmentArgs by navArgs()
    private lateinit var binding: FragmentTableDetailBinding

    private val viewModel: TableViewModel by activityViewModels {
        TableViewModelFactory(
            (requireActivity().application as UserApplication).orderDatabase.tableDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTableDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun bind(table: Table) {
        binding.apply {
            tableNumber.text = table.number.toString()
            capacity.text = table.capacity.toString()
            deleteItem.setOnClickListener { showConfirmationDialog() }
            buttonEdit.setOnClickListener { editTable() }
        }
    }
    private fun editTable() {
        val action = TableDetailFragmentDirections.actionTableDetailFragmentToAddTableFragment(
            getString(R.string.edit_user),
            table.number
        )
        this.findNavController().navigate(action)
    }
    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage("Do you want to delete this table?")
            .setCancelable(false)
            .setNegativeButton("No") { _, _ -> }
            .setPositiveButton("Yes") { _, _ ->
                deleteTable()
            }
            .show()
    }

    private fun deleteTable() {
        viewModel.deleteTable(table)
        findNavController().navigateUp()
    }
    // Trong phương thức này, bạn lắng nghe sự thay đổi của LiveData
    // để cập nhật thông tin bàn và gọi phương thức bind(table) để cập nhật giao diện người dùng với thông tin mới.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = args.tableId
        viewModel.retrieveTable(id).observe(this.viewLifecycleOwner) { selectedTable ->
            table = selectedTable
            bind(table)
        }
        binding.toolbar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.back -> {
                    val action = TableDetailFragmentDirections.actionTableDetailFragmentToTableFragment()
                    findNavController().navigate(action)
                    true
                }
                else -> false
            }}
    }

}