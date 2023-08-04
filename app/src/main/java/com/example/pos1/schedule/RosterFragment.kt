package com.example.pos1.schedule

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pos1.R
import com.example.pos1.User.StaffListFragmentDirections
import com.example.pos1.User.UserAdapter
import com.example.pos1.UserApplication
import com.example.pos1.databinding.FragmentRosterBinding
import com.example.pos1.databinding.FragmentScheduleBinding
import com.example.pos1.editmenu.MenuListFragmentDirections
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RosterFragment : Fragment() {
    private lateinit var binding: FragmentRosterBinding

    private val viewModel: RosterViewModel by activityViewModels {
        RosterViewModelFactory(
            (activity?.application as UserApplication).orderDatabase.rosterDao()
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRosterBinding.inflate(inflater, container, false)


        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.floatingActionButton.setOnClickListener {
            val action = RosterFragmentDirections.actionRosterFragmentToAddRosterFragment(
            )
            this.findNavController().navigate(action)
        }
        val adapter = RosterAdapter { roster ->
            viewModel.deleteRoster(roster)
        }
        viewModel.allRosters.observe(this.viewLifecycleOwner) { items ->
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

                R.id.back -> {
                    val action = RosterFragmentDirections.actionRosterFragmentToScheduleFragment()
                    findNavController().navigate(action)
                    true
                    // by returning 'true' we're saying that the event
                    // is handled and it shouldn't be propagated further
                }
                else -> false
            }
        }
    }
}