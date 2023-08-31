package com.example.pos1.User

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.pos1.R
import com.example.pos1.UserApplication
import com.example.pos1.databinding.FragmentAddStaffBinding
import com.example.pos1.entity.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.mindrot.jbcrypt.BCrypt

class AddStaffFragment : Fragment() {
    lateinit var user: User
    private val args: AddStaffFragmentArgs by navArgs()
    private val viewModel: UserViewModel by activityViewModels {
        UserViewModel.UserViewModelFactory(
            (activity?.application as UserApplication).orderDatabase
                .userDao()
        )
    }
    private lateinit var binding: FragmentAddStaffBinding
    private var selectedPosition: String = "Admin"
    private val positions = arrayOf("Admin", "Staff", "Kitchen")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddStaffBinding.inflate(inflater, container, false)
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, positions)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.position.adapter = spinnerAdapter
        binding.position.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedPosition = positions[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
        return binding.root
    }

    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.userName,
            binding.passWord,
            binding.staffName,
            binding.staffAge,
            binding.position.selectedItem.toString(),
            binding.tel,
            binding.address
        )
    }
    private fun blank(): Boolean {
        return viewModel.blank(
            binding.userName,
            binding.passWord,
            binding.staffName,
            binding.staffAge,
            binding.position.selectedItem.toString(),
            binding.tel,
            binding.address,
            requireContext()
        )
    }

    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage("You are entering information, do you really want to exit?")
            .setCancelable(false)
            .setNegativeButton("No") { _, _ -> }
            .setPositiveButton("Yes") { _, _ ->
                val action =
                    AddStaffFragmentDirections.actionAddStaffFragmentToStaff()
                findNavController().navigate(action)
            }
            .show()
    }

    private fun bind(user: User) {
        binding.apply {
            userName.text = Editable.Factory.getInstance().newEditable(user.userName)
            passWord.text = Editable.Factory.getInstance().newEditable(user.password)
            staffName.text = Editable.Factory.getInstance().newEditable(user.staffname)
            staffAge.text = Editable.Factory.getInstance().newEditable(user.age.toString())
            tel.text = Editable.Factory.getInstance().newEditable(user.tel)
            address.text = Editable.Factory.getInstance().newEditable(user.address)
            val positionIndex = positions.indexOf(user.position)
            position.setSelection(positionIndex)
            addBt1.setOnClickListener {
                val nameInput = user.userName
                val name= binding.userName.text.toString()
                viewModel.userNameExists(name) { exist ->
                    if (!exist||name==nameInput) {
                        updateUser()
                    }
                    else{

                        binding.userName.setError("This name is already exist"
                        )
                    }
                }
            }
        }
    }

    private fun addNewUser() {
        if (isEntryValid()) {
            val userNameInput = binding.userName.text.toString()

            viewModel.userNameExists(userNameInput) { exists ->
                if (exists) {
                    binding.userName.error = "UserName is already exists!"
                } else {
                    val hashedPassword = BCrypt.hashpw(binding.passWord.text.toString(), BCrypt.gensalt())
                    viewModel.addNewUser(
                        userNameInput,
                        hashedPassword,
                        binding.staffName.text.toString(),
                        binding.staffAge.text.toString().toInt(),
                        binding.position.selectedItem.toString(),
                        binding.tel.text.toString(),
                        binding.address.text.toString(),
                    )
                    findNavController().navigateUp()
                }
            }
        } else {
            Toast.makeText(context, "Input data is not valid", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUser() {
        if (isEntryValid()) {
            //gensalt là phần đc thêm vào trước 1 chuỗi,giúp cho mật khẩu đặt giống nhau cũng có mã hàm bưm khác nhau
            val hashedPassword = BCrypt.hashpw(binding.passWord.text.toString(), BCrypt.gensalt())
            viewModel.updateUser(
                args.userName,
                hashedPassword,
                binding.staffName.text.toString(),
                binding.staffAge.text.toString(),
                binding.position.selectedItem.toString(),
                binding.tel.text.toString(),
                binding.address.text.toString()
            )
            val action = AddStaffFragmentDirections.actionAddStaffFragmentToStaff()
            findNavController().navigate(action)
        } else {
            Toast.makeText(context, "Input data is not valid", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = args.userName
        if (id.isNotEmpty()) {
            viewModel.retrieveItem(id).observe(this.viewLifecycleOwner) { selectedItem ->
                user = selectedItem
                bind(user)
                binding.toolbar.title="User Edit"
//                binding.userName.isEnabled = false
            }
        } else {
            binding.addBt1.setOnClickListener {
//                binding.userName.isEnabled = true
                addNewUser()
            }
        }

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.back -> {
                   if (!blank()) {
                        showConfirmationDialog()
                    }
                    else{
                       val action =
                           AddStaffFragmentDirections.actionAddStaffFragmentToStaff()
                       findNavController().navigate(action)
                    }
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
