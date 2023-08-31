package com.example.pos1.schedule

import ScheduleViewModel
import ScheduleViewModelFactory
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.pos1.R
import com.example.pos1.User.UserViewModel
import com.example.pos1.UserApplication
import com.example.pos1.databinding.FragmentAddScheduleBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import java.util.Calendar

@Suppress("DEPRECATION")
class AddScheduleFragment : Fragment() {
    private val viewModel: ScheduleViewModel by activityViewModels {
        ScheduleViewModelFactory(
            (activity?.application as UserApplication).orderDatabase.scheduleDao()
        )
    }
    private var selectedEmployee: String? = null
    private var selectedDate: String? = null
    private var selectedShift: String? = null
    private val userViewModel: UserViewModel by activityViewModels {
        UserViewModel.UserViewModelFactory((activity?.application as UserApplication).orderDatabase.userDao())
    }
    private val rosterViewModel: RosterViewModel by activityViewModels {
        RosterViewModelFactory((activity?.application as UserApplication).orderDatabase.rosterDao())
    }
    private var _binding: FragmentAddScheduleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddScheduleBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Xử lý sự kiện khi người dùng nhấn vào TextView
        binding.etDate.setOnClickListener {
            showDatePicker()
        }
        // Lấy danh sách tên nhân viên từ ViewModel và cập nhật Spinner
        userViewModel.getAllUserNames().observe(viewLifecycleOwner) { userNames ->
            val adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, userNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerEmployee.adapter = adapter

            // Xử lý sự kiện khi người dùng chọn tên nhân viên từ Spinner
            binding.spinnerEmployee.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        selectedEmployee = userNames[position]
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // Không làm gì khi không có lựa chọn được chọn
                    }
                }
        }

        binding.btnAddSchedule.setOnClickListener {
            addNewSchedule()
        }


        //lấy roster
        rosterViewModel.allRosters.observe(viewLifecycleOwner) { rosters ->
            val rosterStrings = rosters.map { "${it.start_time}:00 - ${it.finish_time}:00" }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, rosterStrings)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerShift.adapter = adapter

            binding.spinnerShift.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedShift = rosterStrings[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // No action
                }
            }
        }

        //toolbar
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {

                R.id.back -> {
                    findNavController().navigateUp()
                    true
                    // by returning 'true' we're saying that the event
                    // is handled and it shouldn't be propagated further
                }
                else -> false
            }
        }
        viewModel.errorMessage.observe(viewLifecycleOwner, Observer { message ->
            message?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                selectedDate = formatDate(year, month, dayOfMonth)
                binding.etDate.text = selectedDate
            },
            year,
            month,
            day
        )

        // Chỉ cho phép chọn ngày từ ngày hôm nay
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()

        datePickerDialog.show()
    }

    private fun formatDate(year: Int, month: Int, dayOfMonth: Int): String {
        return String.format("%d-%02d-%02d", year, month + 1, dayOfMonth)
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
    private fun addNewSchedule() {
        if (isEntryValid()) {
            val pickedDate=binding.etDate.text.toString()
            val selectedShift=selectedShift ?: ""
            val selectedEmployee=selectedEmployee?: ""
            viewModel.scheduleExist(selectedEmployee,pickedDate,selectedShift) { exist ->
                if (!exist) {
                    viewModel.addNewSchedule(
                        pickedDate,
                        selectedShift,
                        selectedEmployee
                    )
                    findNavController().navigateUp()
                } else {
                    Snackbar.make(requireView(), "This Schedule is already exist", Snackbar.LENGTH_LONG).show()
                }
            }
        }
        else{
        }
    }



    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.etDate.text.toString(),
            selectedShift ?: "",
            selectedEmployee ?: ""
        )
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}