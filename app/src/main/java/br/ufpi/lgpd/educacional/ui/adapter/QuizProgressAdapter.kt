package br.ufpi.lgpd.educacional.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.ufpi.lgpd.educacional.R
import br.ufpi.lgpd.educacional.databinding.ItemQuizProgressBinding
import br.ufpi.lgpd.educacional.ui.profile.QuizProgressItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class QuizProgressAdapter(
    private val onItemClick: (QuizProgressItem) -> Unit
) : ListAdapter<QuizProgressItem, QuizProgressAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemQuizProgressBinding.inflate(
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
        private val binding: ItemQuizProgressBinding,
        private val onItemClick: (QuizProgressItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: QuizProgressItem) {
            val context = binding.root.context
            binding.apply {
                quizTitle.text = item.title
                categoryBadge.text = item.category
                difficultyBadge.text = formatDifficulty(item.difficulty)
                setDifficultyStyle(difficultyBadge, item.difficulty)

                root.setOnClickListener { onItemClick(item) }

                if (item.isCompleted && item.bestScore != null) {
                    statusIcon.text = "✓"
                    statusIcon.setTextColor(ContextCompat.getColor(context, R.color.success))
                    statusIconFrame.backgroundTintList =
                        ContextCompat.getColorStateList(context, R.color.success_light)

                    scoreText.text = "${item.bestScore}%"

                    // Color score based on performance
                    val scoreColor = when {
                        item.bestScore >= 90 -> ContextCompat.getColor(context, R.color.success)
                        item.bestScore >= 70 -> ContextCompat.getColor(context, R.color.primary)
                        item.bestScore >= 50 -> ContextCompat.getColor(context, R.color.accent)
                        else -> ContextCompat.getColor(context, R.color.error)
                    }
                    scoreText.setTextColor(scoreColor)

                    completedDate.text = if (item.completedAt != null) {
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        "Concluído em ${sdf.format(Date(item.completedAt))}"
                    } else {
                        "Concluído"
                    }
                } else {
                    statusIcon.text = "○"
                    statusIcon.setTextColor(ContextCompat.getColor(context, R.color.text_tertiary))
                    statusIconFrame.backgroundTintList =
                        ContextCompat.getColorStateList(context, R.color.surface_variant)

                    scoreText.text = "--"
                    scoreText.setTextColor(ContextCompat.getColor(context, R.color.text_tertiary))

                    completedDate.text = "${item.totalQuestions} perguntas"
                }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<QuizProgressItem>() {
        override fun areItemsTheSame(oldItem: QuizProgressItem, newItem: QuizProgressItem) =
            oldItem.quizId == newItem.quizId

        override fun areContentsTheSame(oldItem: QuizProgressItem, newItem: QuizProgressItem) =
            oldItem == newItem
    }
}
