package com.example.pos1.editmenu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.pos1.R
import com.example.pos1.User.UserDetailFragmentDirections
import com.example.pos1.UserApplication
import com.example.pos1.databinding.FragmentItemDetailBinding
import com.example.pos1.entity.Item
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ItemDetailFragment : Fragment() {
    lateinit var item: Item
    private val args: ItemDetailFragmentArgs by navArgs()
    private lateinit  var binding: FragmentItemDetailBinding

    private val viewModel: ItemViewModel by activityViewModels {
        ItemViewModelFactory(
            (requireActivity().application as UserApplication).orderDatabase.itemDao()
        )
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentItemDetailBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }



    private fun bindItemDetails(item: Item) {
        binding?.apply {
            name.text = item.name
            type.text = item.type
            stock.text = item.stock.toString()
            price.text = item.price.toString()
            image.text = item.image.toString()
            deleteItem.setOnClickListener { showConfirmationDialog() }
            editItem.setOnClickListener { editItem() }
        }
    }
    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage("Do you want to delete this item?")
            .setCancelable(false)
            .setNegativeButton("No") { _, _ -> }
            .setPositiveButton("Yes") { _, _ ->
                deleteItem()
            }
            .show()
    }

    private fun deleteItem() {
        viewModel.deleteItem(item)
        findNavController().navigateUp()
    }

    private fun editItem() {
        val action = ItemDetailFragmentDirections.actionItemDetailFragmentToNewItemFragment2(
            R.string.edit_user.toString(),
            item.id
        )
        this.findNavController().navigate(action)
    }





    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val itemId = args.itemId
        viewModel.retrieveItem(itemId).observe(viewLifecycleOwner) { item ->
            item?.let {
                this.item = item // Gán giá trị cho thuộc tính user
                bindItemDetails(item)
            }
        }
        binding?.toolbar?.setOnMenuItemClickListener {
            when (it.itemId) {

                R.id.back -> {
                    val action = ItemDetailFragmentDirections.actionItemDetailFragmentToMenuListFragment()
                    findNavController().navigate(action)
                    true
                    // by returning 'true' we're saying that the event
                    // is handled and it shouldn't be propagated further
                }
                else -> false
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

}