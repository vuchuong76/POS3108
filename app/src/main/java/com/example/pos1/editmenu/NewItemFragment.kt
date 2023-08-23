package com.example.pos1.editmenu

//noinspection SuspiciousImport
import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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


@Suppress("DEPRECATION")
class NewItemFragment : Fragment() {
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Tạo mảng chứa các mục bạn muốn hiển thị trong Spinner
        val items = arrayOf("Food", "Drink", "Appetizer")
// Tìm Spinner trong layout
        val spinner = binding.type
// Tạo ArrayAdapter sử dụng layout mặc định và dữ liệu cho Spinner
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, items)
// Đặt layout cho danh sách lựa chọn (dropdown list)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
// Đặt ArrayAdapter cho Spinner
        spinner.adapter = adapter
// Đặt listener để xử lý sự kiện khi một mục được chọn
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                view?.let {
//                    // Lấy mục được chọn
//                    val selectedItem = parent.getItemAtPosition(position).toString()
//                    // Cập nhật dữ liệu tương ứng
                    // TODO: Xử lý tương ứng với việc chọn mục này
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // TODO: Xử lý trường hợp không có mục nào được chọn, nếu cần
            }
        }

        binding.back.setOnClickListener {
            val action = NewItemFragmentDirections.actionNewItemFragmentToMenuListFragment()
            findNavController().navigate(action)
        }
        val id = args.itemId
        if (id > 0) {
            viewModel.retrieveItem(id).observe(this.viewLifecycleOwner) { selectedItem ->
                item = selectedItem
                binding.toolbar.title = "Editing"
                bind(item)
            }
        } else {
            binding.save.setOnClickListener {
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

    //hàm yêu cầu 1 hình ảnh
    private fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        resultLauncher.launch(Intent.createChooser(intent, "Select Picture"))
    }


    //xử lý kết quả trả về từ 1 intent
    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            //xem kết quả trả về từ Activity có phải là RESULT_OK hay không tức là ảnh được pick ok
            if (result.resultCode == Activity.RESULT_OK) {
                //ktra kết quả bên trong có null không, nếu không null thì thực hiện content->
                result.data?.let { content ->
                    imageSelected = content.data.toString()

                    //load anh da chon len imageview
                    Glide.with(this)
                        .load(imageSelected)
                        .into(binding.ivImage)
                }
            }
        }


    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.name,
            binding.type.selectedItem.toString(),
            binding.stock,
            binding.price,
            imageSelected,
            requireContext()
        )
    }

    @SuppressLint("SuspiciousIndentation")
    private fun bind(item: Item) {
        binding.apply {
            val position = (type.adapter as ArrayAdapter<String>).getPosition(item.type)
            type.setSelection(position)
            name.setText(item.name, TextView.BufferType.SPANNABLE)
            stock.setText(item.stock.toString(), TextView.BufferType.SPANNABLE)
            price.setText(item.price.toString(), TextView.BufferType.SPANNABLE)
            imageSelected = item.image
            Glide.with(requireContext())
                .load(item.image)
                .into(binding.ivImage)

            Log.d("testthu1", imageSelected)


            save.setOnClickListener { updateItem() }
        }
    }

    private fun addNewItem() {
        if (isEntryValid()) {
            val nameInput = binding.name.text.toString()
            val roundedPrice = roundToOneDecimalPlace(binding.price.text.toString().toFloat())
            viewModel.itemNameExist(nameInput) { exist ->
                if (!exist) {
                    viewModel.addNewItem(
                        nameInput,
                        binding.type.selectedItem.toString(),
                        binding.stock.text.toString(),
                        roundedPrice.toString(),
                        imageSelected
                    )
                    val action = NewItemFragmentDirections.actionNewItemFragmentToMenuListFragment()
                    findNavController().navigate(action)
                } else {
                    Toast.makeText(context, "Item Name is already exist", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
        }
    }

    private fun updateItem() {
        if (isEntryValid()) {
            val roundedPrice = roundToOneDecimalPlace(binding.price.text.toString().toFloat())
            viewModel.updateItem(
                args.itemId,
                binding.name.text.toString(),
                binding.type.selectedItem.toString(),
                binding.stock.text.toString(),
                roundedPrice.toString(),
                imageSelected

            )
            Log.d("testthu2", binding.ivImage.toString())
            val action = NewItemFragmentDirections.actionNewItemFragmentToMenuListFragment()
            findNavController().navigate(action)
        } else {
        }

    }

    //hàm chuyển sang float có 1 chữ số sau dấu phẩy
    fun roundToOneDecimalPlace(value: Float): Float {
        return (value * 10).toInt().toFloat() / 10
    }


    override fun onDestroyView() {
        super.onDestroyView()
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }

}
