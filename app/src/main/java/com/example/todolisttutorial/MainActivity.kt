package com.example.todolisttutorial

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolisttutorial.databinding.ActivityMainBinding
import java.time.LocalDate
import java.util.Calendar

class MainActivity : AppCompatActivity(), TaskItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private val taskViewModel: TaskViewModel by viewModels {
        TaskItemModelFactory((application as TodoApplication).repository)
    }

    private lateinit var incompleteAdapter: TaskItemAdapter
    private lateinit var completedAdapter: TaskItemAdapter
    private var selectedCategory: String? = null
    private var selectedStartDate: LocalDate? = null
    private var selectedEndDate: LocalDate? = null
    private var isCompletedTasksVisible = false
    private var isCompletedIconPressed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        incompleteAdapter = TaskItemAdapter(this, emptyList(), this)
        completedAdapter = TaskItemAdapter(this, emptyList(), this)

        binding.incompleteTasksRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.incompleteTasksRecyclerView.adapter = incompleteAdapter

        binding.completedTasksRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.completedTasksRecyclerView.adapter = completedAdapter

        binding.newTaskButton.setOnClickListener {
            NewTaskSheet(null).show(supportFragmentManager, "newTaskTag")
        }

        binding.completedTitle.setOnClickListener {
            isCompletedTasksVisible = !isCompletedTasksVisible
            binding.completedTasksRecyclerView.visibility = if (isCompletedTasksVisible) View.VISIBLE else View.GONE

            toggleCompletedIcon()
        }
        binding.filterButton.setOnClickListener {
            showCategoryAndDateFilterDialog()
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchDatabase(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { searchDatabase(it) }
                return true
            }
        })

        taskViewModel.taskItems.observe(this) { tasks ->
            updateTasks(tasks)
        }
    }

    private fun updateTasks(tasks: List<TaskItem>) {
        val filteredTasks = tasks.filter { task ->
            val matchesCategory = selectedCategory?.let { it == task.category } ?: true
            val matchesDateRange = selectedStartDate?.let { start ->
                task.dueDate()?.let { it.isAfter(start.minusDays(1)) && it.isBefore(selectedEndDate?.plusDays(1) ?: LocalDate.MAX) }
            } ?: true

            matchesCategory && matchesDateRange
        }

        val incompleteTasks = filteredTasks.filter { !it.isCompleted() }
        val completedTasks = filteredTasks.filter { it.isCompleted() }

        incompleteAdapter.taskItems = incompleteTasks
        completedAdapter.taskItems = completedTasks

        incompleteAdapter.notifyDataSetChanged()
        completedAdapter.notifyDataSetChanged()
    }

    private fun searchDatabase(query: String) {
        val searchQuery = "%$query%"
        taskViewModel.searchTaskItems(searchQuery).observe(this) { list ->
            updateTasks(list)
        }
    }

    @SuppressLint("MissingInflatedId")
    private fun showCategoryAndDateFilterDialog() {
        val categories = listOf("All") + taskViewModel.getAllCategories()

        val dialogView = layoutInflater.inflate(R.layout.dialog_filter, null)
        val categorySpinner = dialogView.findViewById<Spinner>(R.id.categorySpinner)
        val startDateButton = dialogView.findViewById<Button>(R.id.startDateButton)
        val endDateButton = dialogView.findViewById<Button>(R.id.endDateButton)

        categorySpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        startDateButton.setOnClickListener {
            showDatePickerDialog { date ->
                selectedStartDate = date
                startDateButton.text = date.toString()
            }
        }

        endDateButton.setOnClickListener {
            showDatePickerDialog { date ->
                selectedEndDate = date
                endDateButton.text = date.toString()
            }
        }

        AlertDialog.Builder(this)
            .setTitle("Filter Tasks")
            .setView(dialogView)
            .setPositiveButton("Apply") { _, _ ->
                selectedCategory = if (categorySpinner.selectedItemPosition == 0) null else categories[categorySpinner.selectedItemPosition]
                taskViewModel.taskItems.value?.let { updateTasks(it) }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDatePickerDialog(onDateSelected: (LocalDate) -> Unit) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                onDateSelected(LocalDate.of(year, month + 1, dayOfMonth))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    override fun editTaskItem(taskItem: TaskItem) {
        NewTaskSheet(taskItem).show(supportFragmentManager,"newTaskTag")
    }

    override fun completeTaskItem(taskItem: TaskItem) {
        taskViewModel.setCompleted(taskItem)
    }

    override fun deleteTaskItem(taskItem: TaskItem) {
        taskViewModel.deleteTaskItem(taskItem)
    }

    private fun toggleCompletedIcon() {
        val completedTitle = binding.completedTitle
        val drawableId = if (isCompletedIconPressed) {
            R.drawable.completed_24 // Default icon
        } else {
            R.drawable.completed_pressed_24 // Pressed icon
        }
        val drawable = ContextCompat.getDrawable(this, drawableId)
        drawable?.let {
            DrawableCompat.setTint(it, ContextCompat.getColor(this, R.color.gray))
            completedTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, it, null)
        }
        isCompletedIconPressed = !isCompletedIconPressed
    }
}
