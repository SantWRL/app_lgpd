package br.ufpi.lgpd.educacional.ui.feed

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.ufpi.lgpd.educacional.R
import br.ufpi.lgpd.educacional.databinding.ItemFeedPostCompactBinding

/**
 * Compact adapter for horizontal news carousel on Home screen.
 * Suporta um callback de retry para recarregar notícias em caso de erro.
 */
class FeedCompactAdapter(
    private val onRetryClick: (() -> Unit)? = null
) : ListAdapter<FeedPost, FeedCompactAdapter.FeedViewHolder>(FeedDiffCallback()) {

    private fun openUrlSafely(view: View, url: String?) {
        val fallback = "https://www.gov.br/anpd/pt-br/assuntos/noticias"
        val target = url ?: fallback
        runCatching {
            view.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(target)))
        }.onFailure {
            view.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(fallback)))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val binding = ItemFeedPostCompactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedViewHolder(binding, onRetryClick)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FeedViewHolder(
        private val binding: ItemFeedPostCompactBinding,
        private val onRetryClick: (() -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: FeedPost) {
            binding.apply {
                postAuthorName.text = post.authorName
                postAvatarInitial.text = post.authorInitials
                postTime.text = post.timeAgo
                postContent.text = post.content
                postCommentsCount.text = post.commentsCount.toString()
                postRepostsCount.text = post.repostsCount.toString()
                postLikesCount.text = post.likesCount.toString()

                if (post.linkTitle != null) {
                    postLinkCard.visibility = View.VISIBLE
                    postLinkTitle.text = post.linkTitle

                    // Se o post for de erro (id == -2), o link dispara retry
                    if (post.id == -2 && onRetryClick != null) {
                        postLinkCard.setOnClickListener { onRetryClick.invoke() }
                        root.setOnClickListener { onRetryClick.invoke() }
                        postContent.setOnClickListener { onRetryClick.invoke() }
                    } else {
                        val openLink = {
                            openUrlSafely(root, post.linkUrl)
                        }
                        postLinkCard.setOnClickListener { openLink() }
                        root.setOnClickListener { openLink() }
                        postContent.setOnClickListener { openLink() }
                    }
                } else {
                    postLinkCard.visibility = View.GONE
                    root.setOnClickListener {
                        openUrlSafely(it, post.linkUrl)
                    }
                    postContent.setOnClickListener {
                        openUrlSafely(it, post.linkUrl)
                    }
                }

                updateLikeUi(post)

                postLikeIcon.setOnClickListener {
                    post.isLiked = !post.isLiked
                    post.likesCount += if (post.isLiked) 1 else -1
                    updateLikeUi(post)
                }
            }
        }

        private fun updateLikeUi(post: FeedPost) {
            binding.postLikesCount.text = post.likesCount.toString()
            if (post.isLiked) {
                binding.postLikeIcon.setColorFilter(binding.root.context.getColor(R.color.error))
            } else {
                binding.postLikeIcon.setColorFilter(binding.root.context.getColor(R.color.text_tertiary))
            }
        }
    }

    class FeedDiffCallback : DiffUtil.ItemCallback<FeedPost>() {
        override fun areItemsTheSame(oldItem: FeedPost, newItem: FeedPost) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: FeedPost, newItem: FeedPost) = oldItem == newItem
    }
}
