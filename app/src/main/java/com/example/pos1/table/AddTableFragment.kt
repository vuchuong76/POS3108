package com.example.pos1.table

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
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
import org.mindrot.jbcrypt.BCrypt


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
            binding.editNumber.text.toString(),
            binding.editCapacity.text.toString()
        )
    }

    private fun bind(table: Table) {
        binding.apply {
            editNumber.setText(table.number.toString(), TextView.BufferType.SPANNABLE)
            editCapacity.setText(table.capacity.toString(), TextView.BufferType.SPANNABLE)
            buttonSave.setOnClickListener { updateTable() }
        }
    }
//Phương thức này được gọi khi người dùng nhấn nút thêm bàn mới.
// Nếu dữ liệu nhập vào hợp lệ, phương thức này gọi ViewModel để
// thêm bàn mới và sau đó điều hướng đến màn hình danh sách bàn.
//    private fun addNewTable() {
//        if (isEntryValid()) {
//            viewModel.addNewTable(
//                binding.editNumber.text.toString(),
//                binding.editCapacity.text.toString()
//            )
//            val action = AddTableFragmentDirections.actionAddTableFragmentToTableFragment()
//            findNavController().navigate(action)
//        }
//    else {Toast.makeText(context,"Invalid", Toast.LENGTH_SHORT).show()}
//    }

    private fun addNewTable() {
        if (isEntryValid()) {
            val numberInput = binding.editNumber.text.toString().toInt()

            viewModel.tableNumberExists(numberInput) { exists ->
                if (exists) {
                    Toast.makeText(context, "Table Number already exists!", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.addNewTable(
                        numberInput.toString(),
                        binding.editCapacity.text.toString()

                    )
                    val action = AddTableFragmentDirections.actionAddTableFragmentToTableFragment()
                    findNavController().navigate(action)
                }
            }
        } else {
            Toast.makeText(context, "Invalid input data", Toast.LENGTH_SHORT).show()
        }
    }

    //Phương thức này được gọi khi người dùng nhấn nút cập nhật thông tin bàn.
// Nếu dữ liệu nhập vào hợp lệ, phương thức này gọi ViewModel để cập nhật
// thông tin bàn và sau đó điều hướng trở lại màn hình danh sách bàn
    private fun updateTable() {
        if (isEntryValid()) {
            viewModel.updateTable(
                this.args.tableId.toString(),
                this.binding.editCapacity.text.toString(),
            )
            val action = AddTableFragmentDirections.actionAddTableFragmentToTableFragment()
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
            }
        } else {
            binding.buttonSave.setOnClickListener {
                addNewTable()
            }
        }
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.back -> {
                    val action = AddTableFragmentDirections.actionAddTableFragmentToTableFragment()
                    findNavController().navigate(action)
                    true
                }

                else -> false
            }
        }
    }
}
