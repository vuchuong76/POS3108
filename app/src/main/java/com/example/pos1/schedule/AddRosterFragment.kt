package com.example.pos1.schedule

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.pos1.R
import com.example.pos1.UserApplication
import com.example.pos1.databinding.FragmentAddRosterBinding
@Suppress("DEPRECATION")
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
                    val action =
                        AddRosterFragmentDirections.actionAddRosterFragmentToRosterFragment()
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
            val start = binding.start.text.toString()
            val finish = binding.finish.text.toString()
            viewModel.rosterExist(start, finish)
            { exist ->
                if (!exist) {
                    viewModel.addNewRoster(
                        start,
                        finish
                    )
                    val action =
                        AddRosterFragmentDirections.actionAddRosterFragmentToRosterFragment()
                    findNavController().navigate(action)
                } else {
                    Toast.makeText(context, "This roster is already exist", Toast.LENGTH_SHORT)
                        .show()

                }
            }
        } else {
            Toast.makeText(context, "Not correct time", Toast.LENGTH_SHORT).show()
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