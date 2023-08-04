package com.example.pos1.User

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.pos1.R
import com.example.pos1.UserApplication
import com.example.pos1.databinding.FragmentUserDetailBinding
import com.example.pos1.entity.User
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class UserDetailFragment : Fragment() {
    lateinit var user: User
    private val args: UserDetailFragmentArgs by navArgs()
    private  var binding: FragmentUserDetailBinding?=null

    private val viewModel: UserViewModel by activityViewModels {
        UserViewModel.UserViewModelFactory(
            (requireActivity().application as UserApplication).orderDatabase.userDao()
        )
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserDetailBinding.inflate(inflater, container, false)
        return binding?.root
    }



    private fun bindUserDetails(user: User) {
        binding?.apply {
            staffId.text = user.staffId.toString()
//            passWord.text = user.password
            staffName.text = user.staffname
            staffAge.text = user.age.toString()
            position.text = user.position
            tel.text = user.tel.toString()
            address.text = user.address
            deleteItem.setOnClickListener { showConfirmationDialog() }
            editItem.setOnClickListener { editUser() }
        }
    }
    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage("Do you want to delete this user?")
            .setCancelable(false)
            .setNegativeButton("No") { _, _ -> }
            .setPositiveButton("Yes") { _, _ ->
                deleteUser()
            }
            .show()
    }

    private fun deleteUser() {
        viewModel.deleteUser(user)
        findNavController().navigateUp()
    }

    private fun editUser() {
        val action = UserDetailFragmentDirections.actionUserDetailFragmentToAddStaffFragment5(
            R.string.edit_user.toString(),
            user.staffId
        )
        this.findNavController().navigate(action)
    }





    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userId = args.staffId
        viewModel.retrieveItem(userId).observe(viewLifecycleOwner) { user ->
            user?.let {
                this.user = user // Gán giá trị cho thuộc tính user
                bindUserDetails(user)
            }
        }
        binding?.toolbar?.setOnMenuItemClickListener {
            when (it.itemId) {

                R.id.back -> {
                    val action = UserDetailFragmentDirections.actionUserDetailFragmentToStaffListFragment()
                    findNavController().navigate(action)
                    true
                    // by returning 'true' we're saying that the event
                    // is handled and it shouldn't be propagated further
                }
                else -> false
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}



