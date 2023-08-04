package com.example.pos1.orlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pos1.databinding.OrderlistItemBinding
import com.example.pos1.entity.Orderlist


class OrderlistAdapter(private val onItemClicked: (Orderlist) -> Unit) :
    ListAdapter<Orderlist, OrderlistAdapter.ItemViewHolder>(DiffCallback) {

    // Tạo ViewHolder cho mỗi mục đơn hàng
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // Sử dụng OrderItemBinding để liên kết layout cho mục đơn hàng
        return ItemViewHolder(
            OrderlistItemBinding.inflate(
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
    class ItemViewHolder(private val binding: OrderlistItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Gắn dữ liệu của mục đơn hàng vào thành phần giao diện tương ứng
        fun bind(orderlist: Orderlist) {
            binding.orIdView.text = orderlist.orId.toString()
            binding.tbnumView.text = orderlist.tbnum.toString()
            binding.staffIdTextView.text = orderlist.staffId.toString()
            binding.dateView.text = orderlist.date.toString()
            binding.amountView.text = orderlist.amount.toString()
//            binding.statusView.text = orderlist.status.toString()
//            binding.paymentView.text = orderlist.payment.toString()
        }
    }

    companion object {
        // Đối tượng DiffUtil.ItemCallback để xác định tính tương đồng giữa các mục đơn hàng
        private val DiffCallback = object : DiffUtil.ItemCallback<Orderlist>() {
            override fun areItemsTheSame(oldItem: Orderlist, newItem: Orderlist): Boolean {
                // Kiểm tra xem hai mục đơn hàng có cùng ID hay không
                return oldItem.orId == newItem.orId
            }

            override fun areContentsTheSame(oldItem: Orderlist, newItem: Orderlist): Boolean {
                // Kiểm tra xem hai mục đơn hàng có cùng nội dung hay không
                return oldItem == newItem
            }
        }
    }
}