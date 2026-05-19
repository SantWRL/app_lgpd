package br.ufpi.lgpd.educacional.ui.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.ufpi.lgpd.educacional.data.model.Lesson
import br.ufpi.lgpd.educacional.databinding.ItemLessonCardBinding

/**
 * Adapter para lista de lições em formato de card
 */
class LessonCardAdapter(
    private val onLessonClick: (Lesson) -> Unit
) : ListAdapter<Lesson, LessonCardAdapter.LessonViewHolder>(LessonDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder {
        val binding = ItemLessonCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LessonViewHolder(binding, onLessonClick)
    }

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class LessonViewHolder(
        private val binding: ItemLessonCardBinding,
        private val onLessonClick: (Lesson) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(lesson: Lesson) {
            binding.apply {
                lessonTitle.text = lesson.title
                
                lessonDuration.text = "${lesson.estimatedTime} min"
                
                val (difficultyLabel, difficultyColor) = when (lesson.difficulty) {
                    "BEGINNER" -> "Iniciante" to Color.parseColor("#0F766E")
                    "INTERMEDIATE" -> "Intermediário" to Color.parseColor("#D97706")
                    "ADVANCED" -> "Avançado" to Color.parseColor("#DC2626")
                    else -> lesson.difficulty to Color.parseColor("#64748B")
                }
                lessonDifficultyChip.backgroundTintList = ColorStateList.valueOf(difficultyColor)
                lessonDifficultyChip.text = difficultyLabel
                
                root.setOnClickListener {
                    onLessonClick(lesson)
                }
            }
        }
    }

    private class LessonDiffCallback : DiffUtil.ItemCallback<Lesson>() {
        override fun areItemsTheSame(oldItem: Lesson, newItem: Lesson) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Lesson, newItem: Lesson) = oldItem == newItem
    }
}
