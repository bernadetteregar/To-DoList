package com.example.todolisttutorial

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todolisttutorial.databinding.TaskItemCellBinding

class TaskItemAdapter(
    private val context: Context,
    var taskItems: List<TaskItem>,
    private val clickListener: TaskItemClickListener
): RecyclerView.Adapter<TaskItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): TaskItemViewHolder {
        val binding = TaskItemCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskItemViewHolder(context, binding, clickListener)
    }

    override fun getItemCount(): Int = taskItems.size

    override fun onBindViewHolder(holder: TaskItemViewHolder, position: Int) {
        holder.bindTaskItem(taskItems[position])
    }
}