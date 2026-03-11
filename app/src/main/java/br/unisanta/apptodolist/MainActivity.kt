package br.unisanta.apptodolist

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var tvEmptyState: TextView
    private lateinit var tvPendingCount: TextView
    private lateinit var tvCompletedCount: TextView
    private val taskList = mutableListOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        setupRecyclerView()
        updateCounters()
        updateEmptyState()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerView)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        tvPendingCount = findViewById(R.id.tvPendingCount)
        tvCompletedCount = findViewById(R.id.tvCompletedCount)
        val fab: FloatingActionButton = findViewById(R.id.fabAddTask)
        fab.setOnClickListener {
            it.animate().rotationBy(360f).setDuration(400).start()
            showAddTaskDialog()
        }
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            tasks = taskList,
            onCompleteClick = { task, _ -> markTaskAsCompleted(task) },
            onDeleteClick = { task, _ -> deleteTask(task) }
        )
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = taskAdapter
            itemAnimator = null
        }
    }

    private fun showAddTaskDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_add_task)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.92).toInt(),
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val etName: EditText = dialog.findViewById(R.id.etTaskName)
        val etDesc: EditText = dialog.findViewById(R.id.etTaskDescription)
        val btnSave: Button = dialog.findViewById(R.id.btnSave)
        val btnCancel: Button = dialog.findViewById(R.id.btnCancel)

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val desc = etDesc.text.toString().trim()
            when {
                name.isEmpty() -> { etName.error = getString(R.string.error_name_required); etName.requestFocus() }
                desc.isEmpty() -> { etDesc.error = getString(R.string.error_desc_required); etDesc.requestFocus() }
                else -> { addTask(name, desc); dialog.dismiss() }
            }
        }
        btnCancel.setOnClickListener { dialog.dismiss() }
        dialog.window?.decorView?.startAnimation(AnimationUtils.loadAnimation(this, R.anim.dialog_enter))
        dialog.show()
    }

    private fun addTask(name: String, description: String) {
        taskList.add(0, Task(name = name, description = description))
        taskAdapter.notifyItemInserted(0)
        recyclerView.smoothScrollToPosition(0)
        updateEmptyState()
        updateCounters()
        Toast.makeText(this, getString(R.string.task_added), Toast.LENGTH_SHORT).show()
    }

    private fun markTaskAsCompleted(task: Task) {
        val i = taskList.indexOfFirst { it.id == task.id }
        if (i != -1) {
            taskList[i].status = TaskStatus.COMPLETED
            taskAdapter.notifyItemChanged(i)
            updateCounters()
            Toast.makeText(this, getString(R.string.task_completed), Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteTask(task: Task) {
        val i = taskList.indexOfFirst { it.id == task.id }
        if (i != -1) {
            taskList.removeAt(i)
            taskAdapter.notifyItemRemoved(i)
            updateEmptyState()
            updateCounters()
            Toast.makeText(this, getString(R.string.task_deleted), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateEmptyState() {
        if (taskList.isEmpty()) {
            tvEmptyState.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            tvEmptyState.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun updateCounters() {
        tvPendingCount.text = taskList.count { it.status == TaskStatus.PENDING }.toString()
        tvCompletedCount.text = taskList.count { it.status == TaskStatus.COMPLETED }.toString()
    }
}
