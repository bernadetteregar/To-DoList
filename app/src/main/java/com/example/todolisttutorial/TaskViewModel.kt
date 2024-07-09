package com.example.todolisttutorial

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.time.LocalDate

class TaskViewModel(private val repository: TaskItemRepository): ViewModel()
{
    var taskItems: LiveData<List<TaskItem>> = repository.allTaskItems.asLiveData()

    fun addTaskItem(newTask: TaskItem) = viewModelScope.launch {
        repository.insertTaskItem(newTask)
    }

    fun updateTaskItem(taskItem: TaskItem) = viewModelScope.launch {
        repository.updateTaskItem(taskItem)
    }

    fun deleteTaskItem(taskItem: TaskItem) = viewModelScope.launch {
        repository.deleteTaskItem(taskItem)
    }

    fun searchTaskItems(query: String): LiveData<List<TaskItem>> {
        return repository.searchTaskItems(query).asLiveData()
    }

    fun setCompleted(taskItem: TaskItem) = viewModelScope.launch {
        taskItem.completedDateString = if (taskItem.completedDateString == null) {
            TaskItem.dateFormatter.format(LocalDate.now())
        } else {
            null
        }
        repository.updateTaskItem(taskItem)
    }
    fun getAllCategories(): List<String> {
        return taskItems.value?.map { it.category }?.distinct() ?: emptyList()
    }


}