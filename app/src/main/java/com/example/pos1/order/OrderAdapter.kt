package com.example.pos1.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pos1.databinding.OrderItemBinding
import com.example.pos1.entity.Order

class OrderAdapter(private val onItemClicked: (Order) -> Unit) :
    ListAdapter<Order, OrderAdapter.ItemViewHolder>(DiffCallback) {

    // Tạo ViewHolder cho mỗi mục đơn hàng
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // Sử dụng OrderItemBinding để liên kết layout cho mục đơn hàng
        return ItemViewHolder(
            OrderItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    // Liên kết dữ liệu của mục đơn hàng với ViewHolder
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
        holder.itemView.setOnClickListener {
            // Gọi callback khi mục đơn hàng được nhấp vào
            onItemClicked(current)
        }
    }

    // ViewHolder cho mục đơn hàng
    class ItemViewHolder(private val binding: OrderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Gắn dữ liệu của mục đơn hàng vào thành phần giao diện tương ứng
        fun bind(order: Order) {
            binding.status.text = order.order_status.toString()
            binding.nameView.text = order.name
            binding.timeView.text = order.time.toString()
            binding.quantityView.text = order.quantity.toString()
            binding.price.text = order.price.toString()
        }
    }

    companion object {
        // Đối tượng DiffUtil.ItemCallback để xác định tính tương đồng giữa các mục đơn hàng
        private val DiffCallback = object : DiffUtil.ItemCallback<Order>() {
            override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
                // Kiểm tra xem hai mục đơn hàng có cùng ID hay không
                return oldItem.itemId == newItem.itemId
            }

            override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
                // Kiểm tra xem hai mục đơn hàng có cùng nội dung hay không
                return oldItem == newItem
            }
        }
    }
}
