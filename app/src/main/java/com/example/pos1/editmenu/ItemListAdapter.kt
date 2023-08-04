package com.example.pos1.editmenu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pos1.databinding.MenuItemBinding
import com.bumptech.glide.Glide
import com.example.pos1.entity.Item


class ItemListAdapter(private val onItemClicked: (Item) -> Unit) :
    ListAdapter<Item, ItemListAdapter.ItemViewHolder>(ItemListAdapter.ITEMS_COMPARATOR ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            MenuItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }
    //nBindViewHolder: Phương thức này được gọi khi một ViewHolder cần được hiển thị dữ liệu.
    // Nó gán dữ liệu từ mục hiện tại vào ViewHolder và thiết lập OnClickListener cho mục đó.
    // Khi mục được nhấp, phương thức onItemClicked sẽ được gọi với mục tương ứng là tham số.
    override fun onBindViewHolder(holder: ItemListAdapter.ItemViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
        holder.bind(current)
    }
    //TableViewHolder: Lớp này là một lớp con của RecyclerView.ViewHolder.
    // Nó giữ các tham chiếu đến các thành phần trong layout của mục danh sách.
    // Phương thức bind được gọi để gán dữ liệu từ mục hiện tại vào các thành phần tương ứng trong ViewHolder.
    class ItemViewHolder(private var binding: MenuItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item) {
            binding.nameTextView.text = "Name:"+item.name.toString()
            item.image?.let {
                Glide.with(itemView.context)
                    .load(item.image)
                    .into(binding.imageImageView)
            }
            binding.priceTextView.text = "Price:"+item.price.toString()+"$"
            binding.stockTextView.text = "Stock:"+ item.stock.toString()
            binding.type.text = "Type:"+item.type

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