//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Button
//import android.widget.EditText
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.ViewModelProvider
//import com.example.pos1.R
//import com.example.pos1.entity.Schedule
//
//class EditScheduleFragment : Fragment() {
//
//    private lateinit var scheduleViewModel: ScheduleViewModel
//    private lateinit var currentSchedule: Schedule
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val rootView = inflater.inflate(R.layout.fragment_edit_schedule, container, false)
//
//        // Initialize the ViewModel
//        scheduleViewModel = ViewModelProvider(this).get(ScheduleViewModel::class.java)
//
//        // Get references to EditText views
//        val etDate = rootView.findViewById<EditText>(R.id.etDate)
//        val etShift = rootView.findViewById<EditText>(R.id.etShift)
//        val etEmployee = rootView.findViewById<EditText>(R.id.etEmployee)
//
//        // Get reference to the "Save Changes" button
//        val btnSaveChanges = rootView.findViewById<Button>(R.id.btnSaveChanges)
//
//        // Get the current schedule ID from the arguments
//        val scheduleId = arguments?.getLong("scheduleId")
//
//        // Retrieve the current schedule from the database using ViewModel
//        if (scheduleId != null) {
//            scheduleViewModel.getScheduleById(scheduleId).observe(viewLifecycleOwner, { schedule ->
//                if (schedule != null) {
//                    currentSchedule = schedule
//
//                    // Set the values of EditText views with the current schedule data
//                    etDate.setText(currentSchedule.date)
//                    etShift.setText(currentSchedule.shift)
//                    etEmployee.setText(currentSchedule.employee)
//                }
//            })
//        }
//
//        // Set click listener for "Save Changes" button
//        btnSaveChanges.setOnClickListener {
//            // Get updated values from EditText views
//            val updatedDate = etDate.text.toString()
//            val updatedShift = etShift.text.toString()
//            val updatedEmployee = etEmployee.text.toString()
//
//            // Update the current schedule with the updated values
//            currentSchedule.date = updatedDate
//            currentSchedule.shift = updatedShift
//            currentSchedule.employee = updatedEmployee
//
//            // Save the changes to the database using ViewModel
//            scheduleViewModel.updateSchedule(currentSchedule)
//
//            // Navigate back to WeekViewFragment after saving changes
//            requireActivity().supportFragmentManager.popBackStack()
//        }
//
//        return rootView
//    }
//}
