package com.example.pos1.schedule

import ScheduleViewModel
import ScheduleViewModelFactory
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.pos1.R
import com.example.pos1.User.UserViewModel
import com.example.pos1.UserApplication
import com.example.pos1.databinding.FragmentAddRosterBinding
import com.example.pos1.databinding.FragmentAddScheduleBinding
import com.example.pos1.databinding.FragmentNewItemBinding
import com.example.pos1.editmenu.ItemViewModel
import com.example.pos1.editmenu.ItemViewModelFactory
import com.example.pos1.editmenu.NewItemFragmentArgs
import com.example.pos1.editmenu.NewItemFragmentDirections
import com.example.pos1.entity.Item
import com.example.pos1.entity.Roster
import java.util.Calendar

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
                    val action = AddScheduleFragmentDirections.actionAddScheduleFragmentToScheduleFragment()
                    findNavController().navigate(action)
                    true
                    // by returning 'true' we're saying that the event
                    // is handled and it shouldn't be propagated further
                }
                else -> false
            }
        }
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


    private fun addNewSchedule() {
        if (isEntryValid()) {
            viewModel.addNewSchedule(
                binding.etDate.text.toString(),
                selectedShift ?: "",
                selectedEmployee ?: ""
            )
            val action = AddScheduleFragmentDirections.actionAddScheduleFragmentToScheduleFragment()
            findNavController().navigate(action)
        }
    }


    private fun isEntryValid(): Boolean {
        return selectedShift?.let {
            viewModel.isEntryValid(
                binding.etDate.text.toString(),
                it
            )
        } == true
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}