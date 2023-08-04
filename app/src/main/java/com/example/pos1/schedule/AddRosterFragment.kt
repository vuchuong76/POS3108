package com.example.pos1.schedule

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.pos1.R
import com.example.pos1.UserApplication
import com.example.pos1.databinding.FragmentAddRosterBinding
import com.example.pos1.databinding.FragmentNewItemBinding
import com.example.pos1.editmenu.ItemViewModel
import com.example.pos1.editmenu.ItemViewModelFactory
import com.example.pos1.editmenu.NewItemFragmentArgs
import com.example.pos1.editmenu.NewItemFragmentDirections
import com.example.pos1.entity.Item
import com.example.pos1.entity.Roster

class AddRosterFragment : Fragment() {
    private val viewModel: RosterViewModel by activityViewModels {
        RosterViewModelFactory(
            (activity?.application as UserApplication).orderDatabase.rosterDao()
        )
    }
    private var _binding: FragmentAddRosterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddRosterBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addBt1.setOnClickListener {
            addNewRoster()

        }

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {

                R.id.back -> {
                    val action = AddRosterFragmentDirections.actionAddRosterFragmentToRosterFragment()
                    findNavController().navigate(action)
                    true
                    // by returning 'true' we're saying that the event
                    // is handled and it shouldn't be propagated further
                }
                else -> false
            }
        }
    }

        private fun addNewRoster() {
            if (isEntryValid()) {
                viewModel.addNewRoster(
                    binding.start.text.toString(),
                    binding.finish.text.toString()
                )
            val action = AddRosterFragmentDirections.actionAddRosterFragmentToRosterFragment()
            findNavController().navigate(action)
            }
            else{
                Toast.makeText(context,"Not correct time",Toast.LENGTH_SHORT).show()
            }
        }

    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.start.text.toString(),
            binding.finish.text.toString()
            )
    }
        override fun onDestroyView() {
            super.onDestroyView()
            // Hide keyboard.
            val inputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as
                        InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(
                requireActivity().currentFocus?.windowToken,
                0
            )
            _binding = null
        }

    }