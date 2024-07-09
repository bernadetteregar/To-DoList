package com.example.todolisttutorial

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Entity(tableName = "task_item_table")
class TaskItem(
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "desc") var desc: String,
    @ColumnInfo(name = "category") var category: String,
    @ColumnInfo(name = "dueDateString") var dueDateString: String?,
    @ColumnInfo(name = "completedDateString") var completedDateString: String?,
    @PrimaryKey(autoGenerate = true) var id: Int = 0
)
{
    fun completedDate(): LocalDate? = if (completedDateString == null) null
    else LocalDate.parse(completedDateString, dateFormatter)

    fun dueDate(): LocalDate? = if (dueDateString == null) null
    else LocalDate.parse(dueDateString, dateFormatter)

    fun isCompleted() = completedDate() != null
    fun imageResource(): Int = if(isCompleted()) R.drawable.checked_24 else R.drawable.unchecked_24
    fun imageColor(context: Context): Int = if(isCompleted()) blue(context) else black(context)

    private fun blue(context: Context) = ContextCompat.getColor(context, R.color.colorPrimary)
    private fun black(context: Context) = ContextCompat.getColor(context, R.color.black)

    companion object{
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_DATE
    }
}

