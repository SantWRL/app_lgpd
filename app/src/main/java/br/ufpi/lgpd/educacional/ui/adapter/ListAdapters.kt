package br.ufpi.lgpd.educacional.ui.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.ufpi.lgpd.educacional.data.model.Lesson
import br.ufpi.lgpd.educacional.data.model.Quiz
import br.ufpi.lgpd.educacional.databinding.ItemLessonListBinding
import br.ufpi.lgpd.educacional.databinding.ItemQuizListBinding

/**
 * Adapter para lista completa de lições
 */
class LessonsListAdapter(
    private val onLessonClick: (Lesson) -> Unit
) : ListAdapter<Lesson, LessonsListAdapter.LessonViewHolder>(LessonDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder {
        val binding = ItemLessonListBinding.inflate(
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
        private val binding: ItemLessonListBinding,
        private val onLessonClick: (Lesson) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(lesson: Lesson) {
            binding.apply {
                lessonNumber.text = lesson.orderIndex.toString()
                lessonTitle.text = lesson.title
                lessonDescription.text = lesson.description
                lessonTime.text = "${lesson.estimatedTime} min"
                
                val (difficultyLabel, difficultyColor) = when (lesson.difficulty) {
                    "BEGINNER" -> "Iniciante" to Color.parseColor("#0F766E")
                    "INTERMEDIATE" -> "Intermediário" to Color.parseColor("#D97706")
                    "ADVANCED" -> "Avançado" to Color.parseColor("#DC2626")
                    else -> lesson.difficulty to Color.parseColor("#64748B")
                }
                difficultyBadge.backgroundTintList = ColorStateList.valueOf(difficultyColor)
                difficultyBadge.text = difficultyLabel
                
                // Mostrar status de conclusão
                completedIcon.visibility = if (lesson.isCompleted) View.VISIBLE else View.GONE
                
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

/**
 * Adapter para lista completa de quizzes
 */
class QuizzesListAdapter(
    private val onQuizClick: (Quiz) -> Unit
) : ListAdapter<Quiz, QuizzesListAdapter.QuizViewHolder>(QuizDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val binding = ItemQuizListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return QuizViewHolder(binding, onQuizClick)
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class QuizViewHolder(
        private val binding: ItemQuizListBinding,
        private val onQuizClick: (Quiz) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(quiz: Quiz) {
            binding.apply {
                quizTitle.text = quiz.title
                quizQuestions.text = "${quiz.totalQuestions} perguntas"
                quizCategory.text = quiz.category
                
                val (difficultyLabel, difficultyColor) = when (quiz.difficulty) {
                    "BEGINNER" -> "Iniciante" to Color.parseColor("#0F766E")
                    "INTERMEDIATE" -> "Intermediário" to Color.parseColor("#D97706")
                    "ADVANCED" -> "Avançado" to Color.parseColor("#DC2626")
                    else -> quiz.difficulty to Color.parseColor("#64748B")
                }
                difficultyBadge.backgroundTintList = ColorStateList.valueOf(difficultyColor)
                difficultyBadge.text = difficultyLabel

                completedIcon.visibility = if (quiz.isCompleted) View.VISIBLE else View.GONE
                
                root.setOnClickListener {
                    onQuizClick(quiz)
                }
            }
        }
    }

    private class QuizDiffCallback : DiffUtil.ItemCallback<Quiz>() {
        override fun areItemsTheSame(oldItem: Quiz, newItem: Quiz) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Quiz, newItem: Quiz) = oldItem == newItem
    }
}
