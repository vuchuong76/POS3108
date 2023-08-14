package com.example.pos1.schedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pos1.databinding.RosterItemBinding
import com.example.pos1.entity.Roster

class RosterAdapter(private val onItemClicked: (Roster) -> Unit) :
    ListAdapter<Roster, RosterAdapter.RosterViewHolder>(DiffCallback) {

    // Tạo ViewHolder cho mỗi mục đơn hàng
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RosterViewHolder {
        // Sử dụng RosterItemBinding để liên kết layout
        return RosterViewHolder(
            RosterItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    // Liên kết dữ liệu của mục đơn hàng với ViewHolder
    override fun onBindViewHolder(holder: RosterViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
        holder.itemView.setOnClickListener {
            // Gọi callback khi mục đơn hàng được nhấp vào
            onItemClicked(current)
        }
    }

    // ViewHolder cho mục đơn hàng
    class RosterViewHolder(private val binding: RosterItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Gắn dữ liệu của mục đơn hàng vào thành phần giao diện tương ứng
        fun bind(roster: Roster) {
            binding.startView.text = roster.start_time.toString()+":00"
            binding.finishView.text = roster.finish_time.toString()+":00"
        }
    }

    companion object {
        // Đối tượng DiffUtil.ItemCallback để xác định tính tương đồng giữa các mục đơn hàng
        private val DiffCallback = object : DiffUtil.ItemCallback<Roster>() {
            override fun areItemsTheSame(oldItem: Roster, newItem: Roster): Boolean {
                // Kiểm tra xem hai mục đơn hàng có cùng ID hay không
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Roster, newItem: Roster): Boolean {
                // Kiểm tra xem hai mục đơn hàng có cùng nội dung hay không
                return oldItem == newItem
            }
        }
    }
}