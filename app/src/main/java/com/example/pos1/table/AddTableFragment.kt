package com.example.pos1.table

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.pos1.R
import com.example.pos1.UserApplication
import com.example.pos1.databinding.FragmentAddTableBinding
import com.example.pos1.entity.Table
import com.google.android.material.bottomnavigation.BottomNavigationView


@Suppress("DEPRECATION")
class AddTableFragment : Fragment() {

    lateinit var table: Table
    private val args: AddTableFragmentArgs by navArgs()
    private val viewModel: TableViewModel by activityViewModels {
        TableViewModelFactory(
            (activity?.application as UserApplication).orderDatabase.tableDao()
        )
    }

    private lateinit var binding: FragmentAddTableBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddTableBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }


    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.editNumber,
            binding.editCapacity
        )
    }

    private fun bind(table: Table) {
        binding.apply {
            editNumber.setText(table.number.toString(), TextView.BufferType.SPANNABLE)
            editCapacity.setText(table.capacity.toString(), TextView.BufferType.SPANNABLE)
            buttonSave.setOnClickListener { updateTable() }
        }
    }
    private fun addNewTable() {
        if (isEntryValid()) {
            val numberInput = binding.editNumber.text.toString().toInt()

            viewModel.tableNumberExists(numberInput) { exists ->
                if (exists) {
                    binding.editNumber.error="This number is already exist"
                } else {
                    viewModel.addNewTable(
                        numberInput.toString(),
                        binding.editCapacity.text.toString()

                    )
                    val action = AddTableFragmentDirections.actionAddTableFragmentToTable()
                    findNavController().navigate(action)
                }
            }
        } else {
            Toast.makeText(context, "Invalid input data", Toast.LENGTH_SHORT).show()
        }
    }


    private fun updateTable() {
        if (isEntryValid()) {
            viewModel.updateTable(
                this.args.tableId.toString(),
                this.binding.editCapacity.text.toString(),
            )
            val action = AddTableFragmentDirections.actionAddTableFragmentToTable()
            findNavController().navigate(action)
        } else {
            Toast.makeText(context, "Invalid", Toast.LENGTH_SHORT).show()
        }
    }

    //Trong phương thức này, bạn lắng nghe sự thay đổi của LiveData để cập nhật thông
// tin bàn và gọi phương thức bind(table) để cập nhật giao diện người dùng với thông tin mới.
// Nếu id > 0, nghĩa là đang thực hiện cập nhật thông tin bàn, phương thức retrieveTable(id)
// sẽ được gọi để lấy thông tin bàn cần cập nhật.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = args.tableId
        if (id > 0) {
            viewModel.retrieveTable(id).observe(this.viewLifecycleOwner) { selectedTable ->
                table = selectedTable
                bind(table)
                binding.toolbar.title="Edit Table"
                binding.editNumber.isEnabled=false
            }
        } else {
            binding.buttonSave.setOnClickListener {
                addNewTable()
                binding.editNumber.isEnabled=true
            }
        }

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.back -> {
                    findNavController().navigateUp()
                    true
                }

                else -> false
            }
        }
    }
    override fun onResume() {
        super.onResume()

        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav?.visibility = View.GONE // Ẩn hoặc hiển thị dựa vào điều kiện cụ thể của bạn
    }

    override fun onPause() {
        super.onPause()

        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav?.visibility = View.VISIBLE // Đảm bảo nó được hiển thị trở lại khi rời khỏi Fragment (nếu cần)
    }
}
