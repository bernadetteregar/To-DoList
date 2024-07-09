package com.example.todolisttutorial

import android.content.Context
import android.graphics.Paint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.todolisttutorial.databinding.TaskItemCellBinding
import java.time.format.DateTimeFormatter

class TaskItemViewHolder(
    private val context: Context,
    private val binding: TaskItemCellBinding,
    private val clickListener: TaskItemClickListener
):RecyclerView.ViewHolder(binding.root) {
    private val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun bindTaskItem(taskItem: TaskItem){
        binding.name.text = taskItem.name


        if (taskItem.isCompleted()) {
            binding.root.alpha = 0.5f
            binding.editButton.visibility = View.GONE
        } else {
            binding.root.alpha = 1.0f
            binding.completedButton.isEnabled = true
            binding.editButton.visibility = View.VISIBLE
        }

        binding.completedButton.setImageResource(taskItem.imageResource ())
        binding.completedButton.setColorFilter(taskItem.imageColor(context))

        binding.completedButton.setOnClickListener{
            clickListener.completeTaskItem(taskItem)
        }
        binding.editButton.setOnClickListener{
            clickListener.editTaskItem(taskItem)
        }
        if(taskItem.dueDate() != null)
            binding.dueDate.text = dateFormat.format(taskItem.dueDate())
        else
            binding.dueDate.text = ""

        binding.deleteButton.setOnClickListener {
            clickListener.deleteTaskItem(taskItem)
        }
    }
}