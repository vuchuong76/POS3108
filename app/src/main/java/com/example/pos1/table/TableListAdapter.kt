package com.example.pos1.table

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pos1.databinding.TableItemBinding
import com.example.pos1.entity.Table

//Constructor: Adapter nhận vào một tham số là lambda function onItemClicked có kiểu dữ liệu là
// (Table) -> Unit. Lambda function này được gọi khi một mục trong danh sách được nhấn.
class TableListAdapter(private val onItemClicked: (Table) -> Unit) :
    ListAdapter<Table, TableListAdapter.TableViewHolder>(DiffCallback) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableViewHolder {

        return TableViewHolder(
            TableItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }
    override fun onBindViewHolder(holder: TableViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
        holder.binding.tbnumber.setOnClickListener {
            onItemClicked(current)
        }
    }
    class TableViewHolder(var binding: TableItemBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(table: Table) {
            binding.tbnumber.text = table.number.toString()
            binding.capacity.text = table.capacity.toString() +" "+ "people"
        }
    }
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