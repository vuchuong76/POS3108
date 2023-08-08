package com.example.pos1.User

import android.os.Bundle
import android.text.Editable
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.pos1.R
import com.example.pos1.UserApplication
import com.example.pos1.databinding.FragmentAddStaffBinding
import com.example.pos1.entity.User
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
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View { binding = FragmentAddStaffBinding.inflate(inflater, container, false)
        binding.position.text = Editable.Factory.getInstance().newEditable(selectedPosition)

        // Register Context Menu for Position TextView
        registerForContextMenu(binding.position)
        binding.position.setOnClickListener {
            activity?.openContextMenu(it)
        }
        return binding.root
    }


    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu.setHeaderTitle("Select Position")
        menu.add(0, v.id, 0, "Admin")
        menu.add(0, v.id, 0, "Staff")
        menu.add(0, v.id, 0, "Kitchen") // thêm lựa chọn "Kitchen"
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        selectedPosition = when (item.title) {
            "Admin" -> "Admin"
            "Staff" -> "Staff"
            "Kitchen" -> "Kitchen"
            else -> "Admin"
        }
        // Update position TextView with selectedPosition
        binding.position.text = Editable.Factory.getInstance().newEditable(selectedPosition)
        return true
    }


    //kiểm tra dữ liệu có hợp lệ không
    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.staffId,
            binding.passWord,
            binding.staffName,
            binding.staffAge,
            binding.position.text.toString(),
            binding.tel,
            binding.address
        )
    }

    //Phương thức này được sử dụng để gán dữ liệu từ một đối tượng User
// vào các trường nhập liệu trong layout (binding).
// Nó sử dụng Editable.Factory.getInstance().newEditable()
// để tạo một Editable mới từ các giá trị thuộc tính của đối tượng User.
// Sau đó, các Editable này được gán vào các trường nhập liệu tương ứng trong layout.
// Cuối cùng, nút "addBt1" được đặt lắng nghe sự kiện nhấp và gọi phương thức updateUser().
    private fun bind(user: User) {
        binding.apply {
            staffId.text = Editable.Factory.getInstance().newEditable(user.staffId.toString())
            passWord.text = Editable.Factory.getInstance().newEditable(user.password)
            staffName.text = Editable.Factory.getInstance().newEditable(user.staffname)
            staffAge.text = Editable.Factory.getInstance().newEditable(user.age.toString())
            position.text = Editable.Factory.getInstance().newEditable(user.position)
            tel.text = Editable.Factory.getInstance().newEditable(user.tel.toString())
            address.text = Editable.Factory.getInstance().newEditable(user.address)
            addBt1.setOnClickListener { updateUser() }
        }
    }

    private fun addNewUser() {
        if (isEntryValid()) {
            val staffIdInput = binding.staffId.text.toString().toInt()

            viewModel.staffIdExists(staffIdInput) { exists ->
                if (exists) {
                    Toast.makeText(context, "Staff ID already exists!", Toast.LENGTH_SHORT).show()
                } else {
                    val hashedPassword = BCrypt.hashpw(binding.passWord.text.toString(), BCrypt.gensalt())
                    viewModel.addNewUser(
                        staffIdInput,
                        hashedPassword, // Mật khẩu đã được mã hóa
                        binding.staffName.text.toString(),
                        binding.staffAge.text.toString().toInt(),
                        binding.position.text.toString(),
                        binding.tel.text.toString(),
                        binding.address.text.toString(),
                    )
                    findNavController().navigate(com.example.pos1.R.id.action_addStaffFragment_to_staffListFragment)
                }
            }
        } else {
            Toast.makeText(context,"Invalid input data",Toast.LENGTH_SHORT).show()
        }
    }



    private fun updateUser() {
        if (isEntryValid()) {
            val hashedPassword = BCrypt.hashpw(binding.passWord.text.toString(), BCrypt.gensalt())
            viewModel.updateUser(
                this.args.staffId,
                hashedPassword, // Mật khẩu đã được mã hóa
                this.binding.staffName.text.toString(),
                this.binding.staffAge.text.toString(),
                this.binding.position.text.toString(),
                this.binding.tel.text.toString(),
                this.binding.address.text.toString()
            )
            val action = AddStaffFragmentDirections.actionAddStaffFragmentToStaffListFragment()
            findNavController().navigate(action)
        }
        else{
            Toast.makeText(context,"Invalid input data",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = args.staffId
        if (id > 0) {
            viewModel.retrieveItem(id).observe(this.viewLifecycleOwner) { selectedItem ->
                user = selectedItem
                bind(user)
            }
        } else {
            binding.addBt1.setOnClickListener {
                addNewUser()
            }
        }

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.back -> {
                    val action= AddStaffFragmentDirections.actionAddStaffFragmentToStaffListFragment()
                    findNavController().navigate(action)
                    true
                }

                else -> false
            }
        }

        binding.position.setOnClickListener {
            activity?.openContextMenu(it)
        }
        // Register Context Menu for Position Button
        registerForContextMenu(binding.position)
    }
}

