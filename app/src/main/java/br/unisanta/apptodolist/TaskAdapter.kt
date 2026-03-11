package br.unisanta.apptodolist

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskAdapter(
    private val tasks: MutableList<Task>,
    private val onCompleteClick: (Task, Int) -> Unit,
    private val onDeleteClick: (Task, Int) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.cardView)
        val tvTaskName: TextView = itemView.findViewById(R.id.tvTaskName)
        val tvTaskDescription: TextView = itemView.findViewById(R.id.tvTaskDescription)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val tvCreatedAt: TextView = itemView.findViewById(R.id.tvCreatedAt)
        val btnComplete: Button = itemView.findViewById(R.id.btnComplete)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
        val statusIndicator: View = itemView.findViewById(R.id.statusIndicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        val ctx = holder.itemView.context

        holder.tvTaskName.text = task.name
        holder.tvTaskDescription.text = task.description
        holder.tvCreatedAt.text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(task.createdAt))

        if (task.status == TaskStatus.COMPLETED) {
            holder.tvStatus.text = ctx.getString(R.string.status_completed)
            holder.tvStatus.setTextColor(ContextCompat.getColor(ctx, R.color.completed_color))
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_completed)
            holder.statusIndicator.setBackgroundColor(ContextCompat.getColor(ctx, R.color.completed_color))
            holder.tvTaskName.paintFlags = holder.tvTaskName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.tvTaskName.alpha = 0.5f
            holder.tvTaskDescription.alpha = 0.4f
            holder.btnComplete.visibility = View.GONE
            holder.cardView.alpha = 0.85f
        } else {
            holder.tvStatus.text = ctx.getString(R.string.status_pending)
            holder.tvStatus.setTextColor(ContextCompat.getColor(ctx, R.color.pending_color))
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_pending)
            holder.statusIndicator.setBackgroundColor(ContextCompat.getColor(ctx, R.color.pending_color))
            holder.tvTaskName.paintFlags = holder.tvTaskName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.tvTaskName.alpha = 1f
            holder.tvTaskDescription.alpha = 1f
            holder.btnComplete.visibility = View.VISIBLE
            holder.cardView.alpha = 1f
        }

        holder.btnComplete.setOnClickListener {
            val sx = ObjectAnimator.ofFloat(holder.cardView, "scaleX", 1f, 0.95f, 1f)
            val sy = ObjectAnimator.ofFloat(holder.cardView, "scaleY", 1f, 0.95f, 1f)
            AnimatorSet().apply { playTogether(sx, sy); duration = 200; start() }
            holder.cardView.postDelayed({ onCompleteClick(task, holder.adapterPosition) }, 150)
        }

        holder.btnDelete.setOnClickListener {
            holder.itemView.animate()
                .translationX(holder.itemView.width.toFloat())
                .alpha(0f).setDuration(280)
                .withEndAction { onDeleteClick(task, holder.adapterPosition) }
                .start()
        }

        holder.itemView.translationX = -50f
        holder.itemView.alpha = 0f
        holder.itemView.animate()
            .translationX(0f).alpha(1f).setDuration(300)
            .setInterpolator(DecelerateInterpolator())
            .setStartDelay((position * 60L).coerceAtMost(300L))
            .start()
    }

    override fun getItemCount() = tasks.size
}
