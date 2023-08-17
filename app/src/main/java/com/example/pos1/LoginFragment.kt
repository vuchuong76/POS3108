package com.example.pos1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.pos1.User.UserViewModel
import com.example.pos1.order.OrderViewModel
import com.example.pos1.order.OrderViewModelFactory
import kotlinx.coroutines.*
import org.mindrot.jbcrypt.BCrypt

class LoginFragment : Fragment() {
    // Khai báo các phần tử giao diện
    private lateinit var idEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button

    // Khởi tạo ViewModel được chia sẻ giữa các Fragment
    private val sharedViewModel: OrderViewModel by activityViewModels() {
        OrderViewModelFactory(
            (activity?.application as UserApplication).orderDatabase.orderDao(),
            (activity?.application as UserApplication).orderDatabase.itemDao(),
            (activity?.application as UserApplication).orderDatabase.tableDao()
        )
    }
    private val userViewModel: UserViewModel by activityViewModels() {
        UserViewModel.UserViewModelFactory(
            (activity?.application as UserApplication).orderDatabase.userDao()
        )
    }

    // CoroutineScope để xử lý đa luồng
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Gắn layout cho Fragment này
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        // Khởi tạo các phần tử giao diện
        idEditText = view.findViewById(R.id.id)
        passwordEditText = view.findViewById(R.id.pw)
        loginButton = view.findViewById(R.id.loginbt)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        disableBackButton()
        // Đặt lắng nghe sự kiện nhấp vào nút đăng nhập
        loginButton.setOnClickListener {
            // Lấy ID và mật khẩu đã nhập
            val id = idEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Gọi hàm kiểm tra đăng nhập
            loginUser(id, password)
        }
    }

    // Hàm xử lý quá trình đăng nhập
    private fun loginUser(id: String, password: String) {
        // Đặt ID vào ViewModel được chia sẻ


        // Kiểm tra nếu ID hoặc mật khẩu rỗng
        if (id.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Your ID or Password is empty", Toast.LENGTH_SHORT)
                .show()
            return
        }

        // Kiểm tra điều kiện đăng nhập đặc biệt (Bạn có thể tùy chỉnh điều kiện này phù hợp với yêu cầu của mình)
        if (id == "1" && password == "123") {
            // Đăng nhập thành công trong trường hợp đặc biệt (ví dụ: quyền admin)
            findNavController().navigate(R.id.action_loginFragment_to_chooseTableFragment)
            return
        }
        if (id == "1" && password == "1234") {
            // Đăng nhập thành công trong trường hợp đặc biệt (ví dụ: quyền admin)
            findNavController().navigate(R.id.action_loginFragment_to_kitchenFragment)
            return
        }

        if (id == "1" && password == "12") {
            // Đăng nhập thành công trong trường hợp đặc biệt khác (ví dụ: nhân viên)
            findNavController().navigate(R.id.action_loginFragment_to_adminAccessFragment)
            return
        }

        // Lấy UserDao từ UserDatabase
        val userDao = OrderRoomDatabase.getDatabase(requireContext()).userDao()

        // Sử dụng CoroutineScope để thực hiện quá trình đăng nhập bất đồng bộ
        coroutineScope.launch {
            // Thực hiện đăng nhập theo ID bằng UserDao
            val user =
                withContext(Dispatchers.IO) { userDao.loginById(id) }

            // Kiểm tra kết quả của quá trình đăng nhập, giải mã user.password và so sánh chúng
            if (user != null && BCrypt.checkpw(password, user.password)) {
                if (user.position == "Admin") {
                    userViewModel.id = id
                    findNavController().navigate(R.id.action_loginFragment_to_adminAccessFragment)
                } else if (user.position == "Staff") {
                    sharedViewModel.staffId = id
                    val action =
                        LoginFragmentDirections.actionLoginFragmentToChooseTableFragment(staffId = user.staffId)
                    findNavController().navigate(action)
                }
                else if (user.position == "Kitchen") {
                    val action =
                        LoginFragmentDirections.actionLoginFragmentToKitchenFragment()
                    findNavController().navigate(action)
                }
            } else {
                // Đăng nhập thất bại
                Toast.makeText(requireContext(), "Your ID or Password is not correct", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun disableBackButton() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {}
            }
        )
    }
    override fun onDestroy() {
        super.onDestroy()
        // Hủy CoroutineScope để tránh rò rỉ tài nguyên khi Fragment bị hủy
        coroutineScope.cancel()
    }
}

