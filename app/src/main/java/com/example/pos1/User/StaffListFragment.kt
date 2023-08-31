package com.example.pos1.User

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pos1.MainActivity
import com.example.pos1.R
import com.example.pos1.UserApplication
import com.example.pos1.databinding.FragmentStaffListBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder


@Suppress("DEPRECATION")
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

        val adapter = UserAdapter(
        onItemClicked = {user ->

        },
        onButtonClicked = {user ->
            val action =
                StaffListFragmentDirections.actionStaffToUserDetailFragment(user.userName)
            findNavController().navigate(action)

        }
        )


        binding.recyclerView.adapter = adapter

        viewModel.allItems.observe(viewLifecycleOwner) { items ->
            items?.let {
                adapter.submitList(it)
            }
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.floatingActionButton.setOnClickListener {
            val action = StaffListFragmentDirections.actionStaffListFragmentToAddStaffFragment(
                 ""
            )
            findNavController().navigate(action)
        }
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.logout -> {
                   logOutDialog()
                    true
                }
                else -> false
            }
        }
    }
    private fun logOutDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage("Do you really want to log out?")
            .setCancelable(false)
            .setNegativeButton("No") { _, _ -> }
            .setPositiveButton("Yes") { _, _ ->
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
            }
            .show()
    }
}




