package br.ufpi.lgpd.educacional.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.ufpi.lgpd.educacional.R
import br.ufpi.lgpd.educacional.databinding.ItemLessonProgressBinding
import br.ufpi.lgpd.educacional.ui.profile.LessonProgressItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LessonProgressAdapter(
    private val onItemClick: (LessonProgressItem) -> Unit
) : ListAdapter<LessonProgressItem, LessonProgressAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLessonProgressBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemLessonProgressBinding,
        private val onItemClick: (LessonProgressItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LessonProgressItem) {
            val context = binding.root.context
            binding.apply {
                lessonTitle.text = item.title
                categoryBadge.text = item.category
                difficultyBadge.text = formatDifficulty(item.difficulty)
                setDifficultyStyle(difficultyBadge, item.difficulty)

                root.setOnClickListener { onItemClick(item) }

                if (item.isCompleted) {
                    statusIcon.text = "✓"
                    statusIcon.setTextColor(ContextCompat.getColor(context, R.color.success))
                    statusIconFrame.backgroundTintList =
                        ContextCompat.getColorStateList(context, R.color.success_light)

                    completedDate.text = if (item.completedAt != null) {
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        "Concluída em ${sdf.format(Date(item.completedAt))}"
                    } else {
                        "Concluída"
                    }
                } else {
                    statusIcon.text = "○"
                    statusIcon.setTextColor(ContextCompat.getColor(context, R.color.text_tertiary))
                    statusIconFrame.backgroundTintList =
                        ContextCompat.getColorStateList(context, R.color.surface_variant)

                    completedDate.text = "Pendente"
                }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<LessonProgressItem>() {
        override fun areItemsTheSame(oldItem: LessonProgressItem, newItem: LessonProgressItem) =
            oldItem.lessonId == newItem.lessonId

        override fun areContentsTheSame(oldItem: LessonProgressItem, newItem: LessonProgressItem) =
            oldItem == newItem
    }
}

internal fun formatDifficulty(difficulty: String): String = when (difficulty) {
    "BEGINNER" -> "INICIANTE"
    "INTERMEDIATE" -> "INTERMEDIÁRIO"
    "ADVANCED" -> "AVANÇADO"
    else -> difficulty
}

internal fun setDifficultyStyle(textView: android.widget.TextView, difficulty: String) {
    val context = textView.context
    val (color, bgColor) = when (difficulty) {
        "BEGINNER" -> R.color.success to R.color.success_light
        "INTERMEDIATE" -> R.color.primary to R.color.primary_light
        "ADVANCED" -> R.color.accent to R.color.accent_light
        else -> R.color.text_tertiary to R.color.surface_variant
    }
    textView.setTextColor(ContextCompat.getColor(context, color))
    textView.backgroundTintList = ContextCompat.getColorStateList(context, bgColor)
}
