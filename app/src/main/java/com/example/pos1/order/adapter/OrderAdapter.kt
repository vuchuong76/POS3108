package com.example.pos1.order.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pos1.databinding.OrderItemBinding
import com.example.pos1.entity.Order

class OrderAdapter(
    private val onItemClicked: (Order) -> Unit,
    private val onButtonClicked: (Order) -> Unit
) : ListAdapter<Order, OrderAdapter.ItemViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            OrderItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }

        holder.binding.delete.setOnClickListener {
            onButtonClicked(current)
        }
    }

    // ViewHolder cho mục đơn hàng
    class ItemViewHolder(val binding: OrderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order) {
            binding.status.text = order.order_status
            binding.nameView.text = order.name
            binding.timeView.text = order.time
            binding.quantityView.text = order.quantity.toString()
            binding.price.text = order.price.toString()
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Order>() {
            override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
                return oldItem == newItem
            }
        }
    }
}
