package com.example.pos1.schedule

import ScheduleViewModel
import ScheduleViewModelFactory
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pos1.R
import com.example.pos1.UserApplication
import com.example.pos1.databinding.FragmentScheduleBinding
import com.example.pos1.entity.Schedule
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Calendar

@Suppress("DEPRECATION")
class ScheduleFragment : Fragment() {
    private lateinit var binding: FragmentScheduleBinding
    private var selectedDate: String? = null
    private val viewModel: ScheduleViewModel by activityViewModels {
        ScheduleViewModelFactory(
            (activity?.application as UserApplication).orderDatabase.scheduleDao()
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScheduleBinding.inflate(inflater, container, false)


        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.all.setOnClickListener {
            viewModel.selectDate(null)
        }
        //chọn ngày để lọc
        val editText = binding.etDate

        binding.btnDatePicker.setOnClickListener {
            val enteredText = editText.text.toString()
            viewModel.selectDate(enteredText)
        }
        // Xử lý sự kiện khi người dùng nhấn vào TextView
        binding.etDate.setOnClickListener {
            showDatePicker()
        }



        binding.floatingActionButton.setOnClickListener {
            val action = ScheduleFragmentDirections.actionScheduleFragmentToAddScheduleFragment(
            )
            this.findNavController().navigate(action)
        }
        val adapter = ScheduleAdapter { schedule ->
            showConfirmationDialog(schedule)
        }
        viewModel.schedules.observe(this.viewLifecycleOwner) { items ->
            items.let {
                adapter.submitList(it)
            }
        }


        //  Thiết lập RecyclerView
        val layoutManager = GridLayoutManager(context, 1)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {

                R.id.home -> {
                    val action = ScheduleFragmentDirections.actionScheduleFragmentToAdminAccessFragment()
                    findNavController().navigate(action)
                    true
                }
                R.id.setting -> {
                    val action= ScheduleFragmentDirections.actionScheduleFragmentToRosterFragment()
                    findNavController().navigate(action)
                    true
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
//        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }
    private fun showConfirmationDialog(schedule: Schedule) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.delete_question))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                viewModel.deleteSchedule(schedule)
            }
            .show()
    }
    private fun formatDate(year: Int, month: Int, dayOfMonth: Int): String {
        return String.format("%d-%02d-%02d", year, month + 1, dayOfMonth)
    }



}