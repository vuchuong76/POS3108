package com.example.pos1

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pos1.databinding.StockItemBinding
import com.example.pos1.entity.Item

class StockAdapter(private val listener: StockItemActionListener) :
    ListAdapter<Item, StockAdapter.ItemViewHolder>(StockAdapter.ITEMS_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            StockItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current, listener)
    }

    class ItemViewHolder(private var binding: StockItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item, listener: StockItemActionListener) {
            binding.nameTextView.text = "Name:${item.name}"
            item.image?.let {
                Glide.with(itemView.context)
                    .load(item.image)
                    .into(binding.imageImageView)
            }
            binding.priceTextView.text = "Price:${item.price}$"
            binding.stockTextView.text = "Stock:${item.stock}"
            binding.type.text = "Type:${item.type}"

            // Setting click listeners
            binding.addButton.setOnClickListener {
                listener.onAddButtonClick(item)
            }

            binding.minusButton.setOnClickListener {
                listener.onMinusButtonClick(item)
            }

            itemView.setOnClickListener {
                listener.onItemClicked(item)
            }
        }
    }

    companion object {
        private val ITEMS_COMPARATOR = object : DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}

interface StockItemActionListener {
    fun onAddButtonClick(item: Item)
    fun onMinusButtonClick(item: Item)
    fun onItemClicked(item: Item)
}
