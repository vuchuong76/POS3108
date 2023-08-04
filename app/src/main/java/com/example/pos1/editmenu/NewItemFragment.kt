package com.example.pos1.editmenu

import android.R
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.pos1.UserApplication
import com.example.pos1.databinding.FragmentNewItemBinding
import com.example.pos1.entity.Item



class NewItemFragment : Fragment() {
    private val PICK_IMAGE: String = "PICK_IMAGE"
    private val viewModel: ItemViewModel by activityViewModels {
        ItemViewModelFactory(
            (activity?.application as UserApplication).orderDatabase.itemDao()
        )
    }
    lateinit var item: Item
    private val args: NewItemFragmentArgs by navArgs()
    private var _binding: FragmentNewItemBinding? = null
    private val binding get() = _binding!!
    private var imageSelected: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewItemBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }


    //Trong phương thức này, bạn lắng nghe sự thay đổi của LiveData để cập nhật thông
// tin bàn và gọi phương thức bind(table) để cập nhật giao diện người dùng với thông tin mới.
// Nếu id > 0, nghĩa là đang thực hiện cập nhật thông tin bàn, phương thức retrieveTable(id)
// sẽ được gọi để lấy thông tin bàn cần cập nhật.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Tạo mảng chứa các mục bạn muốn hiển thị trong Spinner
        val items = arrayOf("food", "drink", "appetizer")

// Tìm Spinner trong layout
        val spinner = binding.type

// Tạo ArrayAdapter sử dụng layout mặc định và dữ liệu cho Spinner
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, items)

// Đặt layout cho danh sách lựa chọn (dropdown list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

// Đặt ArrayAdapter cho Spinner
        spinner.adapter = adapter

// Đặt listener để xử lý sự kiện khi một mục được chọn
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                // Lấy mục được chọn
                val selectedItem = parent.getItemAtPosition(position).toString()
                // Cập nhật dữ liệu tương ứng
                // TODO: Xử lý tương ứng với việc chọn mục này
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // TODO: Xử lý trường hợp không có mục nào được chọn, nếu cần
            }
        }

        binding.back.setOnClickListener {
            val action= NewItemFragmentDirections.actionNewItemFragmentToMenuListFragment()
            findNavController().navigate(action)
        }
        val id = args.itemId
        if (id > 0) {
            viewModel.retrieveItem(id).observe(this.viewLifecycleOwner) { selectedItem ->
                item = selectedItem
                bind(item)
            }
        } else {
            binding.add.setOnClickListener {
                addNewItem()

            }

        }
        initControls()
    }

    private fun initControls() {
        binding.apply {
            btnSelectImage.setOnClickListener {
                pickImage()
            }
        }
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            result.data?.let { content ->
                imageSelected = content.data.toString()

                //load anh da chon len imageview
                Glide.with(this)
                    .load(imageSelected)
                    .into(binding.ivImage)
            }
        }
    }

    private fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        resultLauncher.launch(Intent.createChooser(intent, "Select Picture"))
    }


    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.name.text.toString(),
            binding.type.selectedItem.toString(),
            binding.stock.text.toString(),
            binding.price.text.toString(),
            imageSelected
        )
    }

    private fun bind(item: Item) {
        binding.apply {
            val position = (type.adapter as ArrayAdapter<String>).getPosition(item.type)
            type.setSelection(position)
            name.setText(item.name, TextView.BufferType.SPANNABLE)
            stock.setText(item.stock.toString(), TextView.BufferType.SPANNABLE)
            price.setText(item.price.toString(), TextView.BufferType.SPANNABLE)
            add.setOnClickListener { updateItem() }
        }
    }

    //Phương thức này được gọi khi người dùng nhấn nút thêm bàn mới.
// Nếu dữ liệu nhập vào hợp lệ, phương thức này gọi ViewModel để
// thêm bàn mới và sau đó điều hướng đến màn hình danh sách bàn.
    private fun addNewItem() {
        if (isEntryValid()) {
            viewModel.addNewItem(
                binding.name.text.toString(),
                binding.type.selectedItem.toString(),
                binding.stock.text.toString(),
                binding.price.text.toString(),
                imageSelected
            )
            val action = NewItemFragmentDirections.actionNewItemFragmentToMenuListFragment()
            findNavController().navigate(action)
        }
        else{
            Toast.makeText(context,"Invalid",Toast.LENGTH_SHORT).show()
        }
    }

    //Phương thức này được gọi khi người dùng nhấn nút cập nhật thông tin bàn.
// Nếu dữ liệu nhập vào hợp lệ, phương thức này gọi ViewModel để cập nhật
// thông tin bàn và sau đó điều hướng trở lại màn hình danh sách bàn
    private fun updateItem() {
        if (isEntryValid()) {
            viewModel.updateItem(
                this.args.itemId,
                this.binding.name.text.toString(),
                binding.type.selectedItem.toString(),
                this.binding.stock.text.toString(),
                this.binding.price.text.toString(),
                imageSelected
            )
            val action = NewItemFragmentDirections.actionNewItemFragmentToMenuListFragment()
            findNavController().navigate(action)
        }
        else{
            Toast.makeText(context,"Invalid",Toast.LENGTH_SHORT).show()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Hide keyboard.
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }

}
