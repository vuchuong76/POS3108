package com.example.pos1.order.adapter
import android.net.Uri
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pos1.R
import com.example.pos1.databinding.FoodDrinkAppetizerItemBinding
import com.example.pos1.entity.Item
//hiển thị menu ra 3 fragment drink, food, appetizer
//class FoodDrinkAppetizerAdapter(
//    private val onItemClicked: (Item) -> Unit,
//    private val itemType: String
//) : ListAdapter<Item, FoodDrinkAppetizerAdapter.ItemViewHolder>(ITEMS_COMPARATOR) {
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
//        return ItemViewHolder(
//            FoodDrinkAppetizerItemBinding.inflate(
//                LayoutInflater.from(parent.context),
//                parent,
//                false
//            )
//        )
//    }
//    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
//        val current = getItem(position)
//        holder.itemView.setOnClickListener {
//            onItemClicked(current)
//        }
//        holder.bind(current)
//    }

class FoodDrinkAppetizerAdapter(
    private val onItemClicked: (Item) -> Unit,
    private val itemType: String
) : ListAdapter<Item, FoodDrinkAppetizerAdapter.ItemViewHolder>(ITEMS_COMPARATOR) {

    // Thời gian debounce
    private val DEBOUNCE_TIME: Long = 300
    private var lastClickTime: Long = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            FoodDrinkAppetizerItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            val currentTime = SystemClock.elapsedRealtime()
            if (currentTime - lastClickTime > DEBOUNCE_TIME) {
                lastClickTime = currentTime
                onItemClicked(current)
            }
        }
        holder.bind(current)
    }



    override fun getItemCount(): Int {
        val filteredList = currentList.filter { it.type == itemType }
        return filteredList.size
    }
    fun setItems(items: List<Item>) {
        val filteredList = items.filter { it.type == itemType }
        submitList(filteredList)
    }
    class ItemViewHolder(private val binding: FoodDrinkAppetizerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        //gán giá trị thuộc tính có trong database vào recycleView
        fun bind(item: Item) {
            binding.nameTextView.text = item.name.toString()
            item.image?.let {
                Glide.with(itemView.context)
                    .load(Uri.parse(item.image))
                    .into(binding.imageImageView)
            }
            binding.priceTextView.text = "${item.price}\$"
            binding.stockTextView.text="Stock : ${item.stock}"
            if(item.stock < 1) {
                binding.root.setBackgroundColor(
                    ContextCompat.getColor(itemView.context, R.color.black)
                )
            } else {
                // Đặt lại màu nền mặc định nếu bạn muốn
                binding.root.setBackgroundColor(
                    ContextCompat.getColor(itemView.context, android.R.color.transparent)
                )
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
