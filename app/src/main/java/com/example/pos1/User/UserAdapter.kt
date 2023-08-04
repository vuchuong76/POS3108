package com.example.pos1.User

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pos1.databinding.UserItemBinding
import com.example.pos1.entity.User

class UserAdapter(
    private val onItemClicked: (User) -> Unit
) : ListAdapter<User, UserAdapter.UserViewHolder>(DiffCallback) {


// chuyen xml layout sang dang view, trả về viewholder khi recyclerView cần
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val viewHolder = UserViewHolder(
            UserItemBinding.inflate(
                LayoutInflater.from( parent.context),
                parent,
                false
            )
        )
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            onItemClicked(getItem(position))
        }
        return viewHolder
    }
//gắn kết nguồn dữ liệu và thành phần giao diện
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
    val current = getItem(position)
    holder.itemView.setOnClickListener {
        onItemClicked(current)
    }
    //truyền đối tượng tương ứng với vị trí trong danh sách
        holder.bind(getItem(position))
    }

    class UserViewHolder(
        private var binding: UserItemBinding
    ): RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SimpleDateFormat")
        fun bind(user: User) {
            binding.apply {
                staffIdTextView.text = user.staffId.toString()
                positionTextView.text = user.position.toString()
                staffNameTextView.text = user.staffname.toString()
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem.staffId == newItem.staffId
            }

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem == newItem
            }
        }
    }
}
