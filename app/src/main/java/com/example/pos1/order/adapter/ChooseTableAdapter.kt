package com.example.pos1.order.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pos1.R
import com.example.pos1.databinding.ChooseTableItemBinding
import com.example.pos1.databinding.TableItemBinding
import com.example.pos1.entity.Table

//Constructor: Adapter nhận vào một tham số là lambda function onItemClicked có kiểu dữ liệu là
// (Table) -> Unit. Lambda function này được gọi khi một mục trong danh sách được nhấn.
class ChooseTableAdapter(private val onItemClicked: (Table) -> Unit) :
    ListAdapter<Table, ChooseTableAdapter.TableViewHolder>(DiffCallback) {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableViewHolder {

        return TableViewHolder(
            ChooseTableItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }
    //nBindViewHolder: Phương thức này được gọi khi một ViewHolder cần được hiển thị dữ liệu.
    // Nó gán dữ liệu từ mục hiện tại vào ViewHolder và thiết lập OnClickListener cho mục đó.
    // Khi mục được nhấp, phương thức onItemClicked sẽ được gọi với mục tương ứng là tham số.
    override fun onBindViewHolder(holder: TableViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)

        if (current.status==1) {
            holder.binding.tbnumber.setBackgroundResource(R.drawable.layout2)
        } else {
            holder.binding.tbnumber.setBackgroundResource(R.drawable.layout1)

        }

        holder.binding.tbnumber.setOnClickListener {
            onItemClicked(current)
        }
    }

    //TableViewHolder: Lớp này là một lớp con của RecyclerView.ViewHolder.
    // Nó giữ các tham chiếu đến các thành phần trong layout của mục danh sách.
    // Phương thức bind được gọi để gán dữ liệu từ mục hiện tại vào các thành phần tương ứng trong ViewHolder.
    class TableViewHolder(var binding: ChooseTableItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(table: Table) {
            binding.tbnumber.text = table.number.toString()
            binding.capacity.text = table.capacity.toString() +" "+ "people"
        }
    }
    // Lớp này cung cấp cách so sánh hai mục danh sách để xác định xem chúng có giống nhau không.
    // Phương thức areItemsTheSame kiểm tra xem hai mục có cùng id hay không,
    // trong khi phương thức areContentsTheSame kiểm tra xem nội dung của hai mục có giống nhau hay không.
    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Table>() {
            override fun areItemsTheSame(oldItem: Table, newItem: Table): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Table, newItem: Table): Boolean {
                return oldItem.number == newItem.number
            }
        }
    }
}