package com.example.pos1.User

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pos1.AdminAccessFragmentDirections
import com.example.pos1.R
import com.example.pos1.UserApplication
import com.example.pos1.databinding.FragmentStaffListBinding
import com.example.pos1.orlist.OrderListFragmentDirections


class StaffListFragment : Fragment() {

    private val viewModel: UserViewModel by activityViewModels {
        UserViewModel.UserViewModelFactory(
            (requireActivity().application as UserApplication).orderDatabase.userDao()
        )
    }
    private lateinit var binding: FragmentStaffListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStaffListBinding.inflate(inflater, container, false)
        // Báo cho hệ thống rằng Fragment này có menu
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = UserAdapter { user ->
            val action =
                StaffListFragmentDirections.actionStaffListFragmentToUserDetailFragment(user.staffId)
            findNavController().navigate(action)
        }

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {

                R.id.home -> {
                    val action = StaffListFragmentDirections.actionStaffListFragmentToAdminAccessFragment()
                    findNavController().navigate(action)
                    true
                    // by returning 'true' we're saying that the event
                    // is handled and it shouldn't be propagated further
                }
                else -> false
            }
        }

        binding.recyclerView.adapter = adapter

        viewModel.allItems.observe(viewLifecycleOwner) { items ->
            items?.let {
                adapter.submitList(it)
            }
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.floatingActionButton.setOnClickListener {
            val action = StaffListFragmentDirections.actionStaffListFragmentToAddStaffFragment(
                getString(R.string.add_staff)
            )
            findNavController().navigate(action)
        }
    }
}




