package br.ufpi.lgpd.educacional.ui.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.ufpi.lgpd.educacional.R
import br.ufpi.lgpd.educacional.databinding.ItemAchievementBinding
import br.ufpi.lgpd.educacional.ui.profile.AchievementItem

class AchievementAdapter :
    ListAdapter<AchievementItem, AchievementAdapter.AchievementViewHolder>(AchievementDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val binding = ItemAchievementBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AchievementViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AchievementViewHolder(
        private val binding: ItemAchievementBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(achievement: AchievementItem) {
            val context = binding.root.context
            val badgeTint = if (achievement.isUnlocked) R.color.accent_light else R.color.surface_variant
            val statusText = if (achievement.isUnlocked) "✓ Desbloqueada" else "Em progresso"
            val statusColor = if (achievement.isUnlocked) R.color.success else R.color.text_tertiary

            binding.achievementName.text = achievement.title
            binding.achievementDescription.text = achievement.description
            binding.achievementStatus.text = statusText
            binding.achievementStatus.setTextColor(ContextCompat.getColor(context, statusColor))
            binding.achievementEmoji.text = achievement.emoji
            binding.achievementIconContainer.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(context, badgeTint)
            )
            binding.root.alpha = if (achievement.isUnlocked) 1f else 0.68f
        }
    }

    private class AchievementDiffCallback : DiffUtil.ItemCallback<AchievementItem>() {
        override fun areItemsTheSame(oldItem: AchievementItem, newItem: AchievementItem) =
            oldItem.title == newItem.title

        override fun areContentsTheSame(oldItem: AchievementItem, newItem: AchievementItem) =
            oldItem == newItem
    }
}
