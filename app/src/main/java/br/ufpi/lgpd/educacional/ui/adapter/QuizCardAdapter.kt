package br.ufpi.lgpd.educacional.ui.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.ufpi.lgpd.educacional.data.model.Quiz
import br.ufpi.lgpd.educacional.databinding.ItemQuizCardBinding

/**
 * Adapter para lista de quizzes em formato de card
 */
class QuizCardAdapter(
    private val onQuizClick: (Quiz) -> Unit
) : ListAdapter<Quiz, QuizCardAdapter.QuizViewHolder>(QuizDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val binding = ItemQuizCardBinding.inflate(
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
        private val binding: ItemQuizCardBinding,
        private val onQuizClick: (Quiz) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(quiz: Quiz) {
            binding.apply {
                quizTitle.text = quiz.title
                
                quizQuestionsCount.text = "${quiz.totalQuestions} Questões"
                
                val (difficultyLabel, difficultyColor) = when (quiz.difficulty) {
                    "BEGINNER" -> "Iniciante" to Color.parseColor("#0F766E")
                    "INTERMEDIATE" -> "Intermediário" to Color.parseColor("#D97706")
                    "ADVANCED" -> "Avançado" to Color.parseColor("#DC2626")
                    else -> quiz.difficulty to Color.parseColor("#64748B")
                }
                quizCategoryChip.backgroundTintList = ColorStateList.valueOf(difficultyColor)
                quizCategoryChip.text = difficultyLabel
                
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
