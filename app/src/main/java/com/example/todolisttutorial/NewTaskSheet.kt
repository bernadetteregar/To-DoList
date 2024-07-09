package com.example.todolisttutorial

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import com.example.todolisttutorial.databinding.FragmentNewTaskSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.time.LocalDate

class NewTaskSheet(var taskItem: TaskItem?) : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentNewTaskSheetBinding
    private lateinit var taskViewModel: TaskViewModel
    private var dueDate: LocalDate? = null
    private var categories = arrayOf("Work", "Personal", "Home")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity()

        taskViewModel = ViewModelProvider(activity).get(TaskViewModel::class.java)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categorySpinner.adapter = adapter

        if (taskItem != null) {
            binding.taskTitle.text = "Edit Task"
            val editable = Editable.Factory.getInstance()
            binding.name.text = editable.newEditable(taskItem!!.name)
            binding.desc.text = editable.newEditable(taskItem!!.desc)
            binding.categorySpinner.setSelection(categories.indexOf(taskItem!!.category))

            dueDate = taskItem!!.dueDate()
            if (dueDate != null) {
                updateDateButtonText()
            }
        } else {
            binding.taskTitle.text = "New Task"
        }

        taskViewModel = ViewModelProvider(activity).get(TaskViewModel::class.java)
        binding.saveButton.setOnClickListener {
            saveAction()
        }
        binding.datePickerButton.setOnClickListener {
            openDatePicker()
        }
    }

    private fun openDatePicker() {
        if (dueDate == null)
            dueDate = LocalDate.now()

        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                dueDate = LocalDate.of(year, month + 1, dayOfMonth)
                updateDateButtonText()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.setTitle("Task Due Date")
        datePickerDialog.show()
    }

    private fun updateDateButtonText() {
        binding.datePickerButton.text = dueDate.toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewTaskSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun saveAction() {
        val name = binding.name.text.toString()
        val desc = binding.desc.text.toString()
        val category = binding.categorySpinner.selectedItem.toString()
        val dueDateString = dueDate?.toString()
        if (taskItem == null) {
            val newTask = TaskItem(name, desc, category, dueDateString, null)
            taskViewModel.addTaskItem(newTask)
        } else {
            taskItem!!.name = name
            taskItem!!.desc = desc
            taskItem!!.category = category
            taskItem!!.dueDateString = dueDateString
            taskViewModel.updateTaskItem(taskItem!!)
        }
        binding.name.setText("")
        binding.desc.setText("")
        dismiss()
    }
}
