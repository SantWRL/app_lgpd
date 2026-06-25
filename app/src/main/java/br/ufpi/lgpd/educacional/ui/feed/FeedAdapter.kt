package br.ufpi.lgpd.educacional.ui.feed

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.ufpi.lgpd.educacional.R
import br.ufpi.lgpd.educacional.databinding.ItemFeedPostBinding

class FeedAdapter(
    private val onCategoryClick: ((String) -> Unit)? = null
) : ListAdapter<FeedPost, FeedAdapter.FeedViewHolder>(FeedDiffCallback()) {

    private fun openUrlSafely(view: View, url: String?) {
        val fallback = "https://www.gov.br/anpd/pt-br/assuntos/noticias"
        val target = url ?: fallback
        runCatching {
            view.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(target)))
        }.onFailure {
            view.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(fallback)))
        }
    }

    companion object {
        private val categoryColors = mapOf(
            "Fiscalização" to R.color.error,
            "Direitos" to R.color.success,
            "Regulatório" to R.color.primary,
            "Internacional" to R.color.accent,
            "Educação" to R.color.warning
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val binding = ItemFeedPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FeedViewHolder(private val binding: ItemFeedPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: FeedPost) {
            binding.apply {
                postAuthorName.text = post.authorName
                postAuthorUsername.text = post.authorUsername
                postAvatarInitial.text = post.authorInitials
                postTime.text = post.timeAgo
                postContent.text = post.content

                postCommentsCount.text = post.commentsCount.toString()
                postRepostsCount.text = post.repostsCount.toString()
                postLikesCount.text = post.likesCount.toString()

                // ── Category Badge ──
                val catColor = categoryColors[post.category] ?: R.color.primary
                postCategoryBadge.text = post.category.uppercase()
                postCategoryBadge.backgroundTintList =
                    ContextCompat.getColorStateList(root.context, catColor)

                // Category badge click: filter by this category
                postCategoryBadge.setOnClickListener {
                    onCategoryClick?.invoke(post.category)
                }

                // ── Link Card ──
                if (post.linkTitle != null) {
                    postLinkCard.visibility = View.VISIBLE
                    postLinkTitle.text = post.linkTitle
                } else {
                    postLinkCard.visibility = View.GONE
                }

                // ── OnClick: abrir notícia no navegador (clicando em qualquer lugar do card) ──
                val openUrlIntent = {
                    openUrlSafely(root, post.linkUrl)
                }
                root.setOnClickListener { openUrlIntent() }
                postLinkCard.setOnClickListener { openUrlIntent() }
                postContent.setOnClickListener { openUrlIntent() }

                // ── Like ──
                updateLikeUi(post)
                postLikeIcon.setOnClickListener {
                    post.isLiked = !post.isLiked
                    post.likesCount += if (post.isLiked) 1 else -1
                    updateLikeUi(post)
                }

                // ── Share ──
                actionShare.setOnClickListener {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "${post.content}\n\n${post.linkUrl ?: "https://www.gov.br/anpd"}")
                    }
                    it.context.startActivity(
                        Intent.createChooser(shareIntent, "Compartilhar notícia")
                    )
                }
            }
        }

        private fun updateLikeUi(post: FeedPost) {
            binding.postLikesCount.text = post.likesCount.toString()
            if (post.isLiked) {
                binding.postLikeIcon.setColorFilter(
                    ContextCompat.getColor(binding.root.context, R.color.error)
                )
            } else {
                binding.postLikeIcon.setColorFilter(
                    ContextCompat.getColor(binding.root.context, R.color.text_tertiary)
                )
            }
        }
    }

    class FeedDiffCallback : DiffUtil.ItemCallback<FeedPost>() {
        override fun areItemsTheSame(oldItem: FeedPost, newItem: FeedPost): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FeedPost, newItem: FeedPost): Boolean {
            return oldItem == newItem
        }
    }
}
