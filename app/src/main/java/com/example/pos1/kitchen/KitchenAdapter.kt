package com.example.pos1.kitchen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pos1.databinding.KitchenItemBinding
import com.example.pos1.databinding.OrderItemBinding
import com.example.pos1.entity.Order

class KitchenAdapter(
    private val onItemClicked: (Order) -> Unit,
    private val onButtonClicked: (Order) -> Unit  // Thêm callback cho button, onOrderClick: kotlin.Any){}
) : ListAdapter<Order, KitchenAdapter.ItemViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            KitchenItemBinding.inflate(
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

        // Gán sự kiện click cho button
        holder.binding.delete.setOnClickListener {  // Thay 'yourButtonId' bằng ID thực tế của button
            onButtonClicked(current)
        }
    }

    // ViewHolder cho mục đơn hàng
    class ItemViewHolder(val binding: KitchenItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order) {
            binding.table.text = order.tableNumber.toString()
            binding.nameView.text = order.name
            binding.timeView.text = order.time.toString()
            binding.quantityView.text = order.quantity.toString()
            binding.price.text = order.price.toString()
            binding.status.text = order.order_status
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Order>() {
            override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
                return oldItem.itemId == newItem.itemId
            }

            override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
                return oldItem == newItem
            }
        }
    }
}
