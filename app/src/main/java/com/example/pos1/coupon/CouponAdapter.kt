package com.example.pos1.coupon

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pos1.databinding.CouponItemBinding
import com.example.pos1.databinding.RosterItemBinding
import com.example.pos1.entity.Coupon
import com.example.pos1.entity.Roster
import com.example.pos1.schedule.RosterAdapter


class CouponAdapter(private val onItemClicked: (Coupon) -> Unit) :
    ListAdapter<Coupon, CouponAdapter.CouponViewHolder>(DiffCallback) {

    // Tạo ViewHolder cho mỗi mục đơn hàng
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponViewHolder {
        // Sử dụng CouponItemBinding để liên kết layout
        return CouponViewHolder(
            CouponItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    // Liên kết dữ liệu của mục đơn hàng với ViewHolder
    override fun onBindViewHolder(holder: CouponViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
        holder.itemView.setOnClickListener {
            // Gọi callback khi mục đơn hàng được nhấp vào
            onItemClicked(current)
        }
    }

    // ViewHolder cho mục đơn hàng
    class CouponViewHolder(private val binding: CouponItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Gắn dữ liệu của mục đơn hàng vào thành phần giao diện tương ứng
        fun bind(coupon: Coupon) {
            binding.codeView.text = coupon.code
            binding.couponView.text = coupon.discount.toString()
        }
    }

    companion object {
        // Đối tượng DiffUtil.ItemCallback để xác định tính tương đồng giữa các mục đơn hàng
        private val DiffCallback = object : DiffUtil.ItemCallback<Coupon>() {
            override fun areItemsTheSame(oldItem: Coupon, newItem: Coupon): Boolean {
                // Kiểm tra xem hai mục đơn hàng có cùng ID hay không
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Coupon, newItem: Coupon): Boolean {
                // Kiểm tra xem hai mục đơn hàng có cùng nội dung hay không
                return oldItem == newItem
            }
        }
    }
}
