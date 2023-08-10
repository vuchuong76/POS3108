package com.example.pos1.schedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pos1.databinding.ItemScheduleBinding
import com.example.pos1.entity.Schedule

class ScheduleAdapter(private val onItemClicked: (Schedule) -> Unit) :
    ListAdapter<Schedule, ScheduleAdapter.ScheduleViewHolder>(DiffCallback) {

    // Tạo ViewHolder cho mỗi mục đơn hàng
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        // Sử dụng ScheduleItemBinding để liên kết layout
        return ScheduleViewHolder(
            ItemScheduleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    // Liên kết dữ liệu của mục đơn hàng với ViewHolder
    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
        holder.itemView.setOnClickListener {
            // Gọi callback khi mục đơn hàng được nhấp vào
            onItemClicked(current)
        }
    }

    // ViewHolder cho mục đơn hàng
    class ScheduleViewHolder(private val binding: ItemScheduleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Gắn dữ liệu của mục đơn hàng vào thành phần giao diện tương ứng
        fun bind(schedule: Schedule) {
            binding.dateTextView.text=schedule.date.toString()
            binding.shiftTextView.text = schedule.shift.toString()
            binding.employeeTextView.text = schedule.employee.toString()
        }
    }

    companion object {
        // Đối tượng DiffUtil.ItemCallback để xác định tính tương đồng giữa các mục đơn hàng
        private val DiffCallback = object : DiffUtil.ItemCallback<Schedule>() {
            override fun areItemsTheSame(oldItem: Schedule, newItem: Schedule): Boolean {
                // Kiểm tra xem hai mục đơn hàng có cùng ID hay không
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Schedule, newItem: Schedule): Boolean {
                // Kiểm tra xem hai mục đơn hàng có cùng nội dung hay không
                return oldItem == newItem
            }
        }
    }
}